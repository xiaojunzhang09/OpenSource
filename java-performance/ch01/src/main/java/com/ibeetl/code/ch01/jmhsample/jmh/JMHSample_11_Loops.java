package com.ibeetl.code.ch01.jmhsample.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 这是官网提供的不要在jmh里用上循环，因为jit会优化循环，
 * @author 公众号 闲谈java开发
 * @see  "http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/"
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_11_Loops {



  int x = 1;
  int y = 2;

  /*
   * 正确做法
   */

  @Benchmark
  public int measureRight() {
    return (x + y);
  }

  /*
   * 错误的做法，看看后面的measureWrong_xxx就知道了
   *
   */
  private int reps(int reps) {
    int s = 0;
    for (int i = 0; i < reps; i++) {
      s += (x + y);
    }
    return s;
  }

  /*
   * 循环不同次数，得出来的reps平均的性能是不一样的
   *
   */

  @Benchmark
  @OperationsPerInvocation(1)
  public int measureWrong_1() {
    return reps(1);
  }

  @Benchmark
  @OperationsPerInvocation(10)
  public int measureWrong_10() {
    return reps(10);
  }

  @Benchmark
  @OperationsPerInvocation(100)
  public int measureWrong_100() {
    return reps(100);
  }

  @Benchmark
  @OperationsPerInvocation(1000)
  public int measureWrong_1000() {
    return reps(1000);
  }

  @Benchmark
  @OperationsPerInvocation(10000)
  public int measureWrong_10000() {
    return reps(10000);
  }

  @Benchmark
  @OperationsPerInvocation(100000)
  public int measureWrong_100000() {
    return reps(100000);
  }



  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(JMHSample_11_Loops.class.getSimpleName())
      .forks(1)
      .build();

    new Runner(opt).run();
  }

}
