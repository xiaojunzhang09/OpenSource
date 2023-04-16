
# 书中章节
```aidl	
第9章　Java字节码	
    9.1　Java字节码	
        9.1.1　基础知识	
        9.1.2　.class文件的格式	
    9.2　Java方法的执行	
        9.2.1　方法在内存中的表示	
        9.2.2　方法在.class文件中的表示	
        9.2.3　指令的分类	
        9.2.4　HelloWorld字节码分析	
        9.2.5　字符串拼接字节码分析	
    9.3　字节码IDE插件	
    9.4　ASM入门	
        9.4.1　生成类名和构造函数	
        9.4.2　生成main方法	
        9.4.3　调用生成的代码	
    9.5　ASM增强代码	
        9.5.1　使用反射实现	
        9.5.2　使用ASM生成辅助类	
        9.5.3　switch语句的分类	
        9.5.4　获取Bean中的property	
        9.5.5　switch语句的实现	
        9.5.6　性能对比	

```

# 主要代码说明

* com.ibeetl.code.ch09.HelloWorld  书中例子，用javap或者asm插件查看字节码
* com.ibeetl.code.ch09.BeanValueBenchmark  动态生成属性调用类，并同反射调用做性能对比
* com.ibeetl.code.ch09.TestSample  通过java agent方式，sayHello方法会在类加载的时候，生成类似sayHello2的方法，运行此类需要加上vm 参数 
`-javaagent:./target/agent-1.0-SNAPSHOT.jar=xxConfigText`,agent的入口类是 com.ibeetl.code.ch09.tracer.TracerAgent，可以打断点运行




# 节选

在2.2节中，字符串拼接concatbyOptimizeBuilder和concatbyBuilder的执行性能相差较大，前者更为优秀，但从源码上看是一样的，这里简单分析一下为什么有此差异。对于2.2节中的例子StringConcatTest.java，使用javap -c StringConcatTest.class查看其字节码属性。性能表现优秀的字符串拼接的代码片段如下：

```
//concatbyOptimizeBuilder
String str = new StringBuilder().append(a).append(b).toString();
```
对应的字节码如下，指令总数是13个：
```
public java.lang.String concatbyOptimizeBuilder();
    Code:
       0: new           #6     
       3: dup
       4: invokespecial #7   
       7: aload_0
       8: getfield      #3    
      11: invokevirtual #8   
      14: aload_0
      15: getfield      #5 
      18: invokevirtual #8 
      21: invokevirtual #9 
      24: astore_1
      25: aload_1
      26: areturn
```
性能表现较差的字符串拼接的代码片段如下：
```
//concatbyBuilder
StringBuilder sb = new StringBuilder();
sb.append(a);
sb.append(b);
```
如果查看其字节码，则会发现字节码指令总数在concatbyOptimizeBuilder的基础上增加了4个指令。这就是concatbyOptimizeBuilder性能表现优秀的原因。

> 由于篇幅有限，这里没有完整列出两个方法的字节码，感兴趣的读者可以基于第2章中的例子StringConcatTest，通过javap –c StringConcatTest.class命令自行查看这两个方法字节码的差别。
> 在5.4节中，说明了HikariCP是一个高性能数据库连接池，其作者在源码wiki的“Down the Rabbit Hole”中提到了对一处重要方法“PreparedStatement prepareStatement(String sql, String[] columnNames) ”的字节码优化，感兴趣的读者可以查阅这篇文章，了解它是如何优化字节码指令的。

在8.20节中，指出了info方法使用可变数组作为参数的性能问题，建议使用info2的定义方式：

```
//info方法调用需要额外的指令构造数组作为参数
info("abc {}，{}","a","b");
void info(String var1, Object... var2){}

//info2方法调用有更少的字节码、更好的性能
info2("abc {}，{}","a","b");
void info2(String message,Object var1,Object var2){}
```

如果查看调用info方法的字节码，则会看到类似下面的字节码，先用ANEWARRAY指令创建数组：
```
ANEWARRAY java/lang/Object
DUP
ICONST_0
LDC "a"
AASTORE
DUP
ICONST_1
LDC "b"
AASTORE
INVOKEVIRTUAL com/ibeetl/com/ch09/HelloWorld.info (Ljava/lang/String;[Ljava/lang/ Object;)V
```
info2方法的字节码则精简得多：

```
LDC "a"
LDC "b"
INVOKEVIRTUAL com/ibeetl/com/ch09/HelloWorld.info2 (Ljava/lang/String;Ljava/lang/Object; Ljava/lang/Object;)V
```