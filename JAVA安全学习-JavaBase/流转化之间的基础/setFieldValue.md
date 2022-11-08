# setFieldValue

为什么连这个都要记录？学弟来问过我....

```java
public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {

    Field field = obj.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(obj, value);
}
```

