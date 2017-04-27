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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Andrii Frunt
 */
public class Metadata<BM extends BeanMetadata<FM>, FM extends FieldMetadata> {
    private Set<BM> beansMetadata = new HashSet<>();

    public Set<BM> getBeansMetadata() {
        return beansMetadata;
    }

    public Metadata<BM, FM> setBeansMetadata(Set<BM> beansMetadata) {
        this.beansMetadata = beansMetadata;
        return this;
    }

    public Set<BM> getAnnotatedWith(Class<? extends Annotation> annotationType) {
        return getBeansMetadata().stream()
                .filter(b -> b.isAnnotatedWith(annotationType))
                .collect(Collectors.toSet());
    }

    public BM getBeanMetadata(Class<?> beanClass) {
        return getBeansMetadata().stream()
                .filter(bm -> bm.typeIs(beanClass))
                .findFirst()
                .orElse(null);
    }

}
