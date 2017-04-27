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
package com.afrunt.beanmetadata.test.ach.metadata;

import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.test.ach.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.afrunt.beanmetadata.test.ach.annotation.InclusionRequirement.*;

/**
 * @author Andrii Frunt
 */
public class ACHFieldMetadata extends FieldMetadata {

    public boolean isACHField() {
        return achAnnotation() != null;
    }

    private ACHField achAnnotation() {
        return getAnnotation(ACHField.class);
    }

    public boolean isMandatory() {
        return inclusionIs(MANDATORY);
    }

    public boolean isOptional() {
        return inclusionIs(OPTIONAL);
    }

    public boolean isBlank() {
        return inclusionIs(BLANK);
    }

    public boolean isRequired() {
        return inclusionIs(REQUIRED);
    }

    public boolean inclusionIs(InclusionRequirement requirement) {
        if (isAnnotatedWith(Inclusion.class)) {
            return getAnnotation(Inclusion.class).value().equals(requirement);
        } else {
            return achAnnotation().inclusion().equals(requirement);
        }
    }

    public List<String> getValues() {
        if (isAnnotatedWith(Values.class)) {
            return Arrays.asList(getAnnotation(Values.class).value());
        } else {
            return Arrays.asList(achAnnotation().values());
        }
    }

    public String getDateFormat() {
        return getOptionalAnnotation(DateFormat.class)
                .map(DateFormat::value)
                .orElse(null);
    }

    public boolean isTypeTag() {
        return achAnnotation().typeTag();
    }

    public int getStart() {
        return achAnnotation().start();
    }

    public int getLength() {
        return achAnnotation().length();
    }

}
