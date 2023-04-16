package com.ibeetl.code.ch02;

import cn.hutool.core.util.StrUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * split是真的耗费cpu，应该尽量避免。我看过的一个系统，高峰期，cpu全在搞split操作。
 * 比较了jdk自带的split和hutool工具的split
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class SplitJmhTest {

	String str = StrUtil.repeat("hello,world", 1000);

	@Benchmark
	public String[] splitByRegex() {
		return str.split(",");

	}

	@Benchmark
	public List<String> splitByTokenizer() {
		StringTokenizer st = new StringTokenizer(str, ",");
		List<String> list = new ArrayList<>();
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return list;
	}

	@Benchmark
	public List<String> splitByIndexOfChar() {
		int pos = 0, end;
		List<String> list = new ArrayList<>();
		while ((end = str.indexOf(',', pos)) >= 0) {
			list.add(str.substring(pos, end));
			pos = end + 1;
		}
		if (pos != str.length() - 1) {
			list.add(str.substring(pos));
		}
		return list;
	}

	@Benchmark
	public List<String> splitByIndexOfString() {
		int pos = 0, end;
		List<String> list = new ArrayList<>();
		while ((end = str.indexOf(",", pos)) >= 0) {
			list.add(str.substring(pos, end));
			pos = end + 1;
		}
		if (pos != str.length() - 1) {
			list.add(str.substring(pos));
		}
		return list;
	}

	@Benchmark
	public List<String> hutoolSplitByChar() {
		return StrUtil.split(str, ',');
	}

	@Benchmark
	public List<String> hutoolSplitByString() {
		return StrUtil.split(str, ',', 0, false, false);
	}

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder().include(SplitJmhTest.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();


	}
}