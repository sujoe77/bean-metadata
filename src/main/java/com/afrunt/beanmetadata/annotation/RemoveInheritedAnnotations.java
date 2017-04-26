package com.afrunt.beanmetadata.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Andrii Frunt
 */
@Target(value = {METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface RemoveInheritedAnnotations {
    Class<? extends Annotation>[] removeOnly() default {};
}
