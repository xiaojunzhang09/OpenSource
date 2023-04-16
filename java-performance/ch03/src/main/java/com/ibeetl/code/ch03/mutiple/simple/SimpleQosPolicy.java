package com.ibeetl.code.ch03.mutiple.simple;

import com.ibeetl.code.ch03.mutiple.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 一个简单计数qos
 * @param <T>
 */
public class SimpleQosPolicy<T> implements QosPolicy<T> {
	QosQueueWrapper[] wrappers = null;
	//下一个队列的索引
	protected int index = 0;
	protected Statistics defaultStatistics = new DefaultStatistics();
	protected Map<String, QosQueueWrapper> map = new HashMap<>();

	@Override
	public void init(QosQueueWrapper[] wrappers) {
		this.wrappers = wrappers;
		for (QosQueueWrapper queueWrapper : wrappers) {
			map.put(queueWrapper.getId(), queueWrapper);
		}

	}

	@Override
	public void modify(QosQueueWrapper wrapper, Qos qos) {
		//SimpleQosPolicy 不需要
	}

	@Override
	public T takeOne() {
		int totalLoop = 0;
		int oldIndex = index;
		while (true) {
			//TODO,当循环一次后未找到符合条件的队列，下次循环应该直接跳到有元素的队列，取出元素
			while (totalLoop < wrappers.length) {
				QosQueueWrapper queueWrapper = next();
				T e = takeFrom(queueWrapper);
				if (e != null) {
					return e;
				}
				totalLoop++;

			}
			totalLoop = 0;
			index = oldIndex;
		}
	}

	protected QosQueueWrapper next() {
		QosQueueWrapper queueWrapper = this.wrappers[index];
		index++;
		if (index == wrappers.length) {
			index = 0;
		}
		return queueWrapper;
	}

	protected T takeFrom(QosQueueWrapper queueWrapper) {
		BlockingQueue queue = queueWrapper.getBlockingQueue();
		if (queue.isEmpty()) {
			return null;
		}
		Qos qos = queueWrapper.getQos();
		if (!qos.isAction()) {
			return null;
		}
		T e = (T) queue.poll();
		defaultStatistics.add(queueWrapper.getId(), qos);
		return e;


	}

	@Override
	public T discardOne(String id) {
		QosQueueWrapper queueWrapper = this.map.get(id);
		if (queueWrapper == null) {
			System.out.println(id);
		}
		Qos qos = queueWrapper.getQos();
		BlockingQueue queue = queueWrapper.getBlockingQueue();
		//队列是非低优先级，且还能放下新对象的时候，先删除低优先级的
		if (qos.isNormal() && queue.remainingCapacity() != 0) {
			//删除其他低优先级
			T e = tryPoll();
			if (e != null) {
				return e;
			}
		}
		T e = (T) queueWrapper.getBlockingQueue().poll();
		if (e != null) {
			defaultStatistics.discard(queueWrapper.getId(), queueWrapper.getQos());
		}
		return e;
	}

	/**
	 * 放弃一个低优先级的元素，子类可以实现自己的逻辑，目前是便利所有低优先级队列，如果有，则去除。
	 *
	 * @return
	 */
	protected T tryPoll() {
		for (QosQueueWrapper wrapper : wrappers) {
			if (wrapper.getQos().isNormal()) {
				continue;
			}
			if (!wrapper.getBlockingQueue().isEmpty()) {
				T e = (T) wrapper.getBlockingQueue().poll();
				defaultStatistics.discard(wrapper.getId(), wrapper.getQos());
				return e;
			}
		}
		return null;
	}

	@Override
	public Statistics getStatistics() {
		return defaultStatistics;
	}

	@Override
	public void setStatistics(Statistics stat) {
		this.defaultStatistics = stat;
	}
}
