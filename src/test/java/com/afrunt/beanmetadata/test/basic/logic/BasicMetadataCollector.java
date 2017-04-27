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
package com.afrunt.beanmetadata.test.basic.logic;

import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.Metadata;
import com.afrunt.beanmetadata.MetadataCollector;

import java.lang.annotation.Annotation;

/**
 * @author Andrii Frunt
 */
public class BasicMetadataCollector extends MetadataCollector<Metadata<BeanMetadata<FieldMetadata>, FieldMetadata>, BeanMetadata<FieldMetadata>, FieldMetadata> {

    @Override
    protected Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> newMetadata() {
        return new Metadata<>();
    }

    @Override
    protected BeanMetadata<FieldMetadata> newBeanMetadata() {
        return new BeanMetadata<>();
    }

    @Override
    protected FieldMetadata newFieldMetadata() {
        return new FieldMetadata();
    }

    @Override
    protected BeanMetadata<FieldMetadata> onAddBeanAnnotation(BeanMetadata<FieldMetadata> beanMetadata, Annotation annotation) {
        beanMetadata = super.onAddBeanAnnotation(beanMetadata, annotation);
        System.out.println("Annotation " + annotation + " added to bean metadata " + beanMetadata);
        return beanMetadata;
    }

    @Override
    protected BeanMetadata<FieldMetadata> onRemoveBeanAnnotation(BeanMetadata<FieldMetadata> beanMetadata, Annotation annotation) {
        beanMetadata = super.onRemoveBeanAnnotation(beanMetadata, annotation);
        System.out.println("Annotation " + annotation + " removed from bean metadata " + beanMetadata);
        return beanMetadata;
    }

    @Override
    protected FieldMetadata onAddFieldAnnotation(FieldMetadata fieldMetadata, Annotation annotation) {
        fieldMetadata = super.onAddFieldAnnotation(fieldMetadata, annotation);
        System.out.println("Annotation " + annotation + " added to field metadata " + fieldMetadata);

        return fieldMetadata;
    }

    @Override
    protected FieldMetadata onRemoveFieldAnnotation(FieldMetadata fieldMetadata, Annotation annotation) {
        fieldMetadata = super.onRemoveFieldAnnotation(fieldMetadata, annotation);
        System.out.println("Annotation " + annotation + " removed from field metadata " + fieldMetadata);
        return fieldMetadata;
    }
}
