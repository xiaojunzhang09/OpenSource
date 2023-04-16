package com.ibeetl.code.ch03.mutiple.simple;


import com.ibeetl.code.ch03.mutiple.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区分高优先级和低优先级，高优先级执行完毕后，低优先级才能执行。
 *
 * 在负荷满情况，低优先级的将被放弃
 *
 */
public class HighAlwaysPolicy implements QosPolicy {

	/**
	 * 界限，高于此的的总是高优先级
	 */
	int line;

	List<QosQueueWrapper> highList;
	int highIndex = 0;

	List<QosQueueWrapper> lowList;
	int lowIndex = 0;

	QosQueueWrapper[] wrappers = null;
	Map<String, QosQueueWrapper> map = new HashMap<>();

	Statistics defaultStatistics = new DefaultStatistics();

	public HighAlwaysPolicy(int line) {
		this.line = line;
	}


	@Override
	public void init(QosQueueWrapper[] wrappers) {

		this.wrappers = wrappers;
		highList = new ArrayList<>();
		lowList = new ArrayList<>();
		for (QosQueueWrapper wrapper : wrappers) {
			if (wrapper.getQos().getValue() >= line) {
				highList.add(wrapper);
			} else {
				lowList.add(wrapper);
			}
		}

		for (QosQueueWrapper queueWrapper : wrappers) {
			map.put(queueWrapper.getId(), queueWrapper);
		}

	}

	@Override
	public void modify(QosQueueWrapper wrapper, Qos qos) {
		List list = new ArrayList(highList);
		list.addAll(lowList);
		QosQueueWrapper[] wrappers = (QosQueueWrapper[]) list.toArray(new QosQueueWrapper[0]);
		init(wrappers);
	}

	@Override
	public Object takeOne() {
		Object o = takeFromHightList(highList);
		if (o != null) {

			return o;
		}
		o = takeFromLowList(lowList);
		return o;
	}

	@Override
	public Object discardOne(String id) {
		QosQueueWrapper wrapper = map.get(id);
		if (wrapper.getQos().getValue() >= line && wrapper.getBlockingQueue().remainingCapacity() != 0) {
			Object e = discardLow();
			return e;
		}
		Object e = wrapper.getBlockingQueue().poll();
		if (e != null) {
			defaultStatistics.discard(wrapper.getId(), wrapper.getQos());
			return e;
		}

		return e;
	}


	protected Object discardLow() {
		if (lowList.isEmpty()) {
			return null;
		}
		//TODO，放弃第一个，不太公平，改成随机？
		for (QosQueueWrapper wrapper : lowList) {
			if (!wrapper.getBlockingQueue().isEmpty()) {
				defaultStatistics.discard(wrapper.getId(), wrapper.getQos());
				return wrapper.getBlockingQueue().poll();
			}
		}
		return null;
	}

	protected Object pollFromList(List<QosQueueWrapper> list) {
		for (QosQueueWrapper queueWrapper : list) {
			if (!queueWrapper.getBlockingQueue().isEmpty()) {
				Object e = queueWrapper.getBlockingQueue().poll();
				defaultStatistics.discard(queueWrapper.getId(), queueWrapper.getQos());
				return e;
			}
		}
		return null;
	}

	@Override
	public Statistics getStatistics() {
		return this.defaultStatistics;
	}

	protected Object takeFromHightList(List<QosQueueWrapper> list) {
		if (list.isEmpty()) {
			return null;
		}
		int loop = 0;
		int size = list.size();
		while (loop < size) {

			QosQueueWrapper target = list.get(highIndex++);
			if (highIndex == highList.size()) {
				highIndex = 0;
			}
			if (!target.getBlockingQueue().isEmpty()) {
				defaultStatistics.add(target.getId(), target.getQos());
				return target.getBlockingQueue().poll();
			}
			loop++;
		}

		return null;
	}


	protected Object takeFromLowList(List<QosQueueWrapper> list) {
		if (list.isEmpty()) {
			return null;
		}
		int loop = 0;
		int size = list.size();
		while (loop < size) {

			QosQueueWrapper target = list.get(lowIndex++);
			if (lowIndex == list.size()) {
				lowIndex = 0;
			}

			if (!target.getBlockingQueue().isEmpty()) {
				defaultStatistics.add(target.getId(), target.getQos());
				return target.getBlockingQueue().poll();
			}
			loop++;
		}

		return null;
	}

	@Override
	public void setStatistics(Statistics stat) {
		this.defaultStatistics = stat;
	}

}
