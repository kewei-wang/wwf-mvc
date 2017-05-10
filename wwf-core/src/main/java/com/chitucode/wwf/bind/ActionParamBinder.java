package com.chitucode.wwf.bind;

import com.chitucode.wwf.context.ActionInfo;
import com.chitucode.wwf.util.*;
import org.apache.commons.lang.time.FastDateFormat;

import javax.servlet.http.HttpServletRequest;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by kowaywang on 17/4/20.
 */
public class ActionParamBinder {

    public static final FastDateFormat WWF_DEFAULT_DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static Object[] bind(HttpServletRequest req, ActionInfo actionInfo) throws InvocationTargetException, IntrospectionException, InstantiationException, ParseException, IllegalAccessException {

        Method method = actionInfo.getMethod();
        Object controllerInstance = actionInfo.getControllerInstance();
        String reqUri = req.getRequestURI();

        PathMatcher matcher = new AntPathMatcher();

        //下面这俩是本次请求从客户端发过来的所有数据
        Map<String, String[]> paramMap = req.getParameterMap();
        Map<String, String> urlParamMap = matcher.extractUriTemplateVariables(actionInfo.getUrlPath(), reqUri);


        List<Object> paramList = new ArrayList<>();

        //获取"方法调用"的参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] parameterNames = ClassUtils.getMethodParamNames(controllerInstance.getClass(), method);

        int parameterTypesSize = parameterTypes.length;
        int parameterNamesSize = parameterNames.length;

        Assert.isTrue(parameterTypesSize == parameterNamesSize, "parameterTypesSize not equals to parameterNamesSize");

        for (int i = 0; i < parameterTypesSize; i++) {

            Class typeClass = parameterTypes[i];
            if (ClassUtils.isPrimitive(typeClass)) {
                //简单类型参数注入(请求路径参数注入)
                typeClass = ClassUtils.getBoxedClass(typeClass);
                Object obj = ClassUtils.getPrimitiveObject(typeClass, urlParamMap.get(parameterNames[i]));
                paramList.add(obj);
            } else {
                //开始注入复杂类型
                try {
                    Object obj = typeClass.newInstance();

                    Field[] fields = typeClass.getDeclaredFields();
                    for (Field field : fields) {

                        String fieldName = field.getName();
                        Class<?> fieldType = field.getType();
                        PropertyDescriptor pd = new PropertyDescriptor(fieldName, typeClass);
                        Method setMethod = pd.getWriteMethod();

                        if (setMethod != null && ClassUtils.isPrimitive(fieldType)) {
                            //String paramValue = urlParamMap.get(fieldName);
                            //Object fieldValue = ClassUtils.getPrimitiveObject(fieldType, paramValue);
                            //setMethod.invoke(obj, new Object[]{fieldValue});

                            String[] paramValues = paramMap.get(fieldName);
                            if (paramValues == null) continue;
                            Object value = new Object();
                            if (paramValues.length == 1) {

                                if (fieldType == String.class) {
                                    value = paramValues[0];
                                } else if (fieldType == Short.class || fieldType == short.class) {
                                    value = Short.parseShort(paramValues[0]);
                                } else if (fieldType == Integer.class || fieldType == int.class) {
                                    value = Integer.parseInt(paramValues[0]);
                                } else if (fieldType == Float.class || fieldType == float.class) {
                                    value = Float.parseFloat(paramValues[0]);
                                } else if (fieldType == Long.class || fieldType == long.class) {
                                    value = Long.parseLong(paramValues[0]);
                                } else if (fieldType == Double.class || fieldType == double.class) {
                                    value = Double.parseDouble(paramValues[0]);
                                } else if (fieldType == Date.class) {

                                    if (paramMap.containsKey("parseDateFormat")) {
                                        String dateFormat = paramMap.get("parseDateFormat")[0];

                                        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                                        value = sdf.parse(paramValues[0]);
                                        sdf = null;

                                    } else {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        try {
                                            value = sdf.parse(paramValues[0]);
                                        } catch (Exception e) {
                                            value = new Object();
                                        } finally {
                                            sdf = null;
                                        }
                                    }

                                }

                            }
                            setMethod.invoke(obj, new Object[]{value});

                        } else if (fieldType.isAssignableFrom(List.class)) {
                            //说明是List或者类似的
                            String[] paramValues = paramMap.get(fieldName);
                            if(paramValues != null)
                            setMethod.invoke(obj, Arrays.asList(paramValues));

                            //Array.
                        } else if (fieldType.isArray()) {
                            Object[] value = new Object[]{};
                            String[] reqValue = paramMap.get(fieldName);
                            if(reqValue != null) {
                                try {
                                    if (fieldType == String[].class) {
                                        value = reqValue;
                                    } else if (fieldType == Short[].class || fieldType == short[].class) {
                                        value = stringArrayToShort(reqValue);
                                    } else if (fieldType == Integer[].class || fieldType == int[].class) {
                                        value = stringArrayToInt(reqValue);
                                    } else if (fieldType == Long[].class || fieldType == long[].class) {
                                        value = stringArrayToLong(reqValue);
                                    } else if (fieldType == Float[].class || fieldType == float[].class) {
                                        value = stringArrayToFloat(reqValue);
                                    } else if (fieldType == Double[].class || fieldType == double[].class) {
                                        value = stringArrayToDouble(reqValue);
                                    }
                                    setMethod.invoke(obj, new Object[]{value});
                                } catch (Exception e) {
                                    //setMethod.invoke(obj, value);
                                    e.printStackTrace();
                                    continue;
                                    //throw e;
                                }
                            }

                            //如果不是primitiveType,则不注入,就放在哪里
                        }

                    }
                    paramList.add(obj);

                } catch (Exception e) {
                    throw e;
                    //paramList.add(new Object());
                }
            }

        }

        return paramList.toArray();
    }

    private static Short[] stringArrayToShort(String[] strArr) {
        Short[] num = new Short[strArr.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = Short.parseShort(strArr[i]);
        }

        return num;
    }

    private static Integer[] stringArrayToInt(String[] strArr) {
        Integer[] num = new Integer[strArr.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = Integer.parseInt(strArr[i]);
        }

        return num;
    }

    private static Long[] stringArrayToLong(String[] strArr) {
        Long[] num = new Long[strArr.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = Long.parseLong(strArr[i]);
        }

        return num;
    }

    private static Float[] stringArrayToFloat(String[] strArr) {
        Float[] num = new Float[strArr.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = Float.parseFloat(strArr[i]);
        }

        return num;
    }

    private static Double[] stringArrayToDouble(String[] strArr) {
        Double[] num = new Double[strArr.length];
        for (int i = 0; i < num.length; i++) {
            num[i] = Double.parseDouble(strArr[i]);
        }
        return num;
    }
}


