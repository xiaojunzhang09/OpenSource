package com.ibeetl.code.ch03.mutiple;

import java.util.Collections;
import java.util.Map;

/**
 * 一个默认的，对性能无影响的统计类
 */
public class DefaultStatistics implements Statistics {
	@Override
	public void add(String id, Qos qos) {

	}

	@Override
	public void discard(String id, Qos qos) {

	}

	@Override
	public Map<String, Long> idStatistics() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Qos, Long> qosStatistics() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Long> discardIdStatistics() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Qos, Long> discardQosStatistics() {
		return Collections.emptyMap();
	}

	@Override
	public void printStatistics() {
		System.out.println("DefaultStatistics类 默认不统计");
	}
}
