package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.BasicMetadataCollector;
import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.Metadata;
import com.afrunt.beanmetadata.test.basic.domain.Bean;
import com.afrunt.beanmetadata.test.basic.domain.SecondBean;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Andrii Frunt
 */
public class BasicMetadataCollectorTest {
    public static final java.util.List<Class<?>> BEANS = Arrays.asList(Bean.class, SecondBean.class);

    @Test
    public void testMetadataCollection() {

        Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = new BasicMetadataCollector().collectMetadata(BEANS);


        System.out.println();

    }
}
