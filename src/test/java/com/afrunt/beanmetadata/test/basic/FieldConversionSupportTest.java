package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.*;
import com.afrunt.beanmetadata.test.basic.domain.ConversionBean;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrii Frunt
 */
public class FieldConversionSupportTest extends BasicTest {

    @Test
    public void testMetadataCollection() {
        Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = getMetadata();
        SupportsFieldConversion supportsFieldConversion = new SupportsFieldConversion();

        BeanMetadata<FieldMetadata> beanMetadata = metadata.getBeanMetadata(ConversionBean.class);
        Assert.assertNotNull(beanMetadata);

        Object stringFieldConverted = supportsFieldConversion.fieldToValue("1", Integer.class, beanMetadata, beanMetadata.getFieldMetadata("stringField"));
        Assert.assertEquals(1, stringFieldConverted);

        try {
            supportsFieldConversion.fieldToValue(1, String.class, beanMetadata, beanMetadata.getFieldMetadata("integerField"));
        } catch (Exception e) {
            Assert.fail("We do not have converter method fieldIntegerToString, but non-null values could be converted to string by default");
            e.printStackTrace();
        }
    }

    @Override
    protected Collection<Class<?>> classes() {
        return Collections.<Class<?>>singletonList(ConversionBean.class);
    }

    public static class SupportsFieldConversion extends FieldConversionSupportBase<BeanMetadata<FieldMetadata>, FieldMetadata> implements FieldConversionSupport<BeanMetadata<FieldMetadata>, FieldMetadata> {
        private Map<Integer, Method> methodsCache = new HashMap<>();

        @Override
        public Map<Integer, Method> getMethodsCache() {
            return methodsCache;
        }

        public Integer fieldStringToInteger(String value, BeanMetadata<FieldMetadata> bm, FieldMetadata fm) {
            return Integer.valueOf(value);
        }

        public String valueIntegerToString(Integer value, BeanMetadata<FieldMetadata> bm, FieldMetadata fm) {
            return String.valueOf(value);
        }
    }
}
