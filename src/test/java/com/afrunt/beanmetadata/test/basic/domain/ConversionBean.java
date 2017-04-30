package com.afrunt.beanmetadata.test.basic.domain;

/**
 * @author Andrii Frunt
 */
public class ConversionBean {
    private String stringField;
    private Integer integerField;

    public String getStringField() {
        return stringField;
    }

    public ConversionBean setStringField(String stringField) {
        this.stringField = stringField;
        return this;
    }

    public Integer getIntegerField() {
        return integerField;
    }

    public ConversionBean setIntegerField(Integer integerField) {
        this.integerField = integerField;
        return this;
    }
}
