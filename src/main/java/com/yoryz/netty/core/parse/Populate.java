package com.yoryz.netty.core.parse;

import com.yoryz.netty.annotation.Inject;
import com.yoryz.netty.core.scan.ComponentFactory;
import com.yoryz.netty.exception.ResourceBeanNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/19 14:31
 */
public class Populate {

    private final Logger logger = LogManager.getLogger(Populate.class);

    private final ComponentFactory componentFactory = ComponentFactory.getInstance();

    private static final Populate INSTANCE = new Populate();

    public static Populate getInstance() {
        return INSTANCE;
    }

    public void populateBean() throws IllegalAccessException {
        Map<String, Class<?>> clazzMap = componentFactory.getClazzMap();

        // iterate all the component
        // inject the fields in them
        for (String component : clazzMap.keySet()) {

            Class<?> tempClazz = clazzMap.get(component);
            for (Field field : tempClazz.getDeclaredFields()) {

                Annotation resourceAnno = field.getAnnotation(Inject.class);
                if (resourceAnno != null) {
                    field.setAccessible(true);

                    try {
                        // inject by name
                        Object injectInstance = componentFactory.getInstance(field.getType().getName());

                        // can not inject by name, inject by type
                        if (injectInstance == null) {
                            for (Class<?> targetClazz : componentFactory.getClazzMap().values()) {

                                // iterate all the implemented interfaces of all the class
                                for (Class<?> targetImplClazz : targetClazz.getInterfaces()) {

                                    // found
                                    // the interface class is equals with the filed's type
                                    if (targetImplClazz.equals(field.getType())) {
                                        injectInstance = componentFactory.getInstance(targetClazz.getName());
                                        break;
                                    }
                                }
                                if (injectInstance != null) {
                                    break;
                                }
                            }
                        }

                        if (injectInstance == null) {
                            throw new ResourceBeanNotFoundException("Can not Inject " +
                                    field.getType().getName() + " on " + component);
                        }

                        field.set(componentFactory.getInstance(component), injectInstance);

                    } catch (IllegalAccessException | ResourceBeanNotFoundException e) {
                        logger.info(e.getMessage());
                        throw e;
                    }
                    logger.info("Inject {} on {}", field.getType().getName(), component);
                }
            }
        }

    }
}
