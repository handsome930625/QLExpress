package com.ql.util.express;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author wangyijie
 */
public class ExpressClassLoader extends ClassLoader {

    public ExpressClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> loadClass(String name, byte[] code) {
        return this.defineClass(name, code, 0, code.length);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(this, name);
        if (clazz != null) {
            return clazz;
        }
        clazz = parentLoadClass(this, name);
        if (clazz == null && name.startsWith("[")) {
            // 进行数组处理
            int index = name.indexOf("L");
            String str = name.substring(0, index);
            String componentClassName = name.substring(index + 1,
                    name.length() - 1);
            int[] dimes = new int[str.length()];
            for (int i = 0; i < dimes.length; i++) {
                dimes[i] = 0;
            }
            try {
                Class<?> componentType = this.loadClass(componentClassName);
                clazz = Array.newInstance(componentType, dimes).getClass();
            } catch (Exception ignored) {
            }
        }

        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public static Class<?> findLoadedClass(ClassLoader loader, String name)
            throws ClassNotFoundException {
        Method m = null;
        try {
            m = ClassLoader.class.getDeclaredMethod("findLoadedClass",
                    String.class);
            m.setAccessible(true);
            Class<?> result = (Class<?>) m.invoke(loader, new Object[]{name});
            if (result == null) {
                result = (Class<?>) m.invoke(loader.getClass().getClassLoader(),
                        new Object[]{name});
            }
            if (result == null) {
                result = (Class<?>) m.invoke(Thread.currentThread()
                        .getContextClassLoader(), new Object[]{name});
            }
            return result;
        } catch (Exception ex) {
            throw new ClassNotFoundException(ex.getMessage());
        } finally {
            if (m != null) {
                m.setAccessible(false);
            }
        }
    }

    public static Class<?> parentLoadClass(ClassLoader loader, String name)
            throws ClassNotFoundException {
        // 如果存在这个类，则直接返回
        Class<?> clazz = null;
        try {
            clazz = loader.getClass().getClassLoader().loadClass(name);
        } catch (Throwable ignored) {
        }
        if (clazz == null) {
            try {
                clazz = Thread.currentThread().getContextClassLoader()
                        .loadClass(name);
            } catch (Throwable ignored) {
            }
        }
        return clazz;
    }
}
