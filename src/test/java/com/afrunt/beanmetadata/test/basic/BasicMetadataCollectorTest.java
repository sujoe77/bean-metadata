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
package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.BasicMetadataCollector;
import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.Metadata;
import com.afrunt.beanmetadata.test.basic.annotation.AnotherFieldAnnotation;
import com.afrunt.beanmetadata.test.basic.annotation.FieldAnnotation;
import com.afrunt.beanmetadata.test.basic.annotation.TypeAnnotation;
import com.afrunt.beanmetadata.test.basic.domain.*;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Andrii Frunt
 */
public class BasicMetadataCollectorTest extends BasicTest {
    @Test
    public void testMetadataCollection() {
        Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = getMetadata();

        Set<BeanMetadata<FieldMetadata>> beansMetadata = metadata.getBeansMetadata();

        assertEquals(2, beansMetadata.size());
        assertEquals(2, metadata.getAnnotatedWith(TypeAnnotation.class).size());

        testBeanMetadata(metadata.getBeanMetadata(Bean.class));

        BeanMetadata<FieldMetadata> sbm = metadata.getBeanMetadata(SecondBean.class);
        assertNotNull(sbm);

    }

    @Test
    public void testBeanMetadataCollection() {
        BasicMetadataCollector metadataCollector = new BasicMetadataCollector();
        BeanMetadata<FieldMetadata> beanMetadata = metadataCollector.collectBeanMetadata(Bean.class);

        assertTrue(beanMetadata.isAnnotatedWith(TypeAnnotation.class));

        assertEquals(3, beanMetadata.getFieldsMetadata().size());

        assertEquals("bean", beanMetadata.getAnnotation(TypeAnnotation.class).value());

        assertTrue(beanMetadata.hasField("id"));
        assertTrue(beanMetadata.hasField("value"));

        FieldMetadata id = beanMetadata.getFieldMetadata("id");

        assertTrue(id.isAnnotatedWithAll(FieldAnnotation.class, AnotherFieldAnnotation.class));
        assertTrue(id.isString());
    }

    @Test
    public void testBeanCreationFromMap() {
        BasicMetadataCollector metadataCollector = new BasicMetadataCollector();
        BeanMetadata<FieldMetadata> beanMetadata = metadataCollector.collectBeanMetadata(Bean.class);

        Map<String, Object> map = new HashMap<>();

        map.put("id", "id");
        map.put("value", "value");
        map.put("primitiveIntField", null);

        Bean bean = beanMetadata.beanFromMap(Bean.class, map);

        assertEquals("id", bean.getId());
        assertEquals("value", bean.getValue());
        assertEquals(0, bean.getPrimitiveIntField());

        map.put("primitiveIntField", 1);
        bean = beanMetadata.beanFromMap(Bean.class, map);
        assertEquals(1, bean.getPrimitiveIntField());


        Map<String, Object> mapFromBean = beanMetadata.beanToMap(bean);
        assertEquals(3, mapFromBean.size());

        assertEquals(map, mapFromBean);

        ChildBean cb = new ChildBean();

        cb.setId("id");
        cb.setValue("value");
        cb.setPrimitiveIntField(1);

        Map<String, Object> childBeanMap = beanMetadata.beanToMap(cb);
        assertEquals(3, childBeanMap.size());
        assertEquals(777, childBeanMap.get("primitiveIntField"));
    }

    private void testBeanMetadata(BeanMetadata<FieldMetadata> bm) {
        assertNotNull(bm);
        TypeAnnotation typeAnnotation = bm.getAnnotation(TypeAnnotation.class);
        assertNotNull(typeAnnotation);
        assertEquals("bean", typeAnnotation.value());
        assertEquals(1, bm.getAnnotations().size());

        assertEquals(3, bm.getFieldsMetadata().size());
        assertNotNull(bm.createInstance());

        testFieldMetadata(bm, "id", String.class, new Class[]{FieldAnnotation.class, AnotherFieldAnnotation.class});
        testFieldMetadata(bm, "value", String.class, new Class[]{FieldAnnotation.class});
        List<FieldMetadata> emptyFields = new ArrayList<>();
        List<FieldMetadata> fieldsMetadata = bm.getFieldsMetadata();

        bm.setFieldsMetadata(emptyFields);
        assertEquals(emptyFields, bm.getFieldsMetadata());
        bm.setFieldsMetadata(fieldsMetadata);
    }

    private void testFieldMetadata(BeanMetadata<FieldMetadata> bm, String name, Class<?> fieldType, Class<? extends Annotation>[] annotations) {
        assertTrue(bm.hasField(name));
        FieldMetadata fm = bm.getFieldMetadata(name);
        assertTrue(fm.isAnnotatedWithAll(annotations));
        assertEquals(annotations.length, fm.getAnnotations().size());
        assertEquals(fieldType, fm.getType());
        assertEquals(bm.getType().getName(), fm.getBeanClassName());
        assertNotNull(fm.getGetter());
        assertEquals(fm.getSetter() == null, fm.isReadOnly());
        assertTrue(fm.typeIs(fieldType));
        Set<Annotation> allAnnotations = fm.getAnnotations();

        fm.removeAllAnnotations();

        assertTrue(fm.getAnnotations().isEmpty());

        fm.setAnnotations(allAnnotations);
    }


    @Override
    protected Collection<Class<?>> classes() {
        return Arrays.<Class<?>>asList(Bean.class, SecondBean.class);
    }
}
