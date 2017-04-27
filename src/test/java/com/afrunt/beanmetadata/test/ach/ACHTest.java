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
package com.afrunt.beanmetadata.test.ach;

import com.afrunt.beanmetadata.test.ach.annotation.ACHRecordType;
import com.afrunt.beanmetadata.test.ach.domain.FileHeader;
import com.afrunt.beanmetadata.test.ach.logic.ACHMetadataCollector;
import com.afrunt.beanmetadata.test.ach.metadata.ACHBeanMetadata;
import com.afrunt.beanmetadata.test.ach.metadata.ACHFieldMetadata;
import com.afrunt.beanmetadata.test.ach.metadata.ACHMetadata;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Andrii Frunt
 */
public class ACHTest {
    @Test
    public void testACHMetadataCollection() {
        ACHMetadataCollector metadataCollector = new ACHMetadataCollector();
        ACHMetadata metadata = metadataCollector.collectMetadata(Collections.singleton(FileHeader.class));

        assertNotNull(metadata);

        ACHBeanMetadata headerMetadata = metadata.getBeanMetadata(FileHeader.class);

        assertTrue(headerMetadata.isAnnotatedWith(ACHRecordType.class));
        assertEquals(1, headerMetadata.getAnnotations().size());

        Set<ACHFieldMetadata> allFieldsMetadata = headerMetadata.getFieldsMetadata();

        assertEquals(15, allFieldsMetadata.size());

        Set<ACHFieldMetadata> achFieldsMetadata = headerMetadata.getACHFieldsMetadata();
        assertEquals(13, achFieldsMetadata.size());

        ACHFieldMetadata fileCreationDate = headerMetadata.getFieldMetadata("fileCreationDate");
        assertNotNull(fileCreationDate);
        assertEquals("yyMMdd", fileCreationDate.getDateFormat());
        assertTrue(fileCreationDate.isDate());

        ACHFieldMetadata recordTypeMetadata = headerMetadata.getFieldMetadata("recordTypeCode");
        assertNotNull(recordTypeMetadata);
        assertEquals(Collections.singletonList("1"), recordTypeMetadata.getValues());
        assertTrue(recordTypeMetadata.isMandatory());
        assertTrue(recordTypeMetadata.isTypeTag());
        assertTrue(recordTypeMetadata.isString());
        assertEquals(0, recordTypeMetadata.getStart());
        assertEquals(1, recordTypeMetadata.getLength());

    }
}
