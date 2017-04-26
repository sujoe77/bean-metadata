package com.afrunt.beanmetadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrii Frunt
 */
public class BeanMetadata<FM extends FieldMetadata> implements Annotated {
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

    public Object createInstance() {
        try {
            return getType()
                    .newInstance();
        } catch (Exception e) {
            throw new BeanMetadataException();
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

    public FM getFieldMetadataByName(String name) {
        return fieldsMetadataMap.get(name);
    }

    public FM getOrCreateFieldMetadataByName(String name, FM fm) {
        fm.setName(name);
        fieldsMetadataMap.putIfAbsent(name, fm);
        return fieldsMetadataMap.get(name);
    }

    public Set<String> getFieldNames() {
        return new HashSet<>(fieldsMetadataMap.keySet());
    }

    public void addFieldMetadata(FM fm) {
        fieldsMetadataMap.put(fm.getName(), fm);
    }

    public void addFieldsMetadata(Set<FM> fms) {
        fms.forEach(this::addFieldMetadata);
    }

    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public BeanMetadata setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }
}
