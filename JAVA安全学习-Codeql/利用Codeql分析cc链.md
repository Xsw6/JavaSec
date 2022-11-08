# 利用Codeql分析cc链

## 数据库的构建

https://blog.csdn.net/qq_40131760/article/details/124733182?spm=1001.2014.3001.5502

## 分析

## source

必然是需要找readObject

```
from Method m
where
m.hasName("readObject")
select m
```

这样当然不够，再调用该方法的类必然还要implements  Serializable
所以添加上条件使用Callable进行限制：

**在CodeQL中，Java的方法限制，我们可以使用Callable，并且Callable父类是 Method (普通的方法)和 Constructor(类的构造方法)**

**对于方法调用，我们可以使用call，并且call的父类包括MethodAccess, ClassInstanceExpression, ThisConstructorInvocationStmt 和 SuperConstructorInvocationStmt**

```
class ReadObject extends Method {
    ReadObject(){
    this.hasName("readObject")
    }
}

class Source extends Callable{
    Source(){
        this.getDeclaringType().getASupertype*() instanceof TypeSerializable and this instanceof ReadObject
    }
}

from Source x
select x
```

## sink

执行点
想一下cc链中执行点再哪里呢？有TemplatesImpl的类加载，也有Mthod.invoke的方法。(文章中并没有使用参数污点分析，而是使用的方法)

### Method.invoke

```
class InvokeMethod extends Method {
    InvokeMethod(){
        this.hasName("invoke") and
        this.getDeclaringType().hasQualifiedName("java.lang.reflect","Method")
    }
}
```

同样要使用 Callable对其进行封装，判断其是否能够被调用。

## 完整的ql（invoke方法）

```
/**
@kind path-problem
*/

import java
import semmle.code.java.dataflow.FlowSources

class ReadObject extends Method {
    ReadObject(){
    this.hasName("readObject")
    }
}

class Source extends Callable{
    Source(){
        this.getDeclaringType().getASupertype*() instanceof TypeSerializable and this instanceof ReadObject
    }
}


class InvokeMethod extends Method {
    InvokeMethod(){
        this.hasName("invoke") and
        this.getDeclaringType().hasQualifiedName("java.lang.reflect","Method")
    }
}


class DangerousMethod extends Callable {
    DangerousMethod(){
        this instanceof InvokeMethod 
      }
  }


//   class CallsDangerousMethod extends Callable {

//     CallsDangerousMethod() {
//         exists(Callable a| this.polyCalls(a) and 
//         a instanceof DangerousMethod )
//     }  
// }  


query predicate edges(Method a, Method b) { 
    a.polyCalls(b) 
    // and(a.getDeclaringType().getASupertype*() instanceof TypeSerializable or a.isStatic()) and
    // (b.getDeclaringType().getASupertype*() instanceof TypeSerializable or b.isStatic()) 
}

from Source source,DangerousMethod sink
where edges+(source, sink)
select source, source, sink, "$@ $@ to $@ $@" ,
source.getDeclaringType(),source.getDeclaringType().getName(),
source,source.getName(),
sink.getDeclaringType(),sink.getDeclaringType().getName(),
sink,sink.getName() 
```

跟文章中有些许不一样，主要也是理解代码。再加上手操一下。