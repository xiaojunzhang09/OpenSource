package com.ibeetl.code.ch03.mutiple.jmh;

import com.ibeetl.code.ch03.mutiple.QosPolicy;
import com.ibeetl.code.ch03.mutiple.simple.FastSimpleQosPolicy;
import com.ibeetl.code.ch03.mutiple.simple.HighAlwaysPolicy;
import com.ibeetl.code.ch03.mutiple.simple.SimpleQosPolicy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Level.Iteration;

/**
 * 测试队列个数,Qos策略不一样的情况下性能
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(50)
@Fork(value = 1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JmhPolicyQueue extends BaseJmhQosThreadPool {

	@Param({"100", "1000", "10000"})
	int queueNum;
	@Param({"simple", "alwaysHigh", "fastSimple"})
	String policy;

	@Setup(Iteration)
	public void init() {
		this.maxQueueNum = queueNum;

		initMutilQosPool();
	}

	@Override
	protected QosPolicy getQosPolicy() {
		if (policy.equals("simple")) {
			return new SimpleQosPolicy();
		} else if (policy.equals("alwaysHigh")) {
			//fine&warning
			return new HighAlwaysPolicy(-2);
		} else if (policy.equals("fastSimple")) {
			return new FastSimpleQosPolicy();
		} else {
			throw new IllegalArgumentException(policy);
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(JmhPolicyQueue.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}

}
