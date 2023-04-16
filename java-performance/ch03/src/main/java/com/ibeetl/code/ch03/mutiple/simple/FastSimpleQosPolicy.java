package com.ibeetl.code.ch03.mutiple.simple;


import com.ibeetl.code.ch03.mutiple.Qos;
import com.ibeetl.code.ch03.mutiple.QosQueueWrapper;

import java.util.concurrent.BlockingQueue;

/**
 * 一个简单计数qos,如果takeOne第一次没有找到合适的队列，则直接从有原始的队列里取出
 * @param <T>
 */
public class FastSimpleQosPolicy<T> extends SimpleQosPolicy<T> {
	protected QosQueueWrapper last = null;

	@Override
	public T takeOne() {
		int totalLoop = 0;
		last = null;
		//按照Qos循环
		while (totalLoop < wrappers.length) {
			QosQueueWrapper queueWrapper = next();
			T e = takeFrom(queueWrapper);
			if (e != null) {
				return e;
			}
			totalLoop++;

		}

		if (last == null) {
			throw new IllegalStateException("不可能发生");
		}
		QosQueueWrapper queueWrapper = last;
		T e = (T) queueWrapper.getBlockingQueue().poll();
		defaultStatistics.add(queueWrapper.getId(), queueWrapper.getQos());
		return e;


	}

	@Override
	protected T takeFrom(QosQueueWrapper queueWrapper) {
		BlockingQueue queue = queueWrapper.getBlockingQueue();
		if (queue.isEmpty()) {
			return null;
		}

		Qos qos = queueWrapper.getQos();
		if (!qos.isAction()) {
			if (last == null) {
				last = queueWrapper;
			} else {
				if (queueWrapper.getQos().getValue() > last.getQos().getValue()) {
					last = queueWrapper;
				}
			}
			return null;
		}
		T e = (T) queue.poll();
		defaultStatistics.add(queueWrapper.getId(), qos);
		return e;


	}
}
