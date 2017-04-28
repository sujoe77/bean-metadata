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

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Andrii Frunt
 */
public interface Typed {
    Class<?> getType();

    default boolean typeIs(Class<?> type) {
        return type.equals(getType());
    }

    default boolean typeIsAssignableFrom(Class<?> cl) {
        return typeIs(cl) || getType().isAssignableFrom(cl) || isCompatiblePrimitives(getType(), cl);
    }

    default boolean typeNameIs(String name) {
        return getType().getName().equals(name);
    }

    default boolean isCompatiblePrimitives(Class<?> fieldType, Class<?> cl) {
        return ClassUtil.isCompatiblePrimitives(fieldType, cl);
    }

    default String getTypeName() {
        return getType().getName();
    }

    default String getSimpleTypeName() {
        return getType().getSimpleName();
    }

    default boolean isString() {
        return typeIs(String.class);
    }

    default boolean isNumber() {
        return Number.class.isAssignableFrom(getType());
    }

    default boolean isFractional() {
        return isNumber() && (isDouble() || isBigDecimal() || isFloat());
    }

    default boolean isShort() {
        return typeIs(Short.class) || isPrimitiveWithName("short");
    }

    default boolean isInteger() {
        return typeIs(Integer.class) || isPrimitiveWithName("int");
    }

    default boolean isDouble() {
        return typeIs(Double.class) || isPrimitiveWithName("double");
    }

    default boolean isLong() {
        return typeIs(Long.class) || isPrimitiveWithName("long");
    }

    default boolean isFloat() {
        return typeIs(Float.class) || isPrimitiveWithName("float");
    }

    default boolean isByte() {
        return typeIs(Byte.class) || isPrimitiveWithName("byte");
    }

    default boolean isBoolean() {
        return typeIs(Boolean.class) || isPrimitiveWithName("boolean");
    }

    default boolean isBigDecimal() {
        return typeIs(BigDecimal.class);
    }

    default boolean isBigInteger() {
        return typeIs(BigInteger.class);
    }

    default boolean isDate() {
        return typeIs(Date.class);
    }

    default boolean isPrimitiveWithName(String name) {
        return isPrimitive() && typeNameIs(name);
    }

    default boolean isPrimitive() {
        return getType().isPrimitive();
    }

    default int getTypeModifiers() {
        return getType().getModifiers();
    }

    default boolean isAbstract() {
        return Modifier.isAbstract(getTypeModifiers());
    }
}
