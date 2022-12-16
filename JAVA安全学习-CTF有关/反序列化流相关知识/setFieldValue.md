## setFieldValue

```java
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        if(field != null) {
            field.set(obj, value);
        }
    }
```

