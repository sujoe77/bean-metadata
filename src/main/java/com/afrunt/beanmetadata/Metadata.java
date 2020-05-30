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

import java8.util.function.Predicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

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

    public Set<BM> getAnnotatedWith(final Class<? extends Annotation> annotationType) {
        return StreamSupport.stream(getBeansMetadata()).filter(new Predicate<BM>() {
            @Override
            public boolean test(BM bm) {
                return bm.isAnnotatedWith(annotationType);
            }
        }).collect(Collectors.<BM>toSet());
    }

    public BM getBeanMetadata(final Class<?> beanClass) {
        return StreamSupport.stream(getBeansMetadata()).filter(new Predicate<BM>() {
            @Override
            public boolean test(BM bm) {
                return bm.typeIs(beanClass);
            }
        }).findFirst().orElse(null);
    }
}
