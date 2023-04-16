package com.ibeetl.code.ch01.nojmh;

import com.ibeetl.code.ch01.sample.CityKey;

import java.util.concurrent.TimeUnit;

/**
 * 强调，用字符串构造对象，晦涩难懂，而且，性能还很差。这里用非jmh方式测试，建议参考jmh测试 MyBenchmark
 * @author 公众号 闲谈java开发
 */
public class PerformanceKeyTest {


    public static void main(String[] args){
        Integer a = 13;
        StringBuilder sb = new StringBuilder();
        sb.append(a);

        int max = 10000000;
        Integer provinceId = 31;
        Integer cityId = 678;

        Long start = System.nanoTime();
        testStringKey(max,provinceId,cityId);
        Long end = System.nanoTime();
        testObjectKey(max,provinceId,cityId);
        Long end1 = System.nanoTime();
        print(start,end,end1);
    }

    public static void print(long start,long end,long end1){
        long  elapsedTime = TimeUnit.NANOSECONDS.toMillis(end - start);
        long  perferElapsedTime = TimeUnit.NANOSECONDS.toMillis(end1 - end);
        System.out.println("elapsedTime="+elapsedTime+",perferElapsedTime="+perferElapsedTime);
    }


    public static String   testStringKey(int max,Integer proviceId,Integer townId){
        String key = null;
        for(int i=0;i<max;i++){
            key = proviceId+"#"+townId;
        }
        return key;

    }
    public static CityKey   testObjectKey(int max,Integer proviceId,Integer townId){
        CityKey key = null;
        for(int i=0;i<max;i++){
             key = new CityKey(proviceId,townId);
        }
        return key;
    }


}
