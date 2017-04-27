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
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author Andrii Frunt
 */
public interface Annotated {
    Set<Annotation> getAnnotations();

    Annotated setAnnotations(Set<Annotation> annotations);

    default Collection<Class<? extends Annotation>> getAnnotationTypes() {
        if (getAnnotations() != null) {
            return getAnnotations().stream()
                    .map(Annotation::annotationType)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    default <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return ofNullable(getAnnotations()).orElse(Collections.emptySet()).stream()
                .filter(a -> a.annotationType().equals(annotationType))
                .map(a -> (T) a)
                .findFirst()
                .orElse(null);
    }

    default <T extends Annotation> Optional<T> getOptionalAnnotation(Class<T> annotationType) {
        return Optional.ofNullable(getAnnotation(annotationType));
    }

    default Annotated addAnnotation(Annotation annotation) {
        Set<Annotation> annotations = new HashSet<>(getAnnotations());
        annotations.remove(getAnnotation(annotation.annotationType()));
        annotations.add(annotation);
        setAnnotations(annotations);
        return this;
    }

    default Annotated addAnnotations(Collection<Annotation> annotations) {
        annotations.forEach(this::addAnnotation);
        return this;
    }

    default Annotated addAnnotations(Annotation[] annotations) {
        addAnnotations(Arrays.asList(annotations));
        return this;
    }

    default <T extends Annotation> T removeAnnotation(Class<T> annotationType) {
        T annotation = getAnnotation(annotationType);
        HashSet<Annotation> annotations = new HashSet<>(getAnnotations());
        annotations.remove(annotation);
        setAnnotations(annotations);
        return annotation;
    }

    default Set<? extends Annotation> removeAnnotations(Collection<Class<? extends Annotation>> annotationTypes) {
        return annotationTypes.stream()
                .map(this::removeAnnotation)
                .collect(Collectors.toSet());
    }

    default Set<Annotation> removeAllAnnotations() {
        Set<Annotation> annotations = getAnnotations();
        setAnnotations(new HashSet<>());
        return annotations;
    }

    default boolean isAnnotatedWith(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @SuppressWarnings("unchecked")
    default boolean isAnnotatedWithAll(Class<? extends Annotation>... annotations) {
        return Arrays.stream(annotations)
                .map(this::isAnnotatedWith)
                .reduce(true, (left, right) -> left && right);
    }

    default boolean notAnnotatedWith(Class<? extends Annotation> annotation) {
        return !isAnnotatedWith(annotation);
    }
}
