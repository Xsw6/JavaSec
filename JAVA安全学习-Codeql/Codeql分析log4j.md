# Codeql分析log4j

[利用CodeQL分析并挖掘Log4j漏洞](https://mp.weixin.qq.com/s/JYco8DysQNszMohH6zJEGw)

## 寻找lookup方法

（`原本想直接找一个调用了lookup的方法的流,但是好像不可取`）根据自己昨天学的嘛，上上手。不过这里好像没有入口点？昨天是学的找到controller方法并且获取其中的参数，找到一个流（使用了本地和全局两种方式）

在我写的时候又有一个问题出现了：有许许多多的包下包含了可能调用jndi的方法。（`这是第一个问题`）
文章中有直接给了答案：

```
    class Context extends  RefType{

        Context(){

            this.hasQualifiedName("javax.naming", "Context")

            or

            this.hasQualifiedName("javax.naming", "InitialContext")

            or

            this.hasQualifiedName("org.springframework.jndi", "JndiCallback")

            or 

            this.hasQualifiedName("org.springframework.jndi", "JndiTemplate")

            or

            this.hasQualifiedName("org.springframework.jndi", "JndiLocatorDelegate")

            or

            this.hasQualifiedName("org.apache.shiro.jndi", "JndiCallback")

            or

            this.getQualifiedName().matches("%JndiCallback")

            or

            this.getQualifiedName().matches("%JndiLocatorDelegate")

            or

            this.getQualifiedName().matches("%JndiTemplate")

        }

    }
```

 `RefType`：在CodeQL中，RefType就包含了我们在Java里面使用到的Class,Interface的声明。例如this.hasQualifiedName("javax.naming", "Context")，是获取到了javax.naming包下的Context类。
通过这个再做个限制，找到lookup。

```
from Call call,Callable m
where
    call.getCallee() = m and 
    m.getDeclaringType() instanceof Context and
    m.hasName("lookup")
select call
```

**简单的说，Callable是可以被调用的东西，Call是可以调用Callable的东西。**又去看了下文章啊哈哈哈。

那么其实这里我们是找到了`sink`,还需要找source。
文章中说虽然Codeql定义了`RemoteFlowSource`支持多种source。是可以直接写了吗？试试？

```
/**
 * @kind path-problem
 */
import java

import semmle.code.java.dataflow.FlowSources

import DataFlow::PathGraph

class Context extends  RefType{

    Context(){

        this.hasQualifiedName("javax.naming", "Context")

        or

        this.hasQualifiedName("javax.naming", "InitialContext")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiCallback")

        or 

        this.hasQualifiedName("org.springframework.jndi", "JndiTemplate")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiLocatorDelegate")

        or

        this.hasQualifiedName("org.apache.shiro.jndi", "JndiCallback")

        or

        this.getQualifiedName().matches("%JndiCallback")

        or

        this.getQualifiedName().matches("%JndiLocatorDelegate")

        or

        this.getQualifiedName().matches("%JndiTemplate")

    }

}

class TainttrackLookup  extends TaintTracking::Configuration {

    TainttrackLookup() { 
        this = "TainttrackLookup" 
    }


    override predicate isSource(DataFlow::Node source) {
        source instanceof RemoteFlowSource
    }


    override predicate isSink(DataFlow::Node sink) {

        exists(MethodAccess m| 
            m.getMethod().getName() = "lookup" 
            and m.getMethod().getDeclaringType() instanceof Context
            and sink.asExpr() = m.getArgument(0)
            )

    }

} 

from TainttrackLookup config , DataFlow::PathNode source, DataFlow::PathNode sink
where
    config.hasFlowPath(source, sink)
select sink.getNode(), source, sink, "source are"
```

没结果....算了继续往下看了。
上面既然失败了，那么就要自己找入口点。由于跟过log4j比较清楚入口点。
那么可以直接设置source。（org.apache.logging.log4j.spi.AbstractLogger）

```
/**
 * @kind path-problem
 */
import java

import semmle.code.java.dataflow.FlowSources

import DataFlow::PathGraph

class Context extends  RefType{

    Context(){

        this.hasQualifiedName("javax.naming", "Context")

        or

        this.hasQualifiedName("javax.naming", "InitialContext")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiCallback")

        or 

        this.hasQualifiedName("org.springframework.jndi", "JndiTemplate")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiLocatorDelegate")

        or

        this.hasQualifiedName("org.apache.shiro.jndi", "JndiCallback")

        or

        this.getQualifiedName().matches("%JndiCallback")

        or

        this.getQualifiedName().matches("%JndiLocatorDelegate")

        or

        this.getQualifiedName().matches("%JndiTemplate")

    }

}

class Logger extends  RefType{

    Logger(){

        this.hasQualifiedName("org.apache.logging.log4j.spi", "AbstractLogger")

    }

}
class TainttrackLookup  extends TaintTracking::Configuration {

    TainttrackLookup() { 
        this = "TainttrackLookup" 
    }


    override predicate isSource(DataFlow::Node source) {
        exists(MethodAccess ma |
            ma.getMethod().getName() = "logIfEnabled"
            and
            ma.getMethod().getDeclaringType() instanceof Logger
            and
            source.asExpr() = ma.getArgument(3)
        )
    }


    override predicate isSink(DataFlow::Node sink) {

        exists(MethodAccess m| 
            m.getMethod().getName() = "lookup" 
            and m.getMethod().getDeclaringType() instanceof Context
            and sink.asExpr() = m.getArgument(0)
            )

    }

} 

from TainttrackLookup config , DataFlow::PathNode source, DataFlow::PathNode sink
where
    config.hasFlowPath(source, sink)
select sink.getNode(), source, sink, "source are"
```

有几条链子发现可以跟踪一下。可以试试。然后一下午就过去了一直报错。搜了搜说是编译的问题....
![image-20221024175651249](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210241756375.png)

![image-20221024175540295](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210241755396.png)

最后直接把c盘下的.m2的依赖全部给删了。一下又好了。

## 第一条

正如文章所说：
![image-20221024205323792](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210242053861.png)

这里的filter要不为空才能满足codeql的链子。

## 第二条

顺着文章的思路继续往下。（这里调用方法和实际log4j调用方法不同）

## 第三条

还是需要filter

## 第四条

仍然需要filter
 文章说的那种方法。我表示写的代码跟他一样，结果显示却不一样.....就简单阅读了一下。得到了个结论codeql挖掘不出log4j。。。

写上我分析的链子吧。

```
import java

import semmle.code.java.dataflow.FlowSources

import DataFlow::PathGraph

class Context extends  RefType{

    Context(){

        this.hasQualifiedName("javax.naming", "Context")

        or

        this.hasQualifiedName("javax.naming", "InitialContext")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiCallback")

        or 

        this.hasQualifiedName("org.springframework.jndi", "JndiTemplate")

        or

        this.hasQualifiedName("org.springframework.jndi", "JndiLocatorDelegate")

        or

        this.hasQualifiedName("org.apache.shiro.jndi", "JndiCallback")

        or

        this.getQualifiedName().matches("%JndiCallback")

        or

        this.getQualifiedName().matches("%JndiLocatorDelegate")

        or

        this.getQualifiedName().matches("%JndiTemplate")

    }

}

class Logger extends  RefType{

    Logger(){

        this.hasQualifiedName("org.apache.logging.log4j.spi", "AbstractLogger")

    }

}

class LoggerInput extends  Method {

    LoggerInput(){  

        //限定调用的类名、方法名、以及方法只有一个参数

        this.getDeclaringType() instanceof Logger and

        this.hasName("error") and this.getNumberOfParameters() = 1

    }

    //将第一个参数作为source

    Parameter getAnUntrustedParameter() { result = this.getParameter(0) }

}



class TainttrackLookup  extends TaintTracking::Configuration {

    TainttrackLookup() { 
        this = "TainttrackLookup" 
    }


    override predicate isSource(DataFlow::Node source) {
        // exists(MethodAccess ma |
        //     ma.getMethod().getName() = "logIfEnabled"
        //     and
        //     ma.getMethod().getDeclaringType() instanceof Logger
        //     and
        //     source.asExpr() = ma.getArgument(3)
        // )

        exists(LoggerInput LoggerMethod |

            source.asParameter() = LoggerMethod.getAnUntrustedParameter())

    }


    override predicate isSink(DataFlow::Node sink) {

        exists(MethodAccess m| 
            m.getMethod().getName() = "lookup" 
            and m.getMethod().getDeclaringType() instanceof Context
            and sink.asExpr() = m.getArgument(0)
            )

    }

    // override predicate isAdditionalTaintStep(DataFlow::Node fromNode, DataFlow::Node toNode) {
    //     exists(MethodAccess ma,MethodAccess ma2 |
    //         ma.getMethod().getDeclaringType().hasQualifiedName("org.apache.logging.log4j.core.impl", "ReusableLogEventFactory") 
    //         and ma.getMethod().hasName("createEvent") and fromNode.asExpr()=ma.getArgument(5) and ma2.getMethod().getDeclaringType().hasQualifiedName("org.apache.logging.log4j.core.config", "LoggerConfig")  
    //         and ma2.getMethod().hasName("log") and ma2.getMethod().getNumberOfParameters() = 2 and toNode.asExpr()=ma2.getArgument(0)
    //                 )
    //   }

} 

from TainttrackLookup config , DataFlow::PathNode source, DataFlow::PathNode sink
where
    config.hasFlowPath(source, sink)
select sink.getNode(), source, sink, "source are"
```

