package com.afrunt.beanmetadata.test.basic.domain;

/**
 * @author Andrii Frunt
 */
public class BeanWithEnumField {
    private EnumType enumField;

    public EnumType getEnumField() {
        return enumField;
    }

    public BeanWithEnumField setEnumField(EnumType enumField) {
        this.enumField = enumField;
        return this;
    }
}
