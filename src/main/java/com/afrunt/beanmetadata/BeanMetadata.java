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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Andrii Frunt
 */
public class BeanMetadata<FM extends FieldMetadata> implements Annotated, Typed {
    private Class<?> type;

    private Map<String, FM> fieldsMetadataMap = new HashMap<>();

    private Set<Annotation> annotations = new HashSet<>();

    public Class<?> getType() {
        return type;
    }

    public BeanMetadata setType(Class<?> type) {
        this.type = type;
        return this;
    }

    public boolean typeIsAssignableFrom(Class<?> type) {
        return getType().isAssignableFrom(type);
    }

    public Object createInstance() {
        return createInstance(getType());
    }

    public <T> T createInstance(Class<T> type) {
        if (!typeIs(type)) {
            throw new BeanMetadataException("Wrong bean type" + type);
        }

        if (isAbstract()) {
            throw new BeanMetadataException("Cannot create instance of abstract class " + getType());
        }

        try {
            return (T) getType().newInstance();
        } catch (Exception e) {
            throw new BeanMetadataException();
        }
    }

    public Object beanFromMap(Map<String, Object> fieldsValues) {
        return beanFromMap(getType(), fieldsValues);
    }

    public <T> T beanFromMap(Class<T> type, Map<String, Object> fieldsValues) {
        if (!typeIs(type)) {
            throw new BeanMetadataException("Wrong bean type" + type);
        }

        T instance = createInstance(type);
        for (String fieldName : fieldsValues.keySet()) {
            FM fm = getFieldMetadata(fieldName);
            Object value = fieldsValues.get(fieldName);
            if (fm != null && !fm.isReadOnly()) {

                if (value == null && fm.isPrimitive()) {
                    continue;
                }

                if (value != null && !fm.typeIsAssignableFrom(value.getClass())) {
                    continue;
                }

                fm.applyValue(instance, value);
            }
        }

        return instance;
    }

    public Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            throw new BeanMetadataException("Bean cannot be null");
        }

        if (typeIs(bean.getClass()) || typeIsAssignableFrom(bean.getClass())) {
            Map<String, Object> map = new HashMap<>();

            for (FM fm : getFieldsMetadata()) {
                Method targetGetter = getTargetGetter(bean, fm);
                map.put(fm.getName(), invokeMethod(bean, targetGetter));
            }

            return map;
        } else {
            throw new BeanMetadataException("Incompatible bean type " + bean.getClass());
        }
    }

    public Set<FM> getFieldsMetadata() {
        return new HashSet<>(fieldsMetadataMap.values());
    }

    public BeanMetadata setFieldsMetadata(Set<FM> fieldsMetadata) {
        fieldsMetadataMap = new HashMap<>();
        fieldsMetadata.forEach(fm -> fieldsMetadataMap.put(fm.getName(), fm));
        return this;
    }

    public FM getFieldMetadata(String fieldName) {
        return fieldsMetadataMap.get(fieldName);
    }

    public FM getOrCreateFieldMetadataByName(String name, FM fm) {
        fm.setName(name);
        fieldsMetadataMap.putIfAbsent(name, fm);
        return fieldsMetadataMap.get(name);
    }

    public Set<String> getFieldNames() {
        return new HashSet<>(fieldsMetadataMap.keySet());
    }

    public Set<FM> getFieldsAnnotatedWith(Class<? extends Annotation> annotation) {
        return getFieldsMetadata().stream()
                .filter(fm -> fm.isAnnotatedWith(annotation))
                .collect(Collectors.toSet());
    }

    public boolean hasField(String fieldName) {
        return getFieldMetadata(fieldName) != null;
    }

    public void addFieldMetadata(FM fm) {
        fieldsMetadataMap.put(fm.getName(), fm);
    }

    public void addFieldsMetadata(Set<FM> fms) {
        fms.forEach(this::addFieldMetadata);
    }

    public FM removeFieldMetadata(String fieldName) {
        FM fm = getFieldMetadata(fieldName);
        if (fm != null) {
            HashSet<FM> fms = new HashSet<>(getFieldsMetadata());
            fms.remove(fm);
            setFieldsMetadata(fms);
        }
        return fm;
    }

    public <T> T applyFieldValue(T instance, String fieldName, Object value) {
        return applyFieldValue(instance, getFieldMetadata(fieldName), value);
    }

    public <T> T applyFieldValue(T instance, FM fm, Object value) {
        if (instance != null && fm != null) {
            return fm.applyValue(instance, value);
        } else {
            throw new BeanMetadataException("Instance and field metadata are required to apply the field value " + this);
        }
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public BeanMetadata setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    protected Method getTargetGetter(Object target, FM fm) {
        try {
            Method originalGetter = fm.getGetter();
            Method getter = target.getClass().getMethod(originalGetter.getName());
            if (fm.typeIsAssignableFrom(getter.getReturnType())) {
                return getter;
            } else {
                throw new BeanMetadataException("Compatible getter not found for target " + target);
            }
        } catch (NoSuchMethodException e) {
            throw new BeanMetadataException("Target getter not found for " + fm);
        }
    }

    protected Object invokeMethod(Object instance, Method method, Object... params) {
        try {
            return method.invoke(instance, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanMetadataException("Error during method invocation " + method, e);
        }
    }

    @Override
    public String toString() {
        return "BeanMetadata[" + getSimpleTypeName() + "]";
    }
}
