package com.ibeetl.code.ch03.mutiple;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 实现多个优先级队列的队列
 * @param <E>
 */
public interface QosQueue<E> {
	/**
	 * 添加一个qos队列
	 * @param id
	 * @param qos
	 * @param size
	 * @param blockingQueue
	 */
	public void addQosQueue(String id, Qos qos, int size, BlockingQueue blockingQueue);

	/**
	 * 修改qos，比如根据网络拥塞情况，调整某队列的qos
	 * @param id
	 * @param qos
	 */
	public void modifyQos(String id, Qos qos);

	/**
	 * 提供一个任务，同BlockingQueue
	 * @param e
	 * @return
	 */
	public boolean offer(E e);

	/**
	 *  获取一个任务，如果没有任务，则等待，同BlockingQueue
	 * @return
	 * @throws InterruptedException
	 */
	public E take() throws InterruptedException;

	/**
	 * 获取一个任务，如果没有任务，则等待一定时间，同BlockingQueue
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public E poll(long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * 当线程池繁忙，任务无法执行，清除一个队列index的元素
	 * @param index 队列标记，来自于任务
	 * @return
	 * @see Task
	 */
	public E poll(String index);

	/**
	 * 当线程池繁忙，清除index指示队列的所有元素
	 * @param index
	 */
	public void clear(String index);


	public void setQosPolicy(QosPolicy qosPolicy);

	public QosPolicy getQosPolicy();

}
