package com.ibeetl.code.ch01.jmhsample.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 这是官网提供的deadcode演示，我把关键英语翻译了一下
 * @author 公众号 闲谈java开发
 * @see  “http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/”
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class JMHSample_08_DeadCode {


  private double x = Math.PI;

  @Benchmark
  public void baseline() {
    /**
     * 基准，什么都不做，如果其他方法跟这个性能一样，那说明其他测试方法写错了,其他情况
     * 1) 有时候性能优化，也可以把上一个版本的作为baseLine
     * 2) 基本操作作为baseline，测试基于此的各种调整
    */

  }

  @Benchmark
  public void measureWrong() {
    //错误测试
    Math.log(x);
  }

  @Benchmark
  public double measureRight() {
    // 正确测试
    return Math.log(x);
  }


  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(JMHSample_08_DeadCode.class.getSimpleName())
      .forks(1)
      .build();

    new Runner(opt).run();
  }

}
