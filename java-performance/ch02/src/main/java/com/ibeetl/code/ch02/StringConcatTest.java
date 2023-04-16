package com.ibeetl.code.ch02;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 一个颠覆很多人看法的测试，+ 拼接字符串居然最快。如果使用StringBuilder，符合使用方式，也能非常快。
 * 否则，就慢不少了。
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class StringConcatTest {

  String a = "select u.id,u.name from user  u";
  String b="  where u.id=? "   ;

	/**
	 * 性能最好的方式
	 * @return
	 */
	@Benchmark
  public String concat(){
    String c = a+b;
    return c ;
  }

  @Benchmark
  public String concatByOptimizeBuilder(){
  	//同 #concat()，这种字节码最少
    String c = new StringBuilder().append(a).append(b).toString();
    return c;
  }



  @Benchmark
  public String concatByBuilder(){
    //字节码较多，性能慢
    StringBuilder sb = new StringBuilder();
    sb.append(a);
    sb.append(b);
    return sb.toString();
  }

  @Benchmark
  public String concatbyBuffer(){
  	//不会优化，性能同#concatbyBuilder()差不多,尽管用锁，但锁消除了
    StringBuffer sb = new StringBuffer();
    sb.append(a);
    sb.append(b);
    return sb.toString();
  }


  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(StringConcatTest.class.getSimpleName())
      .forks(1)
      .build();
    new Runner(opt).run();
  }
}

