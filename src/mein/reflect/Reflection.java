package mein.reflect;

import java.lang.reflect.Method;

/* 
 * 2022-05-09  21:28  GMT+8
 */

public class Reflection
{
    private static final Class CallerSensitive;
    static {
        try {
            CallerSensitive = Class.forName("sun.reflect.CallerSensitive");
            // assert(CallerSensitive.isAnnotation());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            CallerSensitive = null;
        }
    }


    public static Class<?> getCallerClass() {
        if (CallerSensitive == null) return getCallerClass(3);
        try {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            for (int i = 2; i < stackTrace.length; i++) {
                Class<?> caller = Class.forName(stackTrace[i].getClassName());
                if (!existsCallerSensitiveAnnotation(caller, stackTrace[i].getMethodName())) {
                    return caller;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Class<?> getCallerClass(int depth) {
        try {
            return Class.forName(new Throwable().getStackTrace()[depth].getClassName());
        } catch (Exception e) {
            return null;
        }
    }


    private static boolean existsCallerSensitiveAnnotation(Class declaredClass, String methodName) throws NoSuchMethodException {
        if (methodName.charAt(0) == '<') return false;
        for (Method m : declaredClass.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                return m.getAnnotation(CallerSensitive) != null;
            }
        }
        throw new NoSuchMethodException(declaredClass + "::" + methodName);
    }
}
