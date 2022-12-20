package javaagent;


import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class HelloWorld {
    public static void main(String[] args) {
        hello h1 = new hello();
        h1.hello();
        // 输出当前进程的 pid
        System.out.println("pid ==> " + ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        // 产生中断，等待注入
        Scanner sc = new Scanner(System.in);
        sc.nextInt();

        hello h2 = new hello();
        h2.hello();
        System.out.println("ends...");
    }
}
