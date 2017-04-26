package com.afrunt.beanmetadata.test.domain;

import com.afrunt.beanmetadata.test.annotation.FieldAnnotation;
import com.afrunt.beanmetadata.test.annotation.TypeAnnotation;

/**
 * @author Andrii Frunt
 */
@TypeAnnotation("bean")
public class Bean extends BaseBean {
    private String value;

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
}
