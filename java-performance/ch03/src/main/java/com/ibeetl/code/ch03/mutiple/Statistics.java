package com.ibeetl.code.ch03.mutiple;

import java.util.Map;

/**
 * 一个qos统计类,线程安全，线程池应该保证线程安全下调用。
 * 统计成功和失败数目
 */
public interface Statistics {
	/**
	 * 添加一个元素，调用此方法，表示线程池获取到了一个任务，任务来自id以及当时的qos
	 * @param id
	 * @param qos
	 */
	void add(String id, Qos qos);

	/**
	 * 调用此方法，表示线程池获放弃了一个任务，任务来自队列id和当时的qos
	 * @param id
	 * @param qos
	 */
	void discard(String id, Qos qos);

	/**
	 * 总的成功数目
	 * @return
	 */
	default long totalSuccess() {
		long total = 0;
		for (long l : qosStatistics().values()) {
			total = total + l;
		}
		return total;
	}

	/**
	 * 总失败数目
	 * @return
	 */
	default long totalFailure() {
		long total = 0;
		for (long l : discardQosStatistics().values()) {
			total = total + l;
		}
		return total;
	}

	/**
	 * 统计 id的个数
	 * @return
	 */
	Map<String, Long> idStatistics();

	/**
	 * 统计qos的个数，qos的getValue一致的应该合并
	 * @return
	 */
	Map<Qos, Long> qosStatistics();

	/**
	 * 统计放弃的id的个数
	 * @return
	 */
	Map<String, Long> discardIdStatistics();

	/**
	 * 统计放弃的qos个数，qos的getValue一致的应该合并
	 * @return
	 */
	Map<Qos, Long> discardQosStatistics();

	/**
	 * 打印统计信息
	 */
	default void printStatistics() {
		System.out.println("id:" + idStatistics());
		System.out.println("qos" + qosStatistics());
		System.out.println("discard id" + discardIdStatistics());
		System.out.println("discard total" + discardQosStatistics());
	}
}
