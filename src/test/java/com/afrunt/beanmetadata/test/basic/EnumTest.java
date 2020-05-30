package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.Metadata;
import com.afrunt.beanmetadata.test.basic.domain.BeanWithEnumField;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Andrii Frunt
 */
public class EnumTest extends BasicTest {
    @Test
    public void test() {
        Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = getMetadata();

        BeanMetadata<FieldMetadata> beanMetadata = metadata.getBeanMetadata(BeanWithEnumField.class);

        FieldMetadata enumField = beanMetadata.getFieldMetadata("enumField");

        Assert.assertTrue(enumField.isEnum());
    }

    @Override
    protected Collection<Class<?>> classes() {
        return Collections.<Class<?>>singletonList(BeanWithEnumField.class);
    }
}
