package com.chitucode.wwf.util;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class类工具
 * 
 */
public class ClassUtils {

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean hasClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getClassAnnotation(clazz, annotationClass) != null;
    }

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @param fieldName
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean hasFieldAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass, String fieldName) throws Exception {
        return getFieldAnnotation(clazz, annotationClass, fieldName) != null;
    }

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @param methodName
     *            a {@link java.lang.String} object.
     * @param paramType
     *            a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean hasMethodAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass, String methodName, Class<?>... paramType) throws Exception {
        return getMethodAnnotation(clazz, annotationClass, methodName, paramType) != null;
    }

    /**
     * 获取类注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @return a A object.
     */
    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * 获取类成员注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @param fieldName
     *            成员属性名
     * @return a A object.
     */
    public static <A extends Annotation> A getFieldAnnotation(Class<?> clazz,
            Class<A> annotationClass, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {
                throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
            }
            return field.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: field[" + fieldName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
        }
    }

    /**
     * 获取类方法上的注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @param methodName
     *            方法名
     * @param paramType
     *            方法参数
     * @return a A object.
     */
    public static <A extends Annotation> A getMethodAnnotation(Class<?> clazz,
            Class<A> annotationClass, String methodName, Class<?>... paramType)
            throws Exception {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            if (method == null) {
                throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName());
            }
            return method.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception("no such method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        }
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pagekageName
     *            包名
     * @param recursive
     *            是否递归
     * @return a {@link java.util.Set} object.
     */
    public static Set<Class<?>> getClasses(String pagekageName, boolean recursive) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 获取包的名字 并进行替换
        String packageName = pagekageName;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                            // log
                                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     *            a {@link java.lang.String} object.
     * @param packagePath
     *            a {@link java.lang.String} object.
     * @param recursive
     *            a boolean.
     * @param classes
     *            a {@link java.util.Set} object.
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
            String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>
     * Description：给一个接口，返回这个接口同一个包下的所有实现类
     * </p>
     *
     * @param c
     *            a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c) {
        List<Class<?>> returnClassList = new ArrayList<Class<?>>(); // 返回结果
        // 如果不是一个接口，则不做处理
        if (!c.isInterface()) {
            return returnClassList;
        }
        String packageName = c.getPackage().getName(); // 获得当前的包名
        Set<Class<?>> allClass = getClasses(packageName, true); // 获得当前包下以及子包下的所有类
        // 判断是否是同一个接口
        for (Class<?> clazz : allClass) {
            if (c.isAssignableFrom(clazz)) { // 判断是不是一个接口
                if (!c.equals(clazz)) { // 本身不加进去
                    returnClassList.add(clazz);
                }
            }
        }
        return returnClassList;
    }

    public static Set<Class<?>> getAllClassByInterface(Class<?> c,String packageName,final boolean recursive) {
        Set<Class<?>> returnClassList = new HashSet<>(); // 返回结果
        // 如果不是一个接口，则不做处理
        if (!c.isInterface()) {
            return returnClassList;
        }
        //String packageName = c.getPackage().getName(); // 获得当前的包名
        Set<Class<?>> allClass = getClasses(packageName, recursive); // 获得当前包下以及子包下的所有类
        // 判断是否是同一个接口
        for (Class<?> clazz : allClass) {
            if (c.isAssignableFrom(clazz)) { // 判断是不是一个接口
                if (!c.equals(clazz)) { // 本身不加进去
                    returnClassList.add(clazz);
                }
            }
        }
        return returnClassList;
    }

    /**
     * 得到方法参数名称数组
     * 由于java没有提供获得参数名称的api，利用了javassist来实现
     * @return
     */
    public static String[] getMethodParamNames(Class<?> clazz, Method method) {
        Assert.notNull(clazz,"class can not be null");
        Assert.notNull(method,"method can not be null");
        try {
            ClassPool pool = ClassPool.getDefault();


            pool.insertClassPath(new ClassClassPath(clazz));

            CtClass cc = pool.get(clazz.getName());

            //DEBUG, 函数名相同的方法重载的信息读不到
            //CtMethod cm = cc.getDeclaredMethod(method.getName());
            CtMethod cm = cc.getDeclaredMethod(method.getName());
//			String[] paramTypeNames = new String[method.getParameterTypes().length];
//			for (int i = 0; i < paramTypes.length; i++)
//	            paramTypeNames[i] = paramTypes[i].getName();
//			CtMethod cm = cc.getDeclaredMethod(method.getName(), pool.get(new String[] {}));

            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();

            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                    .getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new RuntimeException("class:"+clazz.getName()
                        +", have no LocalVariableTable, please use javac -g:{vars} to compile the source file");
            }

//			for(int i  = 0 ; i< attr.length() ; i++){
//				System.out.println(i);
//				try {
//					System.out.println("===="+attr.nameIndex(i));
//					System.out.println("===="+attr.index(i));
////					System.out.println("===="+attr.nameIndex(i));
//					System.out.println(clazz.getName()+"================"+i+attr.variableName(i));
//
//
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
            //addContextVariable by lzw 用于兼容jdk 编译时 LocalVariableTable顺序问题
            int startIndex = getStartIndex(attr);
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

            for (int i = 0; i < paramNames.length; i++)
                paramNames[i] = attr.variableName(startIndex + i + pos);
            // paramNames即参数名
            for (int i = 0; i < paramNames.length; i++) {
                System.out.println(paramNames[i]);
            }

            return paramNames;

        } catch (NotFoundException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static int getStartIndex(LocalVariableAttribute attr){

//		attr.st

        int startIndex = 0;
        for(int i  = 0 ; i< attr.length() ; i++){
            if("this".equals(attr.variableName(i))){
                startIndex = i;
                break;
            }
        }
        return startIndex;
    }

    /**
     * 是否为java原生类型
     * @param cls
     * @return
     */
    public static boolean isPrimitives(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls) || cls == String.class;
    }

    public static Class<?> getBoxedClass(Class<?> c) {
        if( c == int.class )
            c = Integer.class;
        else if( c == boolean.class )
            c = Boolean.class;
        else  if( c == long.class )
            c = Long.class;
        else if( c == float.class )
            c = Float.class;
        else if( c == double.class )
            c = Double.class;
        else if( c == char.class )
            c = Character.class;
        else if( c == byte.class )
            c = Byte.class;
        else if( c == short.class )
            c = Short.class;
        return c;
    }

    public static Object getPrimitiveObject(Class<?> clazz,String value){

        Object result = new Object();
        Class c = getBoxedClass(clazz);

        if( c == Integer.class ){
            result = Integer.parseInt(value);
        }else if( c == Boolean.class ){
            result = Boolean.parseBoolean(value);
        }else if(c == Long.class ){
            result = Long.parseLong(value);
        }else if( c == Float.class ){
            result = Float.parseFloat(value);
        }else if( c == Double.class ){
            result = Double.parseDouble(value);
        }else if( c == Character.class ){
            result = value.charAt(0);
        }else if( c == Byte.class ){

        }else if( c == Short.class ){
            result = Short.parseShort(value);
        }else if( c == String.class){
            result = value;
        }

        return result;
    }

}