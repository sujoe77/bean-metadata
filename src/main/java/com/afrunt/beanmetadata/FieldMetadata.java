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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrii Frunt
 */
public class FieldMetadata implements Annotated, Typed {
    private String name;
    private Class<?> type;
    private Method getter;
    private Method setter;
    private String recordClassName;
    private Map<Class<? extends Annotation>, Annotation> annotationsMap = new HashMap<>();

    @Override
    public Map<Class<? extends Annotation>, Annotation> getAnnotationsMap() {
        return annotationsMap;
    }

    @Override
    public FieldMetadata setAnnotationsMap(Map<Class<? extends Annotation>, Annotation> annotationsMap) {
        this.annotationsMap = annotationsMap;
        return this;
    }

    public String getName() {
        return name;
    }

    public FieldMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public Class<?> getType() {
        return type;
    }

    public FieldMetadata setType(Class<?> type) {
        this.type = type;
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

    public String getBeanClassName() {
        return recordClassName;
    }

    public FieldMetadata setBeanClassName(String recordClassName) {
        this.recordClassName = recordClassName;
        return this;
    }

    public <T> T applyValue(T instance, Object value) {
        if (!isReadOnly()) {
            try {
                if (value == null && isPrimitive()) {
                    throw new BeanMetadataException("Cannot apply null to primitive field" + this);
                }

                if (value != null && !typeIsAssignableFrom(value.getClass())) {
                    throw new BeanMetadataException("Cannot apply field value. Types are incompatible. " + this);
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

    @Override
    public String toString() {
        String typeName = type.getName();
        String className = recordClassName.substring(recordClassName.lastIndexOf(".") + 1);
        return className + "->" + this.name + "[" + typeName.substring(typeName.lastIndexOf('.') + 1) + "] ";
    }
}
