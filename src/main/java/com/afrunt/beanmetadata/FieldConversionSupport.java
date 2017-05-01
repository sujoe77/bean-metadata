/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.afrunt.beanmetadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Andrii Frunt
 */
public interface FieldConversionSupport<BM extends BeanMetadata<FM>, FM extends FieldMetadata> {

    default Map<Integer, Method> getMethodsCache() {
        return new HashMap<>();
    }

    default Method getConverterMethod(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        int methodHashCode = getMehodHashCode(methodName, fromType, toType, otherParamTypes);

        Map<Integer, Method> methodsCache = getMethodsCache();
        if (methodsCache.containsKey(methodHashCode)) {
            return methodsCache.get(methodHashCode);
        }

        List<Class<?>> paramTypes = new ArrayList<>();
        paramTypes.add(fromType);
        paramTypes.addAll(otherParamTypes);

        Method[] methods = getClass().getMethods();

        Method method = Arrays.stream(methods)
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> methodParametersTypesAre(m, paramTypes))
                .filter(m -> m.getReturnType().equals(toType))
                .findFirst()
                .orElse(null);

        methodsCache.put(methodHashCode, method);

        return method;
    }

    default int getMehodHashCode(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes) {
        int methodHashCode = 31 * methodName.hashCode();
        methodHashCode = methodHashCode * 31 + fromType.hashCode();
        methodHashCode = methodHashCode * 31 + toType.hashCode();
        return methodHashCode * 31 + otherParamTypes.hashCode();
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

        if (converterMethod == null && value != null && toType.equals(String.class)) {
            return String.valueOf(value);
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
