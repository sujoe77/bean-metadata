package com.afrunt.beanmetadata.test.basic.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Andrii Frunt
 */
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface TypeAnnotation {
    String value();
}
