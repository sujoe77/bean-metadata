package com.afrunt.beanmetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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

    public String getName() {
        return name;
    }

    public FieldMetadata setName(String name) {
        this.name = name;
        return this;
    }

    public boolean typeIs(Class<?> fieldType) {
        return fieldType.equals(getFieldType());
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


}
