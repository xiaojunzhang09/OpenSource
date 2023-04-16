package com.ibeetl.code.ch02;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * 格式化字符串也是系统常用功能，总能在系统调优过程看到此类消耗，最佳建议是避免格式化字符串.
 * 在高性能应用中，log框架就不建议使用占位符方式输出日志
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class StringFormatTest {

  String para = "java";
  String formatString = "hello %s, nice to meet you";
  String messageFormat = "hello {0}, nice to meet you";

  @Benchmark
  public String format(){
   String value =  String.format(formatString,para);
   return value;

  }

  @Benchmark
  public String messageFormat(){
    String value = MessageFormat.format(messageFormat, para);
    return value;
  }


	/**
	 * 性能最好的方式
	 * @return
	 */
	@Benchmark
  public String stringAppend(){
    StringBuilder sb = new StringBuilder();
    sb.append("ello ");
    sb.append(para);
    sb.append(", nice to meet you") ;
    return sb.toString();
  }




  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(StringFormatTest.class.getSimpleName())
      .forks(1)
      .build();
    new Runner(opt).run();
  }
}

