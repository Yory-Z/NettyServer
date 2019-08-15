package normal;

import com.yoryz.netty.annotation.Component;
import com.yoryz.netty.annotation.Service;
import com.yoryz.netty.controller.UserController;
import com.yoryz.netty.core.parse.Populate;
import com.yoryz.netty.core.scan.AbstractPackageScanner;
import com.yoryz.netty.core.scan.ComponentFactory;
import com.yoryz.netty.service.UserService;
import com.yoryz.netty.service.impl.UserServiceImpl;
import com.yoryz.netty.util.MyResponse;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 16:12
 */
public class NormalTest {

    @Test
    public void normalTest() {

    }

    @Test
    public void getMethodParamNames() throws IOException {

        Method[] methods = UserController.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
            System.out.println();
        }
    }

    @Test
    public void clazzAnnotationTest() throws Exception {
        Annotation[] annotations = UserController.class.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            Class type = annotation.annotationType();
            type.getDeclaredAnnotations();
            if (annotation instanceof Component) {
                System.out.println(annotation.toString());
            }
        }
    }

    @Test
    public void injectTest() throws IllegalAccessException, InstantiationException {
        Annotation annotation = UserServiceImpl.class.getAnnotation(Service.class);
        if (annotation != null) {
            System.out.println(annotation.toString());
            UserService userService = UserServiceImpl.class.newInstance();

            UserController userController = UserController.class.newInstance();

            Field[] fields = UserController.class.getDeclaredFields();
            for (Field field : fields) {
                Annotation fieldAnno = field.getAnnotation(Resource.class);
                System.out.println(field.getName());
                System.out.println(field.getType());
                System.out.println(field.getType().getName());
                System.out.println(field.getType().getTypeName());
                System.out.println();
                if (fieldAnno != null && field.getType().isAssignableFrom(UserServiceImpl.class)) {

                    field.setAccessible(true);
                    field.set(userController, userService);
                }
            }

//            userController.print();
        }
    }

    @Test
    public void scanPackageTest() throws Exception {
        AbstractPackageScanner scanner = ComponentFactory.getInstance();
        scanner.scan("com.yoryz.netty");

        Populate.getInstance().populateBean();

        String absName = "com.yoryz.netty.controller.UserController";
        MyResponse res = (MyResponse) Class.forName(absName)
                .getMethod("userLogin", new Class[]{String.class, String.class})
                .invoke(((ComponentFactory) scanner).getInstance(absName), "Yory", "123456");
        System.out.println(res);
    }

    @Test
    public void superClassTest() {
        Type superclass = UserServiceImpl.class.getGenericSuperclass();
        System.out.println(superclass.getTypeName());

        Type[] superInterface = UserServiceImpl.class.getGenericInterfaces();
        for (Type type : superInterface) {
            System.out.println(type.getTypeName());
            System.out.println(type.getTypeName().substring(type.getTypeName().lastIndexOf(".") + 1));
        }
    }

    @Test
    public void testInterfaceConcurrency() {

    }


}
