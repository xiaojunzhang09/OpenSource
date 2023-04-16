package com.ibeetl.code.ch03.mutiple.stat;

import com.ibeetl.code.ch03.mutiple.Qos;
import com.ibeetl.code.ch03.mutiple.Statistics;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于时间线的统计，如,10秒一次统计
 */
public class TimelineStatistics implements Statistics {

	Stack<SimpleStatistics> stack = new Stack<>();
	LocalDateTime startDate = null;
	int durantion;
	TimeUnit timeUnit;
	ScheduledExecutorService scheduledThreadPool = null;
	static final int MAX = 1024;

	public TimelineStatistics(int duration) {
		this(duration, TimeUnit.SECONDS);
	}

	public TimelineStatistics(int duration, TimeUnit timeUnit) {
		startDate = LocalDateTime.now();
		stack.push(new SimpleStatistics());
		this.durantion = duration;
		this.timeUnit = timeUnit;
		//改成当add的时候，检测是否需要push
		scheduledThreadPool = Executors.newScheduledThreadPool(1);
		scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (stack.size() > MAX) {
					//防止内存溢出
					printStatistics();
					stack.clear();
				}
				stack.push(new SimpleStatistics());
			}
		}, duration, duration, timeUnit);
	}

	@Override
	public void add(String id, Qos qos) {
		SimpleStatistics simpleStatistics = stack.peek();
		simpleStatistics.add(id, qos);
	}

	@Override
	public void discard(String id, Qos qos) {
		SimpleStatistics simpleStatistics = stack.peek();
		simpleStatistics.discard(id, qos);
	}

	/**
	 * 合并所有时间线，并输出
	 * @return
	 */
	@Override
	public Map<String, Long> idStatistics() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 合并所有时间线，并输出
	 * @return
	 */
	@Override
	public Map<Qos, Long> qosStatistics() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 合并所有时间线，并输出
	 * @return
	 */
	@Override
	public Map<String, Long> discardIdStatistics() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 合并所有时间线，并输出
	 * @return
	 */
	@Override
	public Map<Qos, Long> discardQosStatistics() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void printStatistics() {

		int i = 0;
		LocalDateTime date = this.startDate;
		for (SimpleStatistics simpleStatistics : stack) {
			System.out.println(">>>>>>>>" + date);
			simpleStatistics.printStatistics();
			date = date.plus(durantion, convert(timeUnit));
		}

	}

	public void stopTime() {
		this.scheduledThreadPool.shutdownNow();
	}


	public static ChronoUnit convert(TimeUnit tu) {
		if (tu == null) {
			return null;
		}
		switch (tu) {
			case DAYS:
				return ChronoUnit.DAYS;
			case HOURS:
				return ChronoUnit.HOURS;
			case MINUTES:
				return ChronoUnit.MINUTES;
			case SECONDS:
				return ChronoUnit.SECONDS;
			case MICROSECONDS:
				return ChronoUnit.MICROS;
			case MILLISECONDS:
				return ChronoUnit.MILLIS;
			case NANOSECONDS:
				return ChronoUnit.NANOS;
			default:
				assert false : "there are no other TimeUnit ordinal values";
				return null;
		}
	}
}

