package com.afrunt.beanmetadata.test.domain;

import com.afrunt.beanmetadata.test.annotation.AnotherFieldAnnotation;
import com.afrunt.beanmetadata.test.annotation.FieldAnnotation;
import com.afrunt.beanmetadata.test.annotation.TypeAnnotation;

/**
 * @author Andrii Frunt
 */
@TypeAnnotation("baseBean")
public class BaseBean {
    private String id;

    @FieldAnnotation("id")
    @AnotherFieldAnnotation
    public String getId() {
        return id;
    }

    public BaseBean setId(String id) {
        this.id = id;
        return this;
    }
}
