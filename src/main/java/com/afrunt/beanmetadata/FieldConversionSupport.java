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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Andrii Frunt
 */
public interface FieldConversionSupport<BM extends BeanMetadata<FM>, FM extends FieldMetadata> {

    Map<Integer, Method> getMethodsCache();

    Map<Integer, String> getMethodNamesCache();

    Method getConverterMethod(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes);

    int getMethodHashCode(String methodName, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes);

    Object fieldToValue(Object value, Class toType, BM beanMetadata, FM fieldMetadata);

    Object valueToField(Object value, Class<?> fromType, BM beanMetadata, FM fieldMetadata);

    Object convert(String what, Object value, BM beanMetadata, FM fieldMetadata, Class<?> fromType, Class<?> toType, List<Class<?>> otherParamTypes);

    String getConverterMethodName(String whatToConvert, Class<?> fromType, Class<?> toType);

    boolean methodParametersTypesAre(Method method, Class<?>... paramTypes);

    boolean methodParametersTypesAre(Method method, List<Class<?>> paramTypes);

    Object executeConverterMethod(Method m, Object... params);

    String methodString(String name, Class<?> fromType, Class<?> toType, List<Class<?>> paramTypes);
}
