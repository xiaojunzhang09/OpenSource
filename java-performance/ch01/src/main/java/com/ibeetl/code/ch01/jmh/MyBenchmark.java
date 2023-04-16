package com.ibeetl.code.ch01.jmh;
import com.ibeetl.code.ch01.sample.Area;
import com.ibeetl.code.ch01.sample.AreaService;
import com.ibeetl.code.ch01.sample.PreferAreaService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 第一章关键例子，测试俩种不同构造区域数据的性能对比
 * @author 公众号 闲谈java开发
 */
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.SECONDS)
@Threads(1)
@Fork(1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MyBenchmark {
    static AreaService areaService = new AreaService();
    static PreferAreaService preferAreaService = new PreferAreaService();
    //输入数据
    static List<Area> data = buildData(20);

    @Benchmark
    public  void  testStringKey(){
        areaService.buildArea(data);
    }
    @Benchmark
    public  void  testObjectKey(){
        preferAreaService.buildArea(data);
    }

    private static List<Area> buildData(int count){
        List<Area>  list = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            Area area = new Area(i,i*10);
            list.add(area);
        }
        return list;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
