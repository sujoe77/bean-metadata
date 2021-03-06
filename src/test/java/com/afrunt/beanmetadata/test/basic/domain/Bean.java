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
package com.afrunt.beanmetadata.test.basic.domain;

import com.afrunt.beanmetadata.test.basic.annotation.FieldAnnotation;
import com.afrunt.beanmetadata.test.basic.annotation.TypeAnnotation;

/**
 * @author Andrii Frunt
 */
@TypeAnnotation("bean")
public class Bean extends BaseBean {
    private String value;
    private int primitiveIntField;

    @Override
    public String getId() {
        return super.getId();
    }

    @FieldAnnotation("value")
    public String getValue() {
        return value;
    }

    public Bean setValue(String value) {
        this.value = value;
        return this;
    }

    public int getPrimitiveIntField() {
        return primitiveIntField;
    }

    public Bean setPrimitiveIntField(int primitiveIntField) {
        this.primitiveIntField = primitiveIntField;
        return this;
    }
}
