package com.yoryz.netty.core.scan;

import com.yoryz.netty.annotation.Component;
import com.yoryz.netty.annotation.Controller;
import com.yoryz.netty.annotation.Dao;
import com.yoryz.netty.annotation.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/19 12:11
 */
public class ComponentFactory extends AbstractPackageScanner {

    private final Logger logger = LogManager.getLogger(ComponentFactory.class);

    private static final ComponentFactory INSTANCE = new ComponentFactory();

    private ComponentFactory() {
        instantMap = new HashMap<>();

        clazzMap = new HashMap<>();

        controllerClazz = new ArrayList<>();
    }

    public static ComponentFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public void doDealClass(Class<?> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        try {
            for (Annotation anno : annotations) {
                if (anno instanceof Controller) {
                    controllerClazz.add(clazz);
                }

                if (anno instanceof Controller || anno instanceof Service ||
                        anno instanceof Dao || anno instanceof Component) {
                    clazz.getConstructor().setAccessible(true);
                    clazzMap.put(clazz.getName(), clazz);
                    instantMap.put(clazz.getName(), clazz.newInstance());
                }

                logger.info("Resolving {}", clazz.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
