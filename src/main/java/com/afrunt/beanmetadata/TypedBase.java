package com.afrunt.beanmetadata;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public abstract class TypedBase implements Typed {

    @Override
    public abstract Class<?> getType();

    @Override
    public boolean typeIs(Class<?> type) {
        return type.equals(getType());
    }

    @Override
    public boolean typeIsAssignableFrom(Class<?> cl) {
        return typeIs(cl) || getType().isAssignableFrom(cl) || isCompatiblePrimitives(getType(), cl);
    }

    @Override
    public boolean typeNameIs(String name) {
        return getType().getName().equals(name);
    }

    @Override
    public boolean isCompatiblePrimitives(Class<?> fieldType, Class<?> cl) {
        return ClassUtil.isCompatiblePrimitives(fieldType, cl);
    }

    @Override
    public String getTypeName() {
        return getType().getName();
    }

    @Override
    public String getSimpleTypeName() {
        return getType().getSimpleName();
    }

    @Override
    public boolean isString() {
        return typeIs(String.class);
    }

    @Override
    public boolean isNumber() {
        return Number.class.isAssignableFrom(getType());
    }

    @Override
    public boolean isFractional() {
        return isNumber() && (isDouble() || isBigDecimal() || isFloat());
    }

    @Override
    public boolean isShort() {
        return typeIs(Short.class) || isPrimitiveWithName("short");
    }

    @Override
    public boolean isInteger() {
        return typeIs(Integer.class) || isPrimitiveWithName("int");
    }

    @Override
    public boolean isDouble() {
        return typeIs(Double.class) || isPrimitiveWithName("double");
    }

    @Override
    public boolean isLong() {
        return typeIs(Long.class) || isPrimitiveWithName("long");
    }

    @Override
    public boolean isFloat() {
        return typeIs(Float.class) || isPrimitiveWithName("float");
    }

    @Override
    public boolean isByte() {
        return typeIs(Byte.class) || isPrimitiveWithName("byte");
    }

    @Override
    public boolean isBoolean() {
        return typeIs(Boolean.class) || isPrimitiveWithName("boolean");
    }

    @Override
    public boolean isBigDecimal() {
        return typeIs(BigDecimal.class);
    }

    @Override
    public boolean isBigInteger() {
        return typeIs(BigInteger.class);
    }

    @Override
    public boolean isDate() {
        return typeIs(Date.class);
    }

    @Override
    public boolean isPrimitiveWithName(String name) {
        return isPrimitive() && typeNameIs(name);
    }

    @Override
    public boolean isPrimitive() {
        return getType().isPrimitive();
    }

    @Override
    public int getTypeModifiers() {
        return getType().getModifiers();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(getTypeModifiers());
    }

    @Override
    public boolean isEnum() {
        return getType().isEnum();
    }
}
