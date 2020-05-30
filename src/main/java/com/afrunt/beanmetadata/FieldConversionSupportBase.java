package com.afrunt.beanmetadata;

import java8.util.function.Predicate;
import java8.util.stream.StreamSupport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FieldConversionSupportBase<BM extends BeanMetadata<FM>, FM extends FieldMetadata> implements FieldConversionSupport<BM, FM> {
    @Override
    public Map<Integer, Method> getMethodsCache() {
        return new HashMap<>();
    }

    @Override
    public Map<Integer, String> getMethodNamesCache() {
        return new HashMap<>();
    }

    @Override
    public Method getConverterMethod(final String methodName, Class<?> fromType, final Class<?> toType, List<Class<?>> otherParamTypes) {
        int methodHashCode = getMethodHashCode(methodName, fromType, toType, otherParamTypes);

        Map<Integer, Method> methodsCache = getMethodsCache();
        Method method = methodsCache.get(methodHashCode);

        if (method != null || methodsCache.containsKey(methodHashCode)) {
            return method;
        }

        final List<Class<?>> paramTypes = new ArrayList<>();
        paramTypes.add(fromType);
        paramTypes.addAll(otherParamTypes);

        Method[] methods = getClass().getMethods();
        method = StreamSupport.stream(Arrays.asList(methods)).filter(new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                return method.getName().equals(methodName)
                        && methodParametersTypesAre(method, paramTypes)
                        && method.getReturnType().equals(toType);
            }
        }).findFirst().orElse(null);

        methodsCache.put(methodHashCode, method);

        return method;
    }

    @Override
    public int getMethodHashCode(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        int methodHashCode = 31 * methodName.hashCode();
        methodHashCode = methodHashCode * 31 + fromType.hashCode();
        methodHashCode = methodHashCode * 31 + toType.hashCode();
        return methodHashCode * 31 + otherParamTypes.hashCode();
    }

    @Override
    public Object fieldToValue(Object value, Class toType, BM beanMetadata, FM fieldMetadata) {
        Class<?> fromType = fieldMetadata.getType();
        Class<?> class1 = beanMetadata.getClass();
        Class<?> class2 = fieldMetadata.getClass();
        return convert("field", value, beanMetadata, fieldMetadata, fromType, toType,
                Arrays.asList(class1, class2));
    }

    @Override
    public Object valueToField(Object value, Class<?> fromType, BM beanMetadata, FM fieldMetadata) {
        Class<?> toType = fieldMetadata.getType();
        Class<?> class1 = beanMetadata.getClass();
        Class<?> class2 = fieldMetadata.getClass();
        return convert("value", value, beanMetadata, fieldMetadata, fromType, toType,
                Arrays.asList(class1, class2));
    }

    @Override
    public Object convert(String what, Object value, BM beanMetadata, FM fieldMetadata, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        String converterMethodName = getConverterMethodName(what, fromType, toType);
        Method converterMethod = getConverterMethod(converterMethodName, fromType, toType, otherParamTypes);

        boolean sameType = fromType.equals(toType);

        if (converterMethod == null && sameType) {
            return value;
        }

        if (converterMethod == null && value != null && toType.equals(String.class)) {
            return String.valueOf(value);
        }

        if (converterMethod == null) {
            String methodString = methodString(converterMethodName, fromType, toType, otherParamTypes);
            throw new BeanMetadataException(what + " converter method not found " + methodString);
        }

        return executeConverterMethod(converterMethod, value, beanMetadata, fieldMetadata);
    }

    @Override
    public String getConverterMethodName(String whatToConvert, Class<?> fromType, Class<?> toType) {
        int methodNameHashCode = whatToConvert.hashCode() * 31;
        methodNameHashCode = methodNameHashCode * 31 + fromType.hashCode();
        methodNameHashCode = methodNameHashCode * 31 + toType.hashCode();

        Map<Integer, String> methodNamesCache = getMethodNamesCache();
        String methodName = methodNamesCache.get(methodNameHashCode);
        if (methodName != null || methodNamesCache.containsKey(methodNameHashCode)) {
            return methodName;
        }

        methodName = whatToConvert + fromType.getSimpleName() + "To" + toType.getSimpleName();
        methodNamesCache.put(methodNameHashCode, methodName);
        return methodName;
    }

    @Override
    public boolean methodParametersTypesAre(Method method, Class<?>... paramTypes) {
        return methodParametersTypesAre(method, Arrays.asList(paramTypes));
    }

    @Override
    public boolean methodParametersTypesAre(Method method, List<Class<?>> paramTypes) {
        return Arrays.asList(method.getParameterTypes()).equals(paramTypes);
    }

    @Override
    public Object executeConverterMethod(Method m, Object... params) {
        try {
            return m.invoke(this, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanMetadataException("Error during converter method invocation", e);
        }
    }

    @Override
    public String methodString(String name, Class<?> fromType, Class<?> toType, List<Class<?>> paramTypes) {
        StringBuilder sb = new StringBuilder(toType.getSimpleName())
                .append(" ")
                .append(name)
                .append("(");

        sb
                .append(fromType.getSimpleName());

        for (Class<?> paramType : paramTypes) {

            sb
                    .append(", ")
                    .append(paramType.getSimpleName());
        }

        sb.append(")");
        return sb.toString();
    }
}
