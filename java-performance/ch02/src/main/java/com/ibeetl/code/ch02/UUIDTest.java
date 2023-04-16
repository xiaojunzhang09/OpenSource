package com.ibeetl.code.ch02;


import com.github.f4b6a3.uuid.UuidCreator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 高并发系统中使用uuid需要注意了，可能会出现阻塞
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(100)
@Fork(1)
public class UUIDTest {



  @Benchmark
  public UUID uuidDefault(){
    return UUID.randomUUID();
  }

  @Benchmark

  @Fork(value=1,jvmArgsAppend="-Djava.security.egd=file:/dev/urandom")
  public UUID uuidNonblockRandom(){
    return UUID.randomUUID();
  }

  @Benchmark
  @Fork(value=1,jvmArgsAppend="-Djava.security.egd=file:/dev/random")
  public UUID uuidBlockRandom(){
    return UUID.randomUUID();
  }


  @Benchmark
  @Fork(value=1,jvmArgsAppend="-Djava.security.egd=file:/dev/./urandom")
  public UUID uuidNonblockRandom_2(){
    return UUID.randomUUID();
  }

  @Benchmark
  @Fork(value=1)
  public UUID timeBasedUUID(){
    return  UuidCreator.getTimeBased();
  }



  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
            .include(UUIDTest.class.getSimpleName())
            .forks(1)
            .syncIterations(false)
            .build();
    new Runner(opt).run();
  }
}

