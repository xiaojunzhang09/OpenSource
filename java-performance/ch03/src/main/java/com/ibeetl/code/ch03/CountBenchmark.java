package com.ibeetl.code.ch03;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * synchronized 最好别采用，使用并发包下提供的lock或者其他类
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Threads(40)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class CountBenchmark {
	AccessAtomicCount atomicCount = new AccessAtomicCount();
	AccessSynchronizedCount count = new AccessSynchronizedCount();

    @Benchmark
    public  int    atomicAdd(){
		return atomicCount.add();
    }
    @Benchmark
    public  int  synchronizedAdd() {
		return count.add();

	}



    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CountBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
