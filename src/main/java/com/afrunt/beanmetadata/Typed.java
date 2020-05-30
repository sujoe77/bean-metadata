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

/**
 * @author Andrii Frunt
 */
public interface Typed {
    Class<?> getType();

    boolean typeIs(Class<?> type);

    boolean typeIsAssignableFrom(Class<?> cl);

    boolean typeNameIs(String name);

    boolean isCompatiblePrimitives(Class<?> fieldType, Class<?> cl);

    String getTypeName();

    String getSimpleTypeName();

    boolean isString();

    boolean isNumber();

    boolean isFractional();

    boolean isShort();

    boolean isInteger();

    boolean isDouble();

    boolean isLong();

    boolean isFloat();

    boolean isByte();

    boolean isBoolean();

    boolean isBigDecimal();

    boolean isBigInteger();

    boolean isDate();

    boolean isPrimitiveWithName(String name);

    boolean isPrimitive();

    int getTypeModifiers();

    boolean isAbstract();

    boolean isEnum();
}
