package httpserver.itf.impl;

import httpserver.itf.HttpRicmlet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.HashMap;

import static httpserver.itf.impl.HttpServer.RICMLET_URL_BASE;

public class Application {
    // Hashmap (key= app name & value = class loader)
    HashMap<String, URLClassLoader> apps = new HashMap<>();

    HttpRicmlet getInstance(String className, String appName, ClassLoader parent) throws ClassNotFoundException, MalformedURLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, URISyntaxException {
        Constructor<?> constructor;
        Class<?> ricmletClass;
        if (apps.containsKey(appName)) {
            URLClassLoader classLoader = apps.get(appName);
            ricmletClass = classLoader.loadClass(className);
        } else {
            // Load class from .jar
            URL jarURL = new URI("file://./" + appName + ".jar").toURL();
            URLClassLoader newClassLoader = new URLClassLoader(new URL[]{jarURL}, parent);
            apps.put(appName, newClassLoader);
            ricmletClass = newClassLoader.loadClass(className);
        }
        constructor = ricmletClass.getDeclaredConstructor();
        return (HttpRicmlet) constructor.newInstance();
    }
}
