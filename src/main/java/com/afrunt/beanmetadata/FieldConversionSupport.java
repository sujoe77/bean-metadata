package com.afrunt.beanmetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrii Frunt
 */
public interface FieldConversionSupport<BM extends BeanMetadata<FM>, FM extends FieldMetadata> {

    default Method getConverterMethod(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        List<Class<?>> paramTypes = new ArrayList<>();
        paramTypes.add(fromType);
        paramTypes.addAll(otherParamTypes);

        Method[] methods = getClass().getMethods();

        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> methodParametersTypesAre(m, paramTypes))
                .filter(m -> m.getReturnType().equals(toType))
                .findFirst()
                .orElse(null);
    }

    default Object fieldToValue(Object value, Class<?> toType, BM beanMetadata, FM fieldMetadata) {
        Class<?> fromType = fieldMetadata.getType();
        return convert("field", value, beanMetadata, fieldMetadata, fromType, toType,
                Arrays.asList(beanMetadata.getClass(), fieldMetadata.getClass()));
    }

    default Object valueToField(Object value, Class<?> fromType, BM beanMetadata, FM fieldMetadata) {
        Class<?> toType = fieldMetadata.getType();
        return convert("value", value, beanMetadata, fieldMetadata, fromType, toType,
                Arrays.asList(beanMetadata.getClass(), fieldMetadata.getClass()));
    }

    default Object convert(String what, Object value, BM beanMetadata, FM fieldMetadata, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        String converterMethodName = getConverterMethodName(what, fromType, toType);
        Method converterMethod = getConverterMethod(converterMethodName, fromType, toType, otherParamTypes);

        boolean sameType = fromType.equals(toType);

        if (converterMethod == null && sameType) {
            return value;
        }

        if (converterMethod == null) {
            String methodString = methodString(converterMethodName, fromType, toType, otherParamTypes);
            throw new BeanMetadataException(what + " converter method not found " + methodString);
        }

        return executeConverterMethod(converterMethod, value, beanMetadata, fieldMetadata);
    }

    default String getConverterMethodName(String whatToConvert, Class<?> fromType, Class<?> toType) {
        return whatToConvert + fromType.getSimpleName() + "To" + toType.getSimpleName();
    }

    default boolean methodParametersTypesAre(Method method, Class<?>... paramTypes) {
        return methodParametersTypesAre(method, Arrays.asList(paramTypes));
    }

    default boolean methodParametersTypesAre(Method method, List<Class<?>> paramTypes) {
        return Arrays.asList(method.getParameterTypes()).equals(paramTypes);
    }

    default Object executeConverterMethod(Method m, Object... params) {
        try {
            return m.invoke(this, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanMetadataException("Error during converter method invocation", e);
        }
    }

    default String methodString(String name, Class<?> fromType, Class<?> toType, List<Class<?>> paramTypes) {
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
