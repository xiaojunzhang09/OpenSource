package com.ibeetl.code.ch03.mutiple;


import com.ibeetl.code.ch03.mutiple.reject.MutiDiscardOldestPolicy;
import com.ibeetl.code.ch03.mutiple.simple.SimpleQosPolicy;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiBlockingQueue<E> implements BlockingQueue<E>, QosQueue<E> {

	Map<String, QosQueueWrapper> map = new HashMap<>();
	QosQueueWrapper[] wrappers;

	QosPolicy<E> qosPolicy = null;

	ReentrantLock lock = new ReentrantLock();

	//计数
	AtomicInteger total = new AtomicInteger();
	//队列总数
	int totalSize;

	/** Condition for waiting takes */
	private final Condition notEmpty;

	/** Condition for waiting puts */
	private final Condition notFull;


	/**
	 * 所有队列的总容量，应该为各个容量之和
	 * @param totalSize 总容量，应该小于或者等于各个队列实际容量之和
	 * @param wrappers
	 */
	public MultiBlockingQueue(int totalSize, QosQueueWrapper... wrappers) {
		this(totalSize, new SimpleQosPolicy(), wrappers);

	}


	public MultiBlockingQueue(int totalSize, QosPolicy qosPolicy, QosQueueWrapper... wrappers) {
		for (QosQueueWrapper wrapper : wrappers) {
			map.put(wrapper.id, wrapper);
		}
		this.wrappers = wrappers;
		this.totalSize = totalSize;
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
		validate();
		this.qosPolicy = qosPolicy;
		qosPolicy.init(wrappers);

	}


	protected void validate() {
		int total = 0;
		for (QosQueueWrapper wrapper : wrappers) {
			BlockingQueue queue = wrapper.getBlockingQueue();
			if (queue.size() != 0) {
				throw new IllegalArgumentException("queue " + wrapper.getId() + " is not empty");
			}
			total = total + queue.remainingCapacity();
		}
		if (total < totalSize) {
			throw new IllegalArgumentException(" totalSize=" + totalSize + " large than real capacity " + total);
		}
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void addQosQueue(String id, Qos qos, int size, BlockingQueue blockingQueue) {
		lock.lock();

		try {
			QosQueueWrapper wrapper = new QosQueueWrapper(id, qos, blockingQueue);
			totalSize = totalSize + size;
			map.put(id, wrapper);
			List<QosQueueWrapper> list = Arrays.asList(wrappers);
			list.add(wrapper);
			this.wrappers = list.toArray(new QosQueueWrapper[0]);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void modifyQos(String id, Qos qos) {
		lock.lock();
		try {
			QosQueueWrapper queue = map.get(id);
			queue.qos = qos;
			qosPolicy.modify(queue, qos);

		} finally {
			lock.unlock();
		}
	}

	/**
	 * thread pool 调用
	 * @param e
	 * @return
	 */
	@Override
	public boolean offer(E e) {
		QosQueueWrapper[] temps = this.wrappers;
		lock.lock();
		try {
			if (total.get() == totalSize) {
				return false;
			}
			Task task = getTaskId(e);
			QosQueueWrapper queueWrapper = map.get(task.getId());
			if (queueWrapper == null) {
				throw new IllegalStateException("can not find " + task.getId() + " queue");
			}
			boolean success = queueWrapper.blockingQueue.offer(e);
			if (success) {
				total.incrementAndGet();
				qosPolicy.offer(task.getId());
				notEmpty.signal();
			}
			return success;
		} catch (Throwable error) {
			error.printStackTrace();
			return false;
		} finally {
			lock.unlock();
		}


	}

	@Override
	public E remove() {
		throw new UnsupportedOperationException();
	}

	protected Task getTaskId(E e) {
		ThreadQosPoolExecutor.MutilFutureTask mtask = (ThreadQosPoolExecutor.MutilFutureTask) e;
		return mtask.getTask();
	}

	/**
	 * 用于reject，必须指定一个queue,tryPoll方法允许删除低优先级队列
	 * @param index
	 * @return
	 * @see MutiDiscardOldestPolicy
	 */
	public E poll(String index) {
		lock.lock();
		try {
			E e = this.qosPolicy.discardOne(index);
			if (e != null) {
				//不可能为null
				total.decrementAndGet();
				notFull.signal();
			}
			return e;
		} finally {
			lock.unlock();
		}

	}


	@Override
	public E poll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E element() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E peek() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(E e) throws InterruptedException {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	/**
	 * thread pool 调用
	 * 取出一个可用值，如果被取出队列是未得到允许，则循环到下一个队列
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public E take() throws InterruptedException {
		lock.lockInterruptibly();
		try {

			while (total.get() == 0) {
				notEmpty.await();
			}
			E e = takeOne();
			notFull.signal();
			return e;
		} catch (InterruptedException error) {
			//shutdown ,see ThreadPoolExecutor.interruptWorkers
			//            System.out.println("interruptWorkers "+Thread.currentThread());
			return null;
		} finally {
			lock.unlock();
		}


	}


	/**
	 * thread pool 调用
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		lock.lockInterruptibly();
		try {
			while (isEmpty()) {
				if (nanos <= 0)
					return null;
				nanos = notEmpty.awaitNanos(nanos);
			}
			E e = takeOne();
			notFull.signal();
			return e;
		} catch (InterruptedException error) {
			return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 从多个队列中取出一个符合qos的元素，总是优先取出高qos。如果没有，则取出低qos（低qos在循环中自增计数从而达到qos）
	 * @return
	 */
	protected E takeOne() {
		E e = qosPolicy.takeOne();
		if (e != null) {
			total.decrementAndGet();
		}
		return e;
	}

	@Override
	public int remainingCapacity() {
		lock.lock();
		int remain = 0;
		try {
			for (QosQueueWrapper wrapper : this.wrappers) {
				remain = remain + wrapper.getBlockingQueue().remainingCapacity();
			}
			return remain;
		} finally {
			lock.unlock();
		}
	}


	/**
	 * thread pool 调用
	 * @param o
	 * @return
	 */
	@Override
	public boolean remove(Object o) {

		lock.lock();
		boolean isRemove = false;
		try {
			Task task = (Task) o;
			String id = task.getId();
			QosQueueWrapper wrapper = map.get(id);
			isRemove = wrapper.blockingQueue.remove(o);
			if (isRemove) {
				total.decrementAndGet();

			}
			return isRemove;
		} finally {
			notFull.signal();
			lock.unlock();
		}

	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		lock.lock();
		int nowTotal = total.get();
		try {
			for (QosQueueWrapper wrapper : this.wrappers) {
				wrapper.getBlockingQueue().clear();
			}
			total.set(0);
		} finally {
			if (nowTotal != 0) {
				notFull.signal();
			}
			lock.unlock();
		}
	}

	@Override
	public void clear(String id) {
		lock.lock();
		BlockingQueue blockingQueue = null;
		int size = 0;
		try {
			blockingQueue = map.get(id).blockingQueue;
			size = blockingQueue.size();
			blockingQueue.clear();
			total.addAndGet(-size);
		} finally {
			if (size != 0) {
				notFull.signal();
			}
			lock.unlock();
		}

	}

	@Override
	public void setQosPolicy(QosPolicy qosPolicy) {
		this.qosPolicy = qosPolicy;
	}

	@Override
	public QosPolicy getQosPolicy() {
		return this.qosPolicy;
	}

	/**
	 * thread pool 调用
	 * @return
	 */
	@Override
	public int size() {
		lock.lock();
		try {
			return total.get();
		} finally {
			lock.unlock();
		}

	}

	/**
	 * threadpool调用
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
		//        return new Object[0];
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 偶尔导致死锁。当ThreadPoolExecutor执行此方法时候(shutdownNow)，应该释放了所有线程，lock没人用，
	 * 但偶尔情况下，所有线程还在等lock. 通过线程栈，发现lock在take方法中，很奇怪。take方法已经正确释放锁
	 * @param c
	 * @return
	 */
	@Override
	public int drainTo(Collection<? super E> c) {
		lock.lock();
		int total = 0;
		try {
			for (QosQueueWrapper wrapper : wrappers) {
				int size = wrapper.blockingQueue.size();
				total = total + size;
				wrapper.blockingQueue.drainTo(c);
				this.total.addAndGet(-size);
			}
		} finally {
			notFull.signal();//?
			lock.unlock();
		}
		return total;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		return map.toString();
	}


}
