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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrii Frunt
 */
public class FieldMetadata implements Annotated {
    private String name;
    private Class<?> fieldType;
    private Method getter;
    private Method setter;
    private Set<Annotation> annotations = new HashSet<>();
    private String recordClassName;

    public String getName() {
        return name;
    }

    public FieldMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public FieldMetadata setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public Method getGetter() {
        return getter;
    }

    public FieldMetadata setGetter(Method getter) {
        this.getter = getter;
        return this;
    }

    public Method getSetter() {
        return setter;
    }

    public FieldMetadata setSetter(Method setter) {
        this.setter = setter;
        return this;
    }

    public boolean isReadOnly() {
        return setter == null;
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public FieldMetadata setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    public String getRecordClassName() {
        return recordClassName;
    }

    public FieldMetadata setRecordClassName(String recordClassName) {
        this.recordClassName = recordClassName;
        return this;
    }

    public boolean isString() {
        return typeIs(String.class);
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(getFieldType());
    }

    public boolean isShort() {
        return typeIs(Short.class) || isPrimitiveWithName("short");
    }

    public boolean isInteger() {
        return typeIs(Integer.class) || isPrimitiveWithName("int");
    }

    public boolean isDouble() {
        return typeIs(Double.class) || isPrimitiveWithName("double");
    }

    public boolean isBigInteger() {
        return typeIs(BigInteger.class);
    }

    public boolean isLong() {
        return typeIs(Long.class) || isPrimitiveWithName("long");
    }

    public boolean isFloat() {
        return typeIs(Float.class) || isPrimitiveWithName("float");
    }

    public boolean isByte() {
        return typeIs(Byte.class) || isPrimitiveWithName("byte");
    }

    public boolean isBoolean() {
        return typeIs(Boolean.class) || isPrimitiveWithName("boolean");
    }

    public boolean isBigDecimal() {
        return typeIs(BigDecimal.class);
    }

    public boolean isDate() {
        return typeIs(Date.class);
    }

    public boolean isPrimitiveWithName(String name) {
        return isPrimitive() && typeNameIs(name);
    }

    public boolean isPrimitive() {
        return getFieldType().isPrimitive();
    }

    public boolean isFractional() {
        return isNumber() && (typeIs(Double.class) || typeIs(BigDecimal.class) || typeIs(Float.class));
    }

    public boolean typeIsAssignableFrom(Class<?> cl) {
        return getFieldType().isAssignableFrom(cl);
    }

    public boolean isAssignableFromType(Class<?> cl) {
        return cl.isAssignableFrom(getFieldType());
    }

    public boolean typeNameIs(String name) {
        return getFieldType().getName().equals(name);
    }

    public boolean typeIs(Class<?> fieldType) {
        return fieldType.equals(getFieldType());
    }

    @Override
    public String toString() {
        String typeName = fieldType.getName();
        String className = recordClassName.substring(recordClassName.lastIndexOf(".") + 1);
        return className + "->" + this.name + "[" + typeName.substring(typeName.lastIndexOf('.') + 1) + "] ";
    }

    public <T> T applyValue(T instance, Object value) {
        if (!isReadOnly()) {
            try {
                if (value == null && isPrimitive()) {
                    throw new BeanMetadataException("Cannot apply null to primitive field" + this);
                }

                getSetter().invoke(instance, value);

                return instance;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new BeanMetadataException("Error applying value " + this, e);
            }
        } else {
            throw new BeanMetadataException("Error applying value. Field is read-only " + this);
        }
    }
}
