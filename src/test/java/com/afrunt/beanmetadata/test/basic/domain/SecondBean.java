package com.afrunt.beanmetadata.test.basic.domain;

import com.afrunt.beanmetadata.annotation.RemoveInheritedAnnotations;
import com.afrunt.beanmetadata.test.basic.annotation.AnotherFieldAnnotation;
import com.afrunt.beanmetadata.test.basic.annotation.FieldAnnotation;
import com.afrunt.beanmetadata.test.basic.annotation.TypeAnnotation;

/**
 * @author Andrii Frunt
 */
@TypeAnnotation("secondBean")
public class SecondBean extends BaseBean {
    private String value;

    @Override
    @RemoveInheritedAnnotations(removeOnly = AnotherFieldAnnotation.class)
    public String getId() {
        return super.getId();
    }

    @FieldAnnotation("value")
    public String getValue() {
        return value;
    }

    public SecondBean setValue(String value) {
        this.value = value;
        return this;
    }
}
