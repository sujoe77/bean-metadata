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


import com.afrunt.beanmetadata.annotation.RemoveInheritedAnnotations;
import com.afrunt.beanmetadata.test.ach.annotation.ACHField;
import com.afrunt.beanmetadata.test.ach.annotation.ACHRecordType;
import com.afrunt.beanmetadata.test.ach.annotation.DateFormat;
import com.afrunt.beanmetadata.test.ach.annotation.Values;
import com.afrunt.beanmetadata.test.basic.annotation.TypeAnnotation;

import java.util.Date;

import static com.afrunt.beanmetadata.test.ach.annotation.InclusionRequirement.*;


/**
 * The File Header Record designates physical file characteristics. It also identifies the Bank as the immediate
 * destination and your company as the immediate origin of the file.
 *
 * @author Andrii Frunt
 */
@ACHRecordType
@RemoveInheritedAnnotations(removeOnly = TypeAnnotation.class)
public class FileHeader extends ACHRecord {
    public static final String PRIORITY_CODE = "Priority Code";
    public static final String IMMEDIATE_DESTINATION = "Immediate Destination";
    public static final String IMMEDIATE_ORIGIN = "Immediate Origin";
    public static final String FILE_CREATION_DATE = "File Creation Date";
    public static final String FILE_CREATION_TIME = "File Creation Time";
    public static final String FILE_ID_MODIFIER = "File ID Modifier";
    public static final String RECORD_SIZE = "ACHRecord Size";
    public static final String BLOCKING_FACTOR = "Blocking Factor";
    public static final String FORMAT_CODE = "Format Code";
    public static final String IMMEDIATE_DESTINATION_NAME = "Immediate Destination Name";
    public static final String IMMEDIATE_ORIGIN_NAME = "Immediate Origin Name";
    public static final String REFERENCE_CODE = "Reference Code";

    private String priorityCode;
    private String immediateDestination;
    private String immediateOrigin;
    private Date fileCreationDate;
    private String fileCreationTime;
    private String fileIdModifier;
    private String blockingFactor;
    private String formatCode;
    private String immediateDestinationName;
    private String immediateOriginName;
    private String referenceCode;

    private String nonACHField;
    private boolean booleanField;
    private int intField;

    @Override
    @Values("1")
    public String getRecordTypeCode() {
        return "1";
    }

    /**
     * Priority codes are not used at this time; this field must contain 01
     *
     * @return 01
     */
    @ACHField(start = 1, length = 2, name = PRIORITY_CODE, inclusion = REQUIRED, values = "01")
    public String getPriorityCode() {
        return priorityCode;
    }

    public FileHeader setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
        return this;
    }

    @ACHField(start = 3, length = 10, name = IMMEDIATE_DESTINATION, inclusion = MANDATORY)
    public String getImmediateDestination() {
        return immediateDestination;
    }

    public FileHeader setImmediateDestination(String immediateDestination) {
        this.immediateDestination = immediateDestination;
        return this;
    }

    @ACHField(start = 13, length = 10, name = IMMEDIATE_ORIGIN, inclusion = MANDATORY)
    public String getImmediateOrigin() {
        return immediateOrigin;
    }

    public FileHeader setImmediateOrigin(String immediateOrigin) {
        this.immediateOrigin = immediateOrigin;
        return this;
    }

    /**
     * The date you create or transmit the input file:
     * <p>
     * “YY” = Last two digits of the Year
     * “MM” = Month in two digits
     * “DD” = Day in two digits
     *
     * @return
     */
    @ACHField(start = 23, length = 6, name = FILE_CREATION_DATE, inclusion = MANDATORY)
    @DateFormat("yyMMdd")
    public Date getFileCreationDate() {
        return fileCreationDate;
    }

    public FileHeader setFileCreationDate(Date fileCreationDate) {
        this.fileCreationDate = fileCreationDate;
        return this;
    }

    /**
     * Time of day you create or transmit the input file. This field is used to distinguish among input files if you
     * submit more than one per day:
     * <p>
     * “HH = Hour based on a 24 hr clock
     * “MM” = Minutes in two digits
     *
     * @return
     */
    @ACHField(start = 29, length = 4, name = FILE_CREATION_TIME)
    public String getFileCreationTime() {
        return fileCreationTime;
    }

    public FileHeader setFileCreationTime(String fileCreationTime) {
        this.fileCreationTime = fileCreationTime;
        return this;
    }

    /**
     * Code to distinguish among multiple input files sent per day. Label the first (or only) file “A” (or “0”) and
     * continue in sequence.
     *
     * @return
     */
    @ACHField(start = 33, length = 1, name = FILE_ID_MODIFIER, inclusion = MANDATORY)
    public String getFileIdModifier() {
        return fileIdModifier;
    }

    public FileHeader setFileIdModifier(String fileIdModifier) {
        this.fileIdModifier = fileIdModifier;
        return this;
    }

    /**
     * This field indicates the number of characters contained in each record. The value 094 is used
     *
     * @return the number of characters contained in each record
     */
    @ACHField(start = 34, length = 3, name = RECORD_SIZE, inclusion = MANDATORY, values = "094")
    public String getRecordSize() {
        return "094";
    }

    @ACHField(start = 37, length = 2, name = BLOCKING_FACTOR, inclusion = MANDATORY, values = "10")
    public String getBlockingFactor() {
        return blockingFactor;
    }

    public FileHeader setBlockingFactor(String blockingFactor) {
        this.blockingFactor = blockingFactor;
        return this;
    }

    @ACHField(start = 39, length = 1, values = "1", inclusion = MANDATORY, name = FORMAT_CODE)
    public String getFormatCode() {
        return formatCode;
    }

    public FileHeader setFormatCode(String formatCode) {
        this.formatCode = formatCode;
        return this;
    }

    @ACHField(start = 40, length = 23, name = IMMEDIATE_DESTINATION_NAME, inclusion = OPTIONAL)
    public String getImmediateDestinationName() {
        return immediateDestinationName;
    }

    public FileHeader setImmediateDestinationName(String immediateDestinationName) {
        this.immediateDestinationName = immediateDestinationName;
        return this;
    }

    /**
     * Your company's name, up to 23 characters including spaces.
     *
     * @return
     */
    @ACHField(start = 63, length = 23, name = IMMEDIATE_ORIGIN_NAME, inclusion = OPTIONAL)
    public String getImmediateOriginName() {
        return immediateOriginName;
    }

    public FileHeader setImmediateOriginName(String immediateOriginName) {
        this.immediateOriginName = immediateOriginName;
        return this;
    }

    /**
     * You may use this field to describe the input file for internal accounting purposes or fill with spaces.
     *
     * @return
     */
    @ACHField(start = 86, length = 8, name = REFERENCE_CODE, inclusion = OPTIONAL)
    public String getReferenceCode() {
        return referenceCode;
    }

    public FileHeader setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
        return this;
    }

    public String getNonACHField() {
        return nonACHField;
    }

    public FileHeader setNonACHField(String nonACHField) {
        this.nonACHField = nonACHField;
        return this;
    }

    public boolean isBooleanField() {
        return booleanField;
    }

    public FileHeader setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
        return this;
    }

    public int getIntField() {
        return intField;
    }

    public FileHeader setIntField(int intField) {
        this.intField = intField;
        return this;
    }
}
