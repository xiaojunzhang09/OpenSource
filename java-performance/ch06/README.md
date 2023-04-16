
# 书中章节
```aidl	
第六章 可读性代码	
    6.1　精简注释	
    6.2　变量	
        6.2.1　变量命名	
        6.2.2　变量的位置	
        6.2.3　中间变量	
    6.3　方法	
        6.3.1　方法签名	
        6.3.2　短方法	
        6.3.3　单一职责	
    6.4　分支	
        6.4.1　if else	
        6.4.2　switch case	
    6.5　发现对象	
        6.5.1　不要使用String	
        6.5.2　不要用数组、Map	
    6.6　checked异常（可控异常）	
    6.7　其他事项	
        6.7.1　避免自动格式化	
        6.7.2　关于Null	
```

# 主要代码说明

没有太多可运行的例子,书里列举了很多可读性代码例子


# 节选

变量的位置应该尽量靠近使用的地方，如果在方法开始处定义了变量，然后在100行代码后才使用，那么代码阅读者的心总是悬着的，觉得不是在看代码，而是在看一本悬疑小说—最后的变量会在哪里使用呢？

User seller,buyer;
seller = ...
//50行代码后
buyer = ...

上面的代码最好在使用buyer的地方就近定义，尤其是业务系统，业务复杂，涉及很多变量，就近定义变量，可以减轻阅读负担。
代码块的变量命名不要与类变量重名，否则会导致阅读困难。

```java
public class Point{
    private int x;
    private int y;
    public void calc(Point p){
        //定义一个变量
        int x = p.getX();
        //50行代码后，很难知道x指的是哪个
        return calcLine(x,y);
    }
}
```


上面的代码在调用return方法的时候，x很容易被误解，误以为传入的是类变量x。


# 节选2

使用一些中间变量来增强代码可读性：

```java
return a*b+c/rate+d*e;
```


上面的代码“一气呵成”，而且只用了一行，但没有下面的代码更容易阅读：

```java
int yearTotal = a*b;
int lastYearTotal = c/rate;
int todayTotal = d*e;
int total = yearTotal+lastYearTotal+todayTotal;
return total
```


# 节选3 


当程序中出现String参数、数组参数，以及Map的时候，已经在提醒我们这是遗漏了系统的对象。这三个类型参数是非常灵活的，能容纳下任何数据结构，但有可能遗漏了系统隐含的对象，尤其是数组和Map。例子如下：

```java
Object[] rets = call();
boolean  success = (Boolean)rets[0];
String msg = (String)rets[1];
```


采用对象定义返回结果：
```java
CallResult rets = call();
boolean  success = rets.isSuccess();
String msg =  rets.getMessage();
```


如果CallResult包含某个返回值，那么将CallResult定义成泛型就更加容易阅读，比如返回CallResult：
```java
public CallResult  getUser();
//更好的方式
public CallResult<User>  getUser(){
```


关于这一点，7.2.1节已经出现过类似的例子了。
同样，使用Map来表示对象也是非常糟糕的，代码阅读者根本不知道Map里有多少对象的属性，必须阅读完所有代码才知道如何使用Map。以下代码使用Map表示user对象。在笔者刚入行的19年前，Map还是被很多开发者推崇的，认为“一个Map走天下”，但实际上是“潇潇洒洒走自己的路，让维护者无路可走”。
```java
Map user = new HashMap();
user.put("id",1);
user.put("name","badcode");
user.put("gender",true);
```

当代码维护者在阅读代码时，想知道user对象的“gender”属性是在什么地方设置的，无法使用IDE的查找引用功能，只能文本搜索整个工程代码，期望能定位到。更糟糕的是，如果需要重构，将gender从boolean类型变成int类型，比如用boolean值的true和false分别表示性别男和性别女，如果改为男、女和未知三个值，则gender需要更改为int类型，同时不得不小心谨慎地找遍每一处代码。如果一旦漏掉，那么可能损失巨大。