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
package com.afrunt.beanmetadata.test.ach.domain;


import com.afrunt.beanmetadata.test.ach.annotation.ACHField;
import com.afrunt.beanmetadata.test.basic.annotation.TypeAnnotation;

import java.util.stream.IntStream;

import static com.afrunt.beanmetadata.test.ach.annotation.InclusionRequirement.MANDATORY;

/**
 * @author Andrii Frunt
 */
@TypeAnnotation("achRecord")
public abstract class ACHRecord {
    public static final String RECORD_TYPE_CODE = "ACH Record Type Code";
    private String record;

    @ACHField(length = 1, inclusion = MANDATORY, name = RECORD_TYPE_CODE, typeTag = true)
    public abstract String getRecordTypeCode();

    public String getRecord() {
        return record;
    }

    public ACHRecord setRecord(String record) {
        this.record = record;
        return this;
    }

    public String reserved(int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i< length; i++){
            sb.append(" ");
        }
        return sb.toString();
    }
}
