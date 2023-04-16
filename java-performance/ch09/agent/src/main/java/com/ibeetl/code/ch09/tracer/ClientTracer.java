package com.ibeetl.code.ch09.tracer;

import java.util.HashMap;
import java.util.Map;

/**
 * 埋点记录指标数据
 * @author 公众号 闲谈java开发
 */
public class ClientTracer {
    static ThreadLocal<Map<String,Metric>> local = ThreadLocal.withInitial(() -> new HashMap<>());

    public static void start(String name){
        Metric metric = new Metric();
        metric.setStart(System.nanoTime());
        metric.setName(name);
        local.get().put(name,metric);
    }

    public static void end(String name){

        Metric metric = local.get().remove(name);
        if(metric==null){
            return ;
        }
        if(metric.getEnd()!=0){
            return ;
        }
        metric.setEnd(System.nanoTime());
        flush(metric);

    }

    public static void endError(String name){

        Metric metric = local.get().remove(name);
        if(metric==null){
            return ;
        }
        if(metric.getEnd()!=0){
            return ;
        }
        metric.setEnd(System.nanoTime());
        metric.setError(true);
        flush(metric);

    }

    /**
     * 把记录好的指标输出到后台server，或者缓存到本地，定时1分钟批量输出
     * @param metric
     */
    static protected  void flush(Metric metric){
        //忽略,简单打印
        System.out.println(metric);
    }

}
