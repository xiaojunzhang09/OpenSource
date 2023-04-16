package com.ibeetl.code.ch03.mutiple.jmh;

import com.ibeetl.code.ch03.mutiple.QosPolicy;
import com.ibeetl.code.ch03.mutiple.simple.FastSimpleQosPolicy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Level.Iteration;

/**
 * 测试队列个数不一样的情况下性能
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(100)
@Fork(value = 1, jvmArgs = {"-Xmx1G", "-Xms1G"})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JmhLargeNumQueue extends BaseJmhQosThreadPool {

	//    @Param({"1","10","100","1000","10000","100000","500000"})
	@Param({"1000", "10000", "50000"})
	int queueNum;

	@Setup(Iteration)
	public void init() {
		this.maxQueueNum = queueNum;
		initMutilQosPool();
	}

	protected QosPolicy getQosPolicy() {
		return new FastSimpleQosPolicy();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(JmhLargeNumQueue.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}

}
