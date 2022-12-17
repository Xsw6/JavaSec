# JNI

**JNI：JNI是Java Native Interface的缩写，通过使用Java本地接口书写程序，可以确保代码在不同的平台上方便移植。从Java1.1开始，JNI标准成为java平台的一部分，它允许Java代码和其他语言写的代码进行交互。JNI一开始是为了本地已编译语言，尤其是C和C++而设计的，但是它并不妨碍你使用其他编程语言，只要调用约定受支持就可以了。**

由一道比赛题目需要动态链接库，问了会的师傅，发现需要学习jni。很好奇就来学习了。（Orz....昨天才说再也不学关于c++方面的知识）

如何正确的创建一个正确的可以被java编译的dll文件呢？引用一下`nice_0e3`师傅的图片。最终我们的目的都是让java虚拟机读取文件。这里分为2路，第一路javac就不做说明了。

第二路：java文件->通过javah生成头文件->编写c/c++最终实现代码

![img](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212171134439.png)

## 定义一个native方法

```java
package xs.test;

public class CommandTest {
    public native int sum(int num1,int num2);

}
```

## 相关命令

编译头文件通常我们使用如下命令即可：`javah cp . 加载类路径`

### javah

![](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212171149691.png)

### 英文点

![image-20221217115120737](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202212171151764.png)

再使用如上命令后，便可以生成一个成功的头文件（名为：`xs_test_CommandTest`也就是包的完整路径）。（PS：这里也说明了并不需要利用javac先将java文件进行编译）
**同时JDK10移除了javah，可以使用javac加-h参数方式生成头文件**。

## 头文件

```
#ifndef _Included_xs_test_CommandTest
#define _Included_xs_test_CommandTest
...
#endif
```

该代码是为了可以让别的c/c++程序在多次引用此头文件而不发生冲突。（因为往往一个头文件可能包含了多个头文件并且其中有重复性）。

```
#ifdef __cplusplus 
extern "C" { 
#endif 
... //一些代码
#ifdef __cplusplus 
} 
#endif 
```

__cplusplus是cpp中的自定义宏，那么定义了这个宏的话表示这是一段cpp的代码，也就是说，上面的代码的含义是:如果这是一段cpp的代码，那么加入extern "C"{和}处理其中的代码。反之如果在c语言中：有效部分只有`一些代码`

extern "C":避免编绎器按照C++的方式去编绎C函数。
作用：这种类型的头文件可以被#include到c文件中编译，也可以被#include到c++文件中编译。

JNIEXPORT 用来表示该函数是否可导出（即：方法的可见性）

jint类型：参考如下类型对照表:

| Java类型 | JNI类型  | C/C++类型      | 大小       |
| :------- | :------- | :------------- | :--------- |
| Boolean  | Jblloean | unsigned char  | 无符号8位  |
| Byte     | Jbyte    | char           | 有符号8位  |
| Char     | Jchar    | unsigned short | 无符号16位 |
| Short    | Jshort   | short          | 有符号16位 |
| Int      | Jint     | int            | 有符号32位 |
| Long     | Jlong    | long long      | 有符号64位 |
| Float    | Jfloat   | float          | 32位       |
| Double   | Jdouble  | double         | 64位       |

JNICALL 用来表示函数的调用规范。
Java_xs_test_CommandTest_sum：Java_固定前缀+包名+方法名
括号中四个参数：第一个是JNI环境变量对象，第二个是Java调用的对象（`如果先利用javac将文件编译成class文件、这里即为jclass`），后两个为参数。

## 编写c语言文件

```c
#include "com_test_Command.h"
JNIEXPORT jint JNICALL Java_com_test_Command_sum
  (JNIEnv *env, jobject obj, jint num1, jint num2){
  return num1+num2;
  }
```

## 生成动态链接库

```cmd
x86_64-w64-mingw32-g++ -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -shared -o cmdtest.dll .\CommandTest.c
```

## 调用

```java
        System.loadLibrary("cmdtest")；
        CommandTest commandTest = new CommandTest();
        int sum = commandTest.sum(1, 2);
        System.out.println(sum);
```

  System.loadLibrary("cmdtest")，不用添加文件名。只需要让dll文件在`java.library.path`路径下。
如何设置dll文件在此路径下：
方法1：在jvm启动时添加-Djava.library.path=路径
方法2：System.setProperty("java.library.path","路径");
方法3：打印`java.library.path`，System.out.println(System.getProperty("java.library.path"));直接将dll文件放入。

至此我们成功的用java调用了动态链接库。

## 参考链接

https://javasec.org/javase/JNI/

https://www.cnblogs.com/nice0e3/p/14067160.html

https://www.bilibili.com/video/BV1rS4y1q7J5/?spm_id_from=333.337.search-card.all.click&vd_source=ffa29603994e597f1f8a2562b25bcd08

https://www.cnblogs.com/qixingchao/p/11911787.html

