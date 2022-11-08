# Shiro检测key

1、利用shiro代码本身有两种办法：

- 传入错误的cookie（`多用于验证是否存在shiro漏洞`）
- 利用PrincipalCollection不返回deleteMe（`利用爆破性质`）

2、利用爆破性质（`不出网`）

- Dnslog验证（有可能满足dns解析但是不满足tcp协议）
- 时间延迟

```
   if((System.getProperty("os.name").toLowerCase().contains("win"))){
              Thread.currentThread().sleep(20000L);
       }
```

- 抛出异常错误（`未成功`）

```
    String result = "shiro-Vul-Discover";
            throw new NoClassDefFoundError(new String(result));
```

