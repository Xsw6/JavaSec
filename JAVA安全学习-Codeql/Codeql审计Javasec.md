# Codeql审计Javasec

[这篇文章](http://me.xxf.world/post/codeql-huan-jing-da-jian/#0x02%E7%BC%96%E5%86%99%E6%9F%A5%E8%AF%A2%E8%AF%AD%E5%8F%A5)（师傅再案例中利用了Codeql自带的XSS规则库，并且讲述了一些基本语法）

[这篇文章](https://www.wangan.com/p/7fygf3002b70f418#%E5%89%8D%E8%A8%80)（分析了利用codeql审计javasec）

## 创建数据库

### 命令行界面

下载好对应的项目，执行`codeql database create [编译后的数据库生成路径] --language=java --command="mvn clean install --file pom.xml" --source-root=""`

- `-l,--language=<lang>` 创建数据库的语言
- `-s,--source-root=<dir>` 项目的源代码路径，默认为当前路径
- `-j,--threads=<num>` 生成数据库使用的线程数，默认为1
- `-M,--ram=<MB>` 使用多大内存执行生成命令
- `-c.--command=<command>` 构建项目使用的命令，如maven项目使用`mvn clean package`等
- `--overwrite` 覆盖之前生成的数据库，如果不加上该命令，若存在同名数据库，则报错。

## XSS规则库的学习

`isSanitizer`：可选，限制污点流

`isSanitizerGuard`：可选，限制污点流

`isAdditionalTaintStep`：可选，添加其他污染步骤

## 审计

与其说审计不如说学习codeql语法。

```
import java
from Method m, BlockStmt block
where
  block = m.getBody() and
  block.getNumStmt() = 0
select m
```

Method 类型是方法类，表示获取当前项目中所有的方法。getBody 谓词返回 body 体，BlockStmt 代表一个语句块。getNumStmt 谓词获取块 child statements 的数量。（通俗易懂的话就是：找到一个方法里面为空）

### 分析Spel

```
import java
import semmle.code.java.frameworks.spring.SpringController
import semmle.code.java.dataflow.TaintTracking
from Call call,Callable parseExpression,SpringRequestMappingMethod route
where
    call.getCallee() = parseExpression and 
    parseExpression.getDeclaringType().hasQualifiedName("org.springframework.expression", "ExpressionParser") and
    parseExpression.hasName("parseExpression") and 
   TaintTracking::localTaint(DataFlow::parameterNode(route.getARequestParameter()),DataFlow::exprNode(call.getArgument(0))) 
select route.getARequestParameter(),call
```

Callable类代表方法和构造函数，调用表达式是用类 `Call` 来抽象的，包括方法调用、 `new` 表达式和使用 `this` 或 `super` 的显式构造函数调用，通过使用谓词 `Call.getCallee` 来找出一个特定的调用表达式所指向的方法或构造函数。

说是话我没搞清楚call.getCallee()和Callable的区别，[fynch3r](https://fynch3r.github.io/CodeQL%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0-%E5%9B%9B/)又讲，但是自己还是不怎么清楚。理解能力太差了。这里我就觉得他两指的是一个东西，至于为什么用call.getCallee是因为后面只能用call.getArgument(0)。

总结一句话：`筛选出一条本地数据，该数据是从所有controller中的方法中的获取到的参数到调用parseExpression()是流通的数据。`

## 分析一条能调用exec的方法

```
import java
import semmle.code.java.frameworks.spring.SpringController
import semmle.code.java.dataflow.TaintTracking

from Call call,Callable m,SpringRequestMappingMethod route
where
    call.getCallee() = m and
    m.getDeclaringType().hasQualifiedName("java.lang", "Runtime") and
    m.hasName("exec") and
   TaintTracking::localTaint(DataFlow::parameterNode(route.getARequestParameter()),DataFlow::exprNode(call.getArgument(0))) 
select route.getARequestParameter(),route
```

## 分析一手sql

```
import java
import semmle.code.java.frameworks.spring.SpringController
import semmle.code.java.dataflow.TaintTracking

from Call call,Callable m,SpringRequestMappingMethod route
where
    call.getCallee() = m and
    m.getDeclaringType().hasQualifiedName("java.sql", "Statement") and
    m.hasName("executeQuery") and
   TaintTracking::localTaint(DataFlow::parameterNode(route.getARequestParameter()),DataFlow::exprNode(call.getArgument(0))) 
select route.getARequestParameter(),route
```

成功！！这玩意真的强大如斯！！！！
这个其实有些弊端，文章中也提到了...如果sink中没有找到明确的exec就会失败。但是有时候exec也许被其他函数内部调用。（`这只是举个例子`）。

其他的审计也是非常简单啊！！

## 全局数据流

这就是类似分析shiro的codeql。
就是上面的语句，我们分析ssrf。首先要知道存在new Url()和存在HttpUtils.URLConnection(url)都有可能存在ssrf。
如下代码发现搜索不到。

```
import java
import semmle.code.java.frameworks.spring.SpringController
import semmle.code.java.dataflow.TaintTracking

from Call call,Callable m,SpringRequestMappingMethod route
where
    call.getCallee() = m and
    m.getDeclaringType().hasQualifiedName("java.net", "URL") and
    m.hasName("URL") and
   TaintTracking::localTaint(DataFlow::parameterNode(route.getARequestParameter()),DataFlow::exprNode(call.getArgument(0))) 
select route.getARequestParameter(),route
```

但是如下代码却能搜索到。

```
import semmle.code.java.dataflow.DataFlow
import semmle.code.java.frameworks.spring.SpringController
import semmle.code.java.dataflow.TaintTracking
class Configuration extends DataFlow::Configuration {
  Configuration() {
    this = "Configer"
  }
  override predicate isSource(DataFlow::Node source) {
    exists( SpringRequestMappingMethod route| source.asParameter()=route.getARequestParameter() )
  }
  override predicate isSink(DataFlow::Node sink) {
    exists(Call call ,Callable parseExpression|
      sink.asExpr() = call.getArgument(0) and
      call.getCallee()=parseExpression and 
   parseExpression.getDeclaringType().hasQualifiedName("java.net", "URL") and
      parseExpression.hasName("URL")
    )
  }
}
from  DataFlow::Node src, DataFlow::Node sink, Configuration config
where config.hasFlow(src, sink)
select src,sink
```

中间看过因为之前看过shiro了，直接掠过了。