package com.ibeetl.code.ch03.mutiple.stat;


import com.ibeetl.code.ch03.mutiple.Qos;
import com.ibeetl.code.ch03.mutiple.Statistics;

import java.util.HashMap;
import java.util.Map;


public class SimpleStatistics implements Statistics {

	Map<String, Long> idStatisticsMap = new HashMap<>();
	Map<Qos, Long> qosStatisticsMap = new HashMap<>();


	Map<String, Long> discardIdStatisticsMap = new HashMap<>();
	Map<Qos, Long> discardQosStatisticsMap = new HashMap<>();

	@Override
	public void add(String id, Qos qos) {
		long d = idStatisticsMap.getOrDefault(id, 0L);
		idStatisticsMap.put(id, d + 1);

		long q = qosStatisticsMap.getOrDefault(qos, 0L);
		qosStatisticsMap.put(qos, q + 1);


	}

	@Override
	public void discard(String id, Qos qos) {

		long d = discardIdStatisticsMap.getOrDefault(id, 0L);
		discardIdStatisticsMap.put(id, d + 1);

		long q = discardQosStatisticsMap.getOrDefault(qos, 0L);
		discardQosStatisticsMap.put(qos, q + 1);
	}

	@Override
	public Map<String, Long> idStatistics() {
		return idStatisticsMap;
	}

	@Override
	public Map<Qos, Long> qosStatistics() {
		//TODO,需要合并同样gegValue的qos
		return qosStatisticsMap;
	}

	@Override
	public Map<String, Long> discardIdStatistics() {
		return discardIdStatisticsMap;
	}

	@Override
	public Map<Qos, Long> discardQosStatistics() {
		return discardQosStatisticsMap;
	}
}
