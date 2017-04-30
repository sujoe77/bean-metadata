package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.*;
import com.afrunt.beanmetadata.test.basic.domain.ConversionBean;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Andrii Frunt
 */
public class FieldConversionSupportTest {
    public static final java.util.List<Class<?>> BEANS = Arrays.asList(ConversionBean.class);

    @Test
    public void testMetadataCollection() {
        BasicMetadataCollector metadataCollector = new BasicMetadataCollector();
        Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = metadataCollector.collectMetadata(BEANS);
        SupportsFieldConversion supportsFieldConversion = new SupportsFieldConversion();

        BeanMetadata<FieldMetadata> beanMetadata = metadata.getBeanMetadata(ConversionBean.class);
        Assert.assertNotNull(beanMetadata);

        Object stringFieldConverted = supportsFieldConversion.fieldToValue("1", Integer.class, beanMetadata, beanMetadata.getFieldMetadata("stringField"));
        Assert.assertEquals(1, stringFieldConverted);

        try {
            supportsFieldConversion.fieldToValue(1, String.class,  beanMetadata, beanMetadata.getFieldMetadata("integerField"));
            Assert.fail("We do not have converter method fieldIntegerToString");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SupportsFieldConversion implements FieldConversionSupport<BeanMetadata<FieldMetadata>, FieldMetadata> {
        public Integer fieldStringToInteger(String value, BeanMetadata<FieldMetadata> bm, FieldMetadata fm) {
            return Integer.valueOf(value);
        }

        public String valueIntegerToString(Integer value, BeanMetadata<FieldMetadata> bm, FieldMetadata fm) {
            return String.valueOf(value);
        }
    }
}
