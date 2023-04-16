package com.ibeetl.code.ch01.jmhsample.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
/**
 * 这是官网提供的常量折叠演示，我把关键英语翻译了一下
 * @author 公众号 闲谈java开发
 * @see  http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_10_ConstantFold {

  private double x = Math.PI;


  private final double wrongX = Math.PI;

  @Benchmark
  public double baseline() {
    // 基准测试，如果其他jmh方法性能跟这个一样，那么就说明那些jmh犯法写错了
    return Math.PI;
  }

  @Benchmark
  public double measureWrong_1() {
    // 错误的测试，jvm不需要计算，直接返回
    return Math.log(Math.PI);
  }

  @Benchmark
  public double measureWrong_2() {
    // 还是错误的测试，wrongX是常量，可以预测结果
    return Math.log(wrongX);
  }

  @Benchmark
  public double measureRight() {
    // 正确测试.
    return Math.log(x);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(JMHSample_10_ConstantFold.class.getSimpleName())
      .forks(1)
      .build();

    new Runner(opt).run();
  }

}
