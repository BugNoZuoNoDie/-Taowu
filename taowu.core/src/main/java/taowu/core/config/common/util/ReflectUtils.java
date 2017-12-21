package taowu.core.config.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Java反射工具类
 * @author <a href="xdemo.org">xdemo.org</a>
 */
public class ReflectUtils {

    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";


    /**
     * 获取ClassName
     * @param type
     * @return
     */
    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
        }
        if (className.endsWith(";")) {
            className = className.substring(0, className.length() - 1);
        }
        if (className.indexOf("<") != -1) {
            className = className.substring(0, className.indexOf("<"));
        }
        return className;
    }

    /**
     * 获取CLASS
     * @param type
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        if (className.startsWith("[L")) {
            return (Class) type;
        }
        return Class.forName(className);
    }


    /**
     * 获取成员变量的修饰符
     * @param clazz
     * @param field
     * @return
     * @throws Exception
     */
    public static <T> int getFieldModifier(Class<T> clazz, String field) throws Exception {
        //getDeclaredFields可以获取所有修饰符的成员变量，包括private,protected等getFields则不可以
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(field)) {
                return fields[i].getModifiers();
            }
        }
        throw new Exception(clazz+" has no field \"" + field + "\"");
    }

    /**
     * 获取成员方法的修饰符
     * @param clazz
     * @param method
     * @return
     * @throws Exception
     */
    public static <T> int getMethodModifier(Class<T> clazz, String method) throws Exception {

        //getDeclaredMethods可以获取所有修饰符的成员方法，包括private,protected等getMethods则不可以
        Method[] m = clazz.getDeclaredMethods();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals(m)) {
                return m[i].getModifiers();
            }
        }
        throw new Exception(clazz+" has no method \"" + m + "\"");
    }

    /**
     *  [对象]根据成员变量名称获取其值
     * @param clazzInstance
     * @param field
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> Object getFieldValue(Object clazzInstance, Object field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Field[] fields = clazzInstance.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(field)) {
                //对于私有变量的访问权限，在这里设置，这样即可访问Private修饰的变量
                fields[i].setAccessible(true);
                return fields[i].get(clazzInstance);
            }
        }

        return null;
    }

    /**
     *[类]根据成员变量名称获取其值（默认值）
     * @param clazz
     * @param field
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static <T> Object getFieldValue(Class<T> clazz, String field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException {

        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(field)) {
                //对于私有变量的访问权限，在这里设置，这样即可访问Private修饰的变量
                fields[i].setAccessible(true);
                return fields[i].get(clazz.newInstance());
            }
        }

        return null;
    }

    /**
     * 获取所有的成员变量
     * @param clazz
     * @return
     */
    public static <T> String[] getFields(Class<T> clazz) {

        Field[] fields = clazz.getDeclaredFields();

        String[] fieldsArray = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldsArray[i] = fields[i].getName();
        }

        return fieldsArray;
    }

    /**
     * 获取所有的成员变量,包括父类
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> Field[] getClassFieldsAndSuperClassFields(Class<T> clazz) throws Exception {

        Field[] fields = clazz.getDeclaredFields();

        if(clazz.getSuperclass()==null){
            throw new Exception(clazz.getName()+"没有父类");
        }

        Field[] superFields = clazz.getSuperclass().getDeclaredFields();


        Field[] allFields=new Field[fields.length+superFields.length];

        for(int i=0;i<fields.length;i++){
            allFields[i]=fields[i];
        }
        for(int i=0;i<superFields.length;i++){
            allFields[fields.length+i]=superFields[i];
        }

        return allFields;
    }

    /**
     * 获取方法
     * @param clazz
     * @param name
     * @param parameterTypes
     * @return
     */
    public static Method getMethodNoException(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Set方法
     * @param clazz
     * @param field
     * @return
     */
    public static Method getPlainSetMethod(Class<?> clazz, Field field) {
        Method method = null;
        if (field.getModifiers() == 2) { // pravite 方法
            method = getMethodNoException(clazz, "set" + StringUtils.firstLetterToUpperCase(field.getName()), field.getType());
            if (method == null) {
                method = getMethodNoException(clazz, "set" + field.getName(), field.getType());
            }
        }
        if (method != null && method.getModifiers() == 1) { // public 方法
            return method;
        }
        return null;
    }

    /**
     * 获取Get方法
     * @param clazz
     * @param field
     * @return
     */
    public static Method getPlainGetMethod(Class<?> clazz, Field field) {
        Method method = null;
        if (field.getModifiers() == 2) { // pravite 方法
            method = getMethodNoException(clazz, "get" + StringUtils.firstLetterToUpperCase(field.getName()));
            if (method == null) {
                method = getMethodNoException(clazz, "is" + StringUtils.firstLetterToUpperCase(field.getName()));
            }
            if (method == null) {
                method = getMethodNoException(clazz, "has" + StringUtils.firstLetterToUpperCase(field.getName()));
            }
            if (method == null) {
                method = getMethodNoException(clazz, "get" + field.getName());
            }
            if (method == null) {
                method = getMethodNoException(clazz, "is" + field.getName());
            }
            if (method == null) {
                method = getMethodNoException(clazz, "has" + field.getName());
            }
        }
        if (method != null && method.getModifiers() == 1) { // public 方法
            return method;
        }
        return null;
    }


    /**
     * 指定类，调用指定的无参方法
     * @param clazz
     * @param method
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T> Object invoke(Class<T> clazz, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        Object instance = clazz.newInstance();
        Method m = clazz.getMethod(method, new Class[] {});
        return m.invoke(instance, new Object[] {});
    }

    /**
     * 通过对象，访问其方法
     *
     * @param clazzInstance
     * @param method
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T> Object invoke(Object clazzInstance, String method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        Method m = clazzInstance.getClass().getMethod(method, new Class[] {});
        return m.invoke(clazzInstance, new Object[] {});
    }

    /**
     * 指定类，调用指定的方法
     *
     * @param clazz
     * @param method
     * @param paramClasses
     * @param params
     * @return Object
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> Object invoke(Class<T> clazz, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Object instance = clazz.newInstance();
        Method _m = clazz.getMethod(method, paramClasses);
        return _m.invoke(instance, params);
    }

    /**
     * 通过类的实例，调用指定的方法
     *
     * @param clazzInstance
     * @param method
     * @param paramClasses
     * @param params
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> Object invoke(Object clazzInstance, String method, Class<T>[] paramClasses, Object[] params) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method _m = clazzInstance.getClass().getMethod(method, paramClasses);
        return _m.invoke(clazzInstance, params);
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        // getFields(User.class);
//        User u = new User();
//        invoke(u, "setName", new Class[] { String.class }, new Object[] { "xx发大水法大水法x" });
//        System.out.println(getFieldValue(u, "name"));
    }

}
