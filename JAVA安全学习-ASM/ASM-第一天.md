# ASM

## 前言

因为想学习一下Gadget Inspector发现其中涉及到相关ASM得知识，然后就试着搜了搜资料发现B站就有视频。
还有附带文档资料：https://lsieun.github.io/java/asm/index.html
基本上是看完了，其实完全没必要。如果只是简单得上手前25集就够了。（其实就是了解classfile的相关结构 知道代码为什么这么写）

## 简单上手ASM

这里就按照视频实现进度学习一下

### 环境搭建

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>ASMStudy</artifactId>
    <version>1.0-SNAPSHOT</version>

        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <java.version>1.8</java.version>
            <maven.compiler.source>${java.version}</maven.compiler.source>
            <maven.compiler.target>${java.version}</maven.compiler.target>
            <asm.version>9.0</asm.version>
        </properties>

        <dependencies>
            <dependency>
                <groupId>org.ow2.asm</groupId>
                <artifactId>asm</artifactId>
                <version>${asm.version}</version>
            </dependency>
            <dependency>
                <groupId>org.ow2.asm</groupId>
                <artifactId>asm-commons</artifactId>
                <version>${asm.version}</version>
            </dependency>
            <dependency>
                <groupId>org.ow2.asm</groupId>
                <artifactId>asm-util</artifactId>
                <version>${asm.version}</version>
            </dependency>
            <dependency>
                <groupId>org.ow2.asm</groupId>
                <artifactId>asm-tree</artifactId>
                <version>${asm.version}</version>
            </dependency>
            <dependency>
                <groupId>org.ow2.asm</groupId>
                <artifactId>asm-analysis</artifactId>
                <version>${asm.version}</version>
            </dependency>
        </dependencies>

        <build>
            <plugins>
                <!-- Java Compiler -->
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <fork>true</fork>
                        <compilerArgs>
                            <arg>-g</arg>
                            <arg>-parameters</arg>
                        </compilerArgs>
                    </configuration>
                </plugin>
            </plugins>
        </build>
</project>
```

## ASM生成第一个简单接口Class

### 预期目标

通过代码创建一个如下class文件
![image-20221121210132337](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211212101441.png)

### 代码实现

中间碰到一个小问题：就是`bufferedOutputStream.flush();`没有在写入数据后flush就始终没有生成的数据。
这里相当于一个小知识点：
java在使用流时,都会有一个缓冲区,按一种它认为比较高效的方法来发数据:把要发的数据先放到缓冲区,缓冲区放满以后再一次性发过去,而不是分开一次一次地发.
`而flush()表示强制将缓冲区中的数据发送出去,不必等到缓冲区满.`

所以如果在用流的时候,没有用flush()这个方法,很多情况下会出现流的另一边读不到数据的问题,特别是在数据特别小的情况下。

```java

import org.objectweb.asm.ClassWriter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

public class HelloWorldGenerateCore {
    public static void main(String[] args) throws IOException {
        String relativePath = "F:\\ASMStudy\\target\\classes\\com\\xs\\HelloWorld.class";
        byte[] dump = dump();
        writeBytes(relativePath,dump);
    }

    public static byte[] dump(){
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V1_8,ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,"com/xs/HelloWorld",null,"java/lang/Object",null);
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
    public static void writeBytes(String filePath ,byte[] bytes) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(bytes);
        bufferedOutputStream.flush();
    }


}
```

### 向其中添加字段

```java
classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC,"name","Ljava/lang/String;",null,"Xsw6");
```

## ASM生成一个简单的Class

由上同理：

```java
        classWriter.visit(V1_8,ACC_PUBLIC,"com/xs/HelloWorld",null,"java/lang/Object",null);
```

这样编译出来的class文件没有默认的无参构造函数！！！让我想到了`如何缩短Rome链子那道题`。（4ra1n师傅好像利用到过这个 利用这个删除了行号表`LineNumberTable`）
附上代码：

```java
byte[] bytes = Files.readAllBytes(Paths.get(path));
ClassReader cr = new ClassReader(bytes);
ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
int api = Opcodes.ASM9;
ClassVisitor cv = new ShortClassVisitor(api, cw);
int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
cr.accept(cv, parsingOptions);
byte[] out = cw.toByteArray();
Files.write(Paths.get(path), out);
```

```java
public class ShortClassVisitor extends ClassVisitor {
    private final int api;

    public ShortClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.api = api;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new ShortMethodAdapter(this.api, mv);
    }
}
```

```java
public class ShortMethodAdapter extends MethodVisitor implements Opcodes {

    public ShortMethodAdapter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        // delete line number
    }
}
```

### 向其中添加任意方法

##### 添加一个无参构造方法

```java
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
```

#### 添加一个静态方法

```java
        {
            MethodVisitor mv2 = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv2.visitCode();
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("class initialization method");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(2, 0);
            mv2.visitEnd();
        }
```

当然了这里也可以利用相关代码直接先创建一个类 他会自动生成对应的asm代码。

https://github.com/lsieun/learn-java-asm：路径在run/print*