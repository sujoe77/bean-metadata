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

import java8.util.function.Consumer;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Andrii Frunt
 */
public class BeanMetadata<FM extends FieldMetadata> extends AnnotatedBase implements Annotated, Typed {
    private Class<?> type;

    private Map<String, FM> fieldsMetadataMap = new HashMap<>();

    private Map<Class<? extends Annotation>, Annotation> annotationsMap = new HashMap<>();

    @Override
    public Class<?> getType() {
        return type;
    }

    public BeanMetadata setType(Class<?> type) {
        this.type = type;
        return this;
    }

    @Override
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

    public List<FM> getFieldsMetadata() {
        return new ArrayList<>(fieldsMetadataMap.values());
    }

    public BeanMetadata setFieldsMetadata(List<FM> fieldsMetadata) {
        fieldsMetadataMap = new HashMap<>();
        StreamSupport.stream(fieldsMetadata).forEach(new Consumer<FM>() {
            @Override
            public void accept(FM fm) {
                fieldsMetadataMap.put(fm.getName(), fm);
            }
        });
        return this;
    }

    public FM getFieldMetadata(String fieldName) {
        return fieldsMetadataMap.get(fieldName);
    }

    public FM getOrCreateFieldMetadataByName(String name, FM fm) {
        fm.setName(name);
        if (!fieldsMetadataMap.containsKey(name)) {
            fieldsMetadataMap.put(name, fm);
        }
        return fieldsMetadataMap.get(name);
    }

    public Set<String> getFieldNames() {
        return new HashSet<>(fieldsMetadataMap.keySet());
    }

    public List<FM> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation) {
        return StreamSupport.stream(getFieldsMetadata()).filter(new Predicate<FM>() {
            @Override
            public boolean test(FM fm) {
                return fm.isAnnotatedWith(annotation);
            }
        }).collect(Collectors.<FM>toList());
    }

    public boolean hasField(String fieldName) {
        return getFieldMetadata(fieldName) != null;
    }

    public void addFieldMetadata(FM fm) {
        fieldsMetadataMap.put(fm.getName(), fm);
    }

    public void addFieldsMetadata(List<FM> fms) {
        StreamSupport.stream(fms).forEach(new Consumer<FM>() {
            @Override
            public void accept(FM fm) {
                addFieldMetadata(fm);
            }
        });
    }

    public FM removeFieldMetadata(String fieldName) {
        FM fm = getFieldMetadata(fieldName);
        if (fm != null) {
            List<FM> fms = new ArrayList<>(getFieldsMetadata());
            fms.remove(fm);
            setFieldsMetadata(fms);
        }
        return fm;
    }

    public Object getFieldValue(Object instance, FM fm) {
        if (fm == null) {
            throw new BeanMetadataException("Field not found");
        }

        return fm.getValue(instance);
    }

    public Object getFieldValue(Object instance, String fieldName) {
        FM fm = getFieldMetadata(fieldName);

        if (fm == null) {
            throw new BeanMetadataException("Field not found " + fieldName);
        }

        return getFieldValue(instance, fm);
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

    @Override
    public Map<Class<? extends Annotation>, Annotation> getAnnotationsMap() {
        return annotationsMap;
    }

    @Override
    public BeanMetadata<FM> setAnnotationsMap(Map<Class<? extends Annotation>, Annotation> annotationsMap) {
        this.annotationsMap = annotationsMap;
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
