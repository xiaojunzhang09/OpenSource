package com.ibeetl.code.ch04;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 位运算，不过最好兼顾代码可读性，比如
 * <pre>
 *  q = (i * 52429) >>> (16+3);
 * </pre>
 * 其实就是q=i*0.1000000003,这个代码来自JDK Integer.toString
 *
 * 位运算的一本书推荐《算法心得》
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class BitTest {
	int a = 19;
	int b = 20;

	public static void main(String[] args) throws RunnerException {
		//先验证一下
		BitTest test = new BitTest();
		boolean isEven = (test.a & 1) != 1;
		boolean isOld = (test.a & 1) == 1;
		System.out.println(isEven + " " + isOld);
		Options opt = new OptionsBuilder().include(BitTest.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}

	@Benchmark
	public boolean general() {
		boolean isEven = a % 2 == 0;
		boolean isOld = b % 2 == 1;
		return isEven && isOld;
	}

	@Benchmark
	public boolean bit() {
		boolean isEven = (a & 1) != 1;
		boolean isOld = (b & 1) == 1;
		return isEven && isOld;
	}
}
