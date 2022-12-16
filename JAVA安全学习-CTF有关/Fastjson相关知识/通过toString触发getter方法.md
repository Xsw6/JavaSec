# 通过toString触发getter方法

例题如：https://pupil857.github.io/2022/12/08/NCTF2022-%E5%87%BA%E9%A2%98%E5%B0%8F%E8%AE%B0/

`JSONObject是Map的子类，在执行toString() 时会将当前类转为字符串形式，会提取类中所有的Field，自然会执行相应的 getter 、is等方法`

