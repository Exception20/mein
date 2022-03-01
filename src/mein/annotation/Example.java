package mein.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Example
{
    @BeanField
    private String value;


    public Example(String value) {
        this.value = value;
    }


    @Getter("value")
    public String getValue() {
        return value;
    }


    @Setter("value")
    public Example setValue(String value) {
        this.value = value;
        return this;
    }



    public static Method getter(Class<?> bean, Field field) {
        for (Method m : bean.getDeclaredMethods()) {
            Getter getter = m.getAnnotation(Getter.class);
                System.out.println(getter);
            if (getter != null &&
                getter.value().equals(field.getName())) {
                    return m;
            }
        }
        return null;
    }

    public static Method getter(Class<?> bean, String fieldName) {
        try {
            Field field = bean.getDeclaredField(fieldName);
            return getter(bean, field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
    
    
    
    
    
    

    public static void mains(String[] args) throws Exception {
        Example instance = new Example("Hello Annotation");
        Method getValue = getter(Example.class, "value");
        if (getValue != null)
            System.out.println(getValue.invoke(instance));
        else
            System.out.println("No Such Method");
    }


    public static String annotationInfo(Object instance, String name) {
        return annotationInfo(instance.getClass(), name);
    }


    public static String annotationInfo(Class<?> clazz, String name) {
        List<Method> methods = Arrays.asList(clazz.getDeclaredMethods());
        for (Method m : methods) {
            if (m.getName().equals(name)) {
                Annotation[] annots = m.getAnnotations();
                if (annots.length == 1) {
                    System.out.println(annots[0]);
                } else {
                    System.out.println(Arrays.toString(annots));
                }
            }
        }
        return "aus";
    }

}
