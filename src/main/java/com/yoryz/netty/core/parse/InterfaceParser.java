package com.yoryz.netty.core.parse;

import com.yoryz.netty.annotation.Controller;
import com.yoryz.netty.annotation.Get;
import com.yoryz.netty.annotation.Post;
import com.yoryz.netty.core.scan.AbstractPackageScanner;
import com.yoryz.netty.core.scan.ComponentFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Single instance
 * initialize the controller instance map
 * initialize the uri map
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/17 15:21
 */
public class InterfaceParser {

    private final Logger logger = LogManager.getLogger(InterfaceParser.class);

    private final AbstractPackageScanner componentFactory = ComponentFactory.getInstance();


    /**
     * store the controller with it's method with parameter names mapping
     * such as: UserController => {
     *                              userLogin => {userName, password}
     *                              print => {str, num}
     *                            }
     */
    private final Map<String, Api[]> apiMap;

    /**
     * store base uri to it's controller's absolute name
     * such as: user => com.yoryz.netty.controller.Controller
     */
    private final Map<String, String> baseUriMap;

    /**
     * for single instance
     */
    private final static InterfaceParser INSTANCE = new InterfaceParser();

    private InterfaceParser(){

        baseUriMap = new HashMap<>();

        apiMap = new HashMap<>();

        initControllerInterface();

        logger.info("Resolving classes solve.");
    }

    public static InterfaceParser getInstance() {
        return INSTANCE;
    }


    /**
     * initial controller's clazzMap, parse the controller's interface's parameters' name
     * only the method has @Post or @Get annotation can be an interface, which in the class
     * with @Controller annotation
     */
    private void initControllerInterface() {
        logger.info("initializing controller's interface");

        try {
            for (Class<?> ctlerClass : componentFactory.getControllers()) {

                Annotation ctlerAnno = ctlerClass.getAnnotation(Controller.class);
                // jump the controller class doesn't have @Controller annotation
                if (ctlerAnno == null) {
                    continue;
                }

                // store: methods name => parameters name
                Map<String, String[]> member = new HashMap<>();

                // get the controller's method's parameters name
                new ClassReader(ctlerClass.getName()).accept(
                        new ParameterNameVisitor(member), AsmInfo.ASM_VERSION);

                List<Api> apiList = new ArrayList<>();

                // this is controller's method's name set
                for (String memberName : member.keySet()) {

                    Method[] methods = ctlerClass.getDeclaredMethods();
                    for (Method method : methods) {
                        // found the matched method name
                        if (method.getName().equals(memberName)) {

                            Annotation[] annotations = method.getDeclaredAnnotations();
                            for (Annotation annotation : annotations) {
                                // found an interface
                                if (annotation instanceof Post) {
                                    apiList.add(Api.apiPost(
                                            ((Post) annotation).value(),
                                            memberName,
                                            method.getParameterTypes(),
                                            member.get(memberName)));

                                } else if (annotation instanceof Get) {
                                    apiList.add(Api.apiGet(
                                            ((Get) annotation).value(),
                                            memberName,
                                            method.getParameterTypes(),
                                            member.get(memberName)));

                                }
                            }
                        }
                    }
                }

                baseUriMap.put(((Controller) ctlerAnno).value(), ctlerClass.getName());
                apiMap.put(((Controller) ctlerAnno).value(), apiList.toArray(new Api[0]));
            }
        } catch (Exception e) {
            throw new RuntimeException("Parse interface failed.");
        }
    }

    /**
     * get controller's interface parameter name map
     *
     * @param controllerName controller name, UserController
     * @return method parameters name map
     */
    public Api[] getMethodMap(String controllerName) {
        return apiMap.get(controllerName);
    }

    /**
     * get controller class' absolute name
     *
     * @param baseUri the base uri, such as /user
     * @return controller absolute name
     */
    public String getControllerAbsoluteName(String baseUri) {
        return baseUriMap.get(baseUri);
    }

}
