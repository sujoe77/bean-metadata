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

import java8.util.Optional;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrii Frunt
 */
public interface Annotated {
    Map<Class<? extends Annotation>, Annotation> getAnnotationsMap();

    Annotated setAnnotationsMap(Map<Class<? extends Annotation>, Annotation> annotationsMap);

    Set<Annotation> getAnnotations();

    Annotated setAnnotations(Set<Annotation> annotations);

    Collection<Class<? extends Annotation>> getAnnotationTypes();

    @SuppressWarnings("unchecked")
    <T extends Annotation> T getAnnotation(Class<T> annotationType);

    <T extends Annotation> Optional<T> getOptionalAnnotation(Class<T> annotationType);

    Annotated addAnnotation(Annotation annotation);

    Annotated addAnnotations(Collection<Annotation> annotations);

    Annotated addAnnotations(Annotation[] annotations);

    <T extends Annotation> T removeAnnotation(Class<T> annotationType);

     Set<? extends Annotation> removeAnnotations(Collection<Class<? extends Annotation>> annotationTypes);

    Set<Annotation> removeAllAnnotations();

    boolean isAnnotatedWith(Class<? extends Annotation> annotationType);

    @SuppressWarnings("unchecked")
    boolean isAnnotatedWithAll(Class<? extends Annotation>... annotations);

    boolean notAnnotatedWith(Class<? extends Annotation> annotation);
}
