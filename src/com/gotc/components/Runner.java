package com.gotc.components;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by srikaram on 12-Nov-16.
 */
public class Runner {

    public static boolean run(String className) {
        try {
            File file = new File(className + ".class");
            if (!file.exists()) {
                return false;
            }
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);
            Class<?> clazz = cl.loadClass(className);
            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, (Object) null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
