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
import java8.util.Optional;
import java8.util.function.Consumer;
import java8.util.function.Function;
import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * @author Andrii Frunt
 */
public abstract class MetadataCollector<M extends Metadata<BM, FM>, BM extends BeanMetadata<FM>, FM extends FieldMetadata> {

    public M collectMetadata(Collection<Class<?>> classes) {
        M metadata = newMetadata();
        Set<BM> beansMetadata = StreamSupport.stream(classes)
                .filter(new Predicate<Class<?>>() {
                    @Override
                    public boolean test(Class<?> aClass) {
                        return !skipClass(aClass);
                    }
                })
                .map(new Function<Class<?>, BM>() {
                    @Override
                    public BM apply(Class<?> aClass) {
                        return collectBeanMetadata(aClass);
                    }
                })
                .filter(new Predicate<BM>() {
                    @Override
                    public boolean test(BM bm) {
                        return bm != null;
                    }
                })
                .filter(new Predicate<BM>() {
                    @Override
                    public boolean test(BM bm) {
                        return !skipBeanMetadata(bm);
                    }
                }).collect(java8.util.stream.Collectors.<BM>toSet());

        StreamSupport.stream(beansMetadata).forEach(new Consumer<BM>() {
            @Override
            public void accept(BM bm) {
                validateBeanMetadata(bm);
            }
        });

        StreamSupport.stream(beansMetadata)
                .map(new Function<BM, List<FM>>() {
                    @Override
                    public List<FM> apply(BM bm) {
                        return bm.getFieldsMetadata();
                    }
                })
                .flatMap(new Function<List<FM>, Stream<FM>>() {
                    @Override
                    public Stream<FM> apply(List<FM> fms) {
                        return StreamSupport.stream(fms);
                    }
                })
                .forEach(new Consumer<FM>() {
                    @Override
                    public void accept(FM bm) {
                        validateFieldMetadata(bm);
                    }
                });

        metadata.setBeansMetadata(
                beansMetadata
        );

        return metadata;
    }

    public BM collectBeanMetadata(Class<?> cl) {
        if (skipClass(cl)) {
            return null;
        }

        BM tm = newBeanMetadata();
        tm.setType(cl);

        List<Class<?>> hierarchy = classHierarchy(cl);
        BM beanMetadata = collectBeanMetadataFromClassHierarchy(hierarchy, tm);

        if (skipBeanMetadata(beanMetadata)) {
            return null;
        } else {
            return beanMetadata;
        }
    }

    protected abstract M newMetadata();

    protected abstract BM newBeanMetadata();

    protected abstract FM newFieldMetadata();

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
        fieldMetadata.setBeanClassName(cl.getName());
        Annotation[] declaredAnnotations = getter.getDeclaredAnnotations();
        fieldMetadata = handleAnnotations(fieldMetadata, declaredAnnotations);
        fieldMetadata.setSetter(Optional.ofNullable(findSetterForGetter(cl, getter)).orElse(fieldMetadata.getSetter()));
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
    private  Collection<Class<? extends Annotation>> annotationsToRemove(Collection<? extends Annotation> declaredAnnotations, final Annotated annotated) {
        RemoveInheritedAnnotations removeInherited =
                (RemoveInheritedAnnotations) StreamSupport.stream(declaredAnnotations)
                        .filter(new Predicate<Annotation>() {
                            @Override
                            public boolean test(Annotation annotation) {
                                return annotation.annotationType().equals(RemoveInheritedAnnotations.class);
                            }
                        })
                        .findFirst().orElse(null);

        if (removeInherited != null) {
            final boolean removeAllAnnotations = removeInherited.removeOnly().length == 0;
            final Collection<Class<? extends Annotation>> removeOnlyClasses = removeAllAnnotations ?
                    annotated.getAnnotationTypes()
                    : Arrays.asList(removeInherited.removeOnly());

            return StreamSupport.stream(annotated.getAnnotationTypes())
                    .filter(new Predicate<Class<? extends Annotation>>() {
                        @Override
                        public boolean test(Class<? extends Annotation> aClass) {
                            return removeOnlyClasses.contains(aClass);
                        }
                    }).collect(Collectors.<Class<? extends Annotation>>toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Set<Method> collectFieldsGetters(Class<?> cl) {
        return StreamSupport.stream(Arrays.asList(cl.getDeclaredMethods()))
                .filter(new Predicate<Method>() {
                    @Override
                    public boolean test(Method method) {
                        return isValidGetter(method);
                    }
                }).collect(Collectors.<Method>toSet());
    }

    protected List<FM> collectFieldsMetadata(Class<?> cl, Set<Method> getters, BM beanMetadata) {
        List<FM> result = new ArrayList<>();
        for (Method getter : getters) {
            String fieldName = fieldNameFromGetter(getter);
            FM fm = beanMetadata.getOrCreateFieldMetadataByName(fieldName, newFieldMetadata());
            fm.setType(getter.getReturnType());
            fm.setGetter(getter);

            fm = collectFieldMetadata(cl, getter, fm);
            if (!skipFieldMetadata(fm)) {
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
        return StreamSupport.stream(annotations).filter(new Predicate<Annotation>() {
            @Override
            public boolean test(Annotation annotation) {
                return !isSkippedAnnotation(annotation);
            }
        }).collect(Collectors.<Annotation>toList());
    }

    protected boolean isSkippedAnnotation(Annotation annotation) {
        return annotation.annotationType().equals(RemoveInheritedAnnotations.class);
    }

    protected boolean isValidGetter(Method m) {
        String name = m.getName();
        Class<?> returnType = m.getReturnType();

        boolean plainGetterName = name.startsWith("get") && !"".equals(name.replaceFirst("get", ""));

        boolean booleanGetterName = name.startsWith("is") && !"".equals(name.replaceFirst("is", ""))
                && isBooleanReturnType(returnType);

        boolean nameIsValid = plainGetterName || booleanGetterName;

        return nameIsValid
                && isPublic(m)
                && !returnType.equals(Void.class)
                && m.getParameterTypes().length == 0;
    }

    private boolean isBooleanReturnType(Class<?> returnType) {
        String returnTypeName = returnType.getName();
        return Boolean.class.equals(returnType) || "boolean".equals(returnTypeName);
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

    protected Method findSetterForGetter(Class<?> cl, Method getter) {
        try {
            Method setter = cl.getMethod(setterNameFromGetter(getter), getter.getReturnType());
            boolean validSetter = setter != null && setter.getParameterTypes().length == 1 && isPublic(setter);
            return validSetter ? setter : null;
        } catch (NoSuchMethodException e) {
            //Field without setter
            return null;
        }
    }

    protected void validateBeanMetadata(BM beanMetadata) {

    }

    protected void validateFieldMetadata(FM fieldMetadata) {

    }

    protected boolean skipClass(Class<?> cl) {
        return false;
    }

    protected boolean skipBeanMetadata(BM beanMetadata) {
        return false;
    }

    protected boolean skipFieldMetadata(FM fieldMetadata) {
        return false;
    }

    private boolean isPublic(Method m) {
        return Modifier.isPublic(m.getModifiers());
    }

    private Collection<Class<? extends Annotation>> annotationsToRemove(Annotated annotated, Annotation[] declaredAnnotations) {
        return annotationsToRemove(Arrays.asList(declaredAnnotations), annotated);
    }

    private String setterNameFromGetter(Method getter) {
        return "set" + capitalize(fieldNameFromGetter(getter));
    }

    private String fieldNameFromGetter(Method getter) {
        String methodName = getter.getName();
        if (methodName.startsWith("get")) {
            return uncapitalize(methodName.substring(3));
        } else {
            return uncapitalize(methodName.substring(2));
        }
    }

    private String capitalize(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            char firstChar = str.charAt(0);
            return Character.isTitleCase(firstChar) ? str : (new StringBuilder(strLen)).append(Character.toTitleCase(firstChar)).append(str.substring(1)).toString();
        } else {
            return str;
        }
    }

    private String uncapitalize(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            char firstChar = str.charAt(0);
            return Character.isLowerCase(firstChar) ? str : (new StringBuilder(strLen)).append(Character.toLowerCase(firstChar)).append(str.substring(1)).toString();
        } else {
            return str;
        }
    }
}
