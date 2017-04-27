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

import com.afrunt.beanmetadata.annotation.RemoveInheritedAnnotations;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/**
 * @author Andrii Frunt
 */
public abstract class MetadataCollector<M extends Metadata<BM, FM>, BM extends BeanMetadata<FM>, FM extends FieldMetadata> {
    public M collectMetadata(Collection<Class<?>> classes) {
        M metadata = newMetadata();

        metadata.setBeansMetadata(
                classes.stream().map(this::collectBeanMetadata)
                        .filter(bm -> !skipBeanMetadata(bm))
                        .collect(Collectors.toSet())
        );

        return metadata;
    }

    protected abstract M newMetadata();

    protected abstract BM newBeanMetadata();

    protected abstract FM newFieldMetadata();

    protected BM collectBeanMetadata(Class<?> cl) {
        BM tm = newBeanMetadata();
        tm.setType(cl);

        List<Class<?>> hierarchy = classHierarchy(cl);
        BM beanMetadata = collectBeanMetadataFromClassHierarchy(hierarchy, tm);
        validateBeanMetadata(beanMetadata);

        return beanMetadata;
    }

    protected BM collectBeanMetadataFromClassHierarchy(List<Class<?>> hierarchy, BM typeMetadata) {
        if (!hierarchy.isEmpty()) {
            Class<?> type = hierarchy.get(0);
            List<Class<?>> tailOfHierarchy = hierarchy.subList(1, hierarchy.size());
            return collectBeanMetadataFromClassHierarchy(tailOfHierarchy, collectTypeMetadataFromClass(type, typeMetadata));
        } else {
            return typeMetadata;
        }
    }

    protected BM collectTypeMetadataFromClass(Class<?> cl, BM beanMetadata) {
        beanMetadata = collectBeanMetadata(cl, beanMetadata);

        Set<Method> annotatedGetters = collectFieldsGetters(cl);

        beanMetadata.addFieldsMetadata(collectFieldsMetadata(cl, annotatedGetters, beanMetadata));

        return beanMetadata;
    }

    protected BM collectBeanMetadata(Class<?> cl, BM beanMetadata) {
        Annotation[] declaredAnnotations = cl.getDeclaredAnnotations();
        beanMetadata = handleAnnotations(beanMetadata, declaredAnnotations);
        return beanMetadata;
    }

    protected FM collectFieldMetadata(Class<?> cl, Method getter, FM fieldMetadata) {
        //TODO: everything related to field annotation starts here
        fieldMetadata.setRecordClassName(cl.getName());
        Annotation[] declaredAnnotations = getter.getDeclaredAnnotations();
        fieldMetadata = handleAnnotations(fieldMetadata, declaredAnnotations);
        fieldMetadata.setSetter(ofNullable(findSetterForGetter(cl, getter)).orElse(fieldMetadata.getSetter()));
        return fieldMetadata;
    }

    protected <T extends Annotated> T handleAnnotations(T annotated, Annotation[] declaredAnnotations) {
        annotated = removeAnnotations(annotated, declaredAnnotations);
        annotated = addAnnotations(annotated, declaredAnnotations);
        return annotated;
    }

    protected <T extends Annotated> T addAnnotations(T annotated, Annotation[] declaredAnnotations) {
        Collection<Annotation> filteredDeclaredAnnotations = filterSkippedAnnotations(declaredAnnotations);
        annotated.addAnnotations(filteredDeclaredAnnotations);
        for (Annotation a : filteredDeclaredAnnotations) {
            if (annotated instanceof BeanMetadata) {
                onAddBeanAnnotation((BM) annotated, a);
            } else {
                onAddFieldAnnotation((FM) annotated, a);
            }
        }
        return annotated;
    }

    protected <T extends Annotated> T removeAnnotations(T annotated, Annotation[] declaredAnnotations) {
        Collection<Class<? extends Annotation>> annotationTypes = annotationsToRemove(annotated, declaredAnnotations);
        Set<? extends Annotation> annotations = annotated.removeAnnotations(annotationTypes);
        for (Annotation a : annotations) {
            if (annotated instanceof BeanMetadata) {
                onRemoveBeanAnnotation((BM) annotated, a);
            } else {
                onRemoveFieldAnnotation((FM) annotated, a);
            }
        }
        return annotated;
    }

    protected BM onAddBeanAnnotation(BM beanMetadata, Annotation annotation) {
        return beanMetadata;
    }

    protected BM onRemoveBeanAnnotation(BM beanMetadata, Annotation annotation) {
        return beanMetadata;
    }

    protected FM onAddFieldAnnotation(FM fieldMetadata, Annotation annotation) {
        return fieldMetadata;
    }

    protected FM onRemoveFieldAnnotation(FM fieldMetadata, Annotation annotation) {
        return fieldMetadata;
    }

    @SuppressWarnings("unchecked")
    private Collection<Class<? extends Annotation>> annotationsToRemove(Collection<Annotation> declaredAnnotations, Annotated annotated) {
        RemoveInheritedAnnotations removeInherited = (RemoveInheritedAnnotations) declaredAnnotations.stream().filter(a -> a instanceof RemoveInheritedAnnotations).findFirst().orElse(null);

        if (removeInherited != null) {
            boolean removeAllAnnotations = removeInherited.removeOnly().length == 0;
            Collection<Class<? extends Annotation>> removeOnlyClasses = removeAllAnnotations ?
                    annotated.getAnnotationTypes()
                    : Arrays.asList(removeInherited.removeOnly());

            return annotated.getAnnotationTypes().stream()
                    .filter(removeOnlyClasses::contains)
                    .collect(Collectors.toList());

        } else {
            return Collections.emptyList();
        }
    }

    private Set<Method> collectFieldsGetters(Class<?> cl) {
        return Arrays.stream(cl.getDeclaredMethods())
                .filter(this::isValidGetter)
                .collect(Collectors.toSet());
    }

    protected Set<FM> collectFieldsMetadata(Class<?> cl, Set<Method> getters, BM beanMetadata) {
        Set<FM> result = new HashSet<>();
        for (Method getter : getters) {
            String fieldName = fieldNameFromGetter(getter);
            FM fm = beanMetadata.getOrCreateFieldMetadataByName(fieldName, newFieldMetadata());
            fm.setFieldType(getter.getReturnType());
            fm.setGetter(getter);

            fm = collectFieldMetadata(cl, getter, fm);
            if (!skipFieldMetadata(fm)) {
                validateFieldMetadata(fm);
                result.add(fm);
            } else {
                beanMetadata.removeFieldMetadata(fieldName);
            }
        }
        return result;
    }

    protected Collection<Annotation> filterSkippedAnnotations(Annotation[] annotations) {
        return filterSkippedAnnotations(Arrays.asList(annotations));
    }

    protected Collection<Annotation> filterSkippedAnnotations(Collection<Annotation> annotations) {
        return annotations.stream()
                .filter(a -> !isSkippedAnnotation(a))
                .collect(Collectors.toList());
    }

    protected boolean isSkippedAnnotation(Annotation annotation) {
        return annotation instanceof RemoveInheritedAnnotations;
    }

    protected boolean isValidGetter(Method m) {
        String name = m.getName();
        return name.startsWith("get")
                && !"".equals(name.replaceFirst("get", ""))
                && !m.getReturnType().equals(Void.class)
                && m.getParameterCount() == 0;
    }

    protected List<Class<?>> classHierarchy(Class<?> cl) {
        List<Class<?>> hierarchy = new ArrayList<>();
        while (!Object.class.equals(cl)) {
            hierarchy.add(cl);
            cl = cl.getSuperclass();
        }
        Collections.reverse(hierarchy);
        return hierarchy;
    }

    protected Set<Method> collectFieldGettersForFieldNames(Class<?> cl, Set<String> fieldNames) {
        return fieldNames.stream()
                .map(fieldName -> findGetterForFieldName(cl, fieldName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    protected Method findGetterForFieldName(Class<?> cl, String fieldName) {
        return Arrays.stream(cl.getDeclaredMethods())
                .filter(m -> getterNameFromFieldName(fieldName).equals(m.getName()) && isValidGetter(m))
                .findFirst()
                .orElse(null);
    }

    protected Method findSetterForGetter(Class<?> cl, Method getter) {
        try {
            Method setter = cl.getMethod(setterNameFromGetter(getter), getter.getReturnType());
            return setter.getParameterCount() == 1 ? setter : null;
        } catch (NoSuchMethodException e) {
            //Field without setter
            return null;
        }
    }

    protected void validateBeanMetadata(BM beanMetadata) {

    }

    protected void validateFieldMetadata(FM fieldMetadata) {

    }

    protected boolean skipBeanMetadata(BM beanMetadata) {
        return false;
    }

    protected boolean skipFieldMetadata(FM fieldMetadata) {
        return false;
    }

    private Collection<Class<? extends Annotation>> annotationsToRemove(Annotated annotated, Annotation[] declaredAnnotations) {
        return annotationsToRemove(Arrays.asList(declaredAnnotations), annotated);
    }

    private String setterNameFromGetter(Method getter) {
        return "set" + StringUtils.capitalize(fieldNameFromGetter(getter));
    }

    private String getterNameFromFieldName(String fieldName) {
        return "get" + StringUtils.capitalize(fieldName);
    }

    private String fieldNameFromGetter(Method getter) {
        return uncapitalize(getter.getName().substring(3));
    }
}
