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
package com.afrunt.beanmetadata.test.ach.logic;

import com.afrunt.beanmetadata.MetadataCollector;
import com.afrunt.beanmetadata.test.ach.exception.ACHException;
import com.afrunt.beanmetadata.test.ach.metadata.ACHBeanMetadata;
import com.afrunt.beanmetadata.test.ach.metadata.ACHFieldMetadata;
import com.afrunt.beanmetadata.test.ach.metadata.ACHMetadata;

import java.text.SimpleDateFormat;

/**
 * @author Andrii Frunt
 */
public class ACHMetadataCollector extends MetadataCollector<ACHMetadata, ACHBeanMetadata, ACHFieldMetadata> {
    @Override
    protected ACHMetadata newMetadata() {
        return new ACHMetadata();
    }

    @Override
    protected ACHBeanMetadata newBeanMetadata() {
        return new ACHBeanMetadata();
    }

    @Override
    protected ACHFieldMetadata newFieldMetadata() {
        return new ACHFieldMetadata();
    }

    @Override
    protected void validateFieldMetadata(ACHFieldMetadata fm) {
        super.validateFieldMetadata(fm);
        boolean achField = fm.isACHField();

        if (achField) {
            validateDateField(fm);
        }
    }

    private void validateDateField(ACHFieldMetadata fm) {
        if (fm.isDate()) {
            String dateFormat = fm.getDateFormat();
            if (dateFormat == null) {
                throw new ACHException("Date format is required for date fields");
            }

            try {
                new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
                throw new ACHException(dateFormat + " is wrong date format for field " + fm);
            }

            if (dateFormat.length() != fm.getLength()) {
                throw new ACHException("The length of date pattern should be equal to field length");
            }
        }
    }
}
