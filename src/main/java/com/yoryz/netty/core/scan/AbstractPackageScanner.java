package com.yoryz.netty.core.scan;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/19 11:59
 */
public abstract class AbstractPackageScanner {

    public abstract void doDealClass(Class<?> clazz);

    public void scan(Class<?> clazz) {
        scan(clazz.getPackage().getName());
    }

    public void scan(String packageName) {
        String packagePath = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resource = classLoader.getResources(packagePath);
            while (resource.hasMoreElements()) {
                URL url = resource.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    scan(url);
                } else {
                    File file = new File(url.toURI());
                    if (!file.exists()) {
                        continue;
                    }
                    scan(packageName, file);
                }
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * scan the jar file
     *
     * @param url the url of jar file
     * @throws IOException can not open the url of the jar file
     */
    private void scan(URL url) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> jarEntries = jarFile.entries();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarName = jarEntry.getName();
            if (jarEntry.isDirectory() || !jarName.endsWith(".class")) {
                continue;
            }

            String className = jarName.replace(".class", "").replaceAll("/", ".");
            dealClazz(className);
        }
    }

    /**
     * scan the normal class file
     *
     * @param packageName the package of the class file
     * @param currentFile the class file
     */
    private void scan(String packageName, File currentFile) {
        File[] files = currentFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return pathname.getName().endsWith(".class");
            }
        });

        for (File file : files) {
            if (file.isDirectory()) {
                // recurring scanning
                scan(packageName + "." + file.getName(), file);
            } else {
                String fileName = file.getName().replace(".class", "");
                String className = packageName + "." + fileName;
                dealClazz(className);
            }
        }
    }

    private void dealClazz(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotation() || clazz.isInterface() ||
                    clazz.isEnum() || clazz.isPrimitive()) {
                return;
            }
            doDealClass(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
