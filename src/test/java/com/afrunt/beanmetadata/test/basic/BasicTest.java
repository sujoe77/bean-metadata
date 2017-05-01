package com.afrunt.beanmetadata.test.basic;

import com.afrunt.beanmetadata.BasicMetadataCollector;
import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.beanmetadata.FieldMetadata;
import com.afrunt.beanmetadata.Metadata;

import java.util.Collection;

/**
 * @author Andrii Frunt
 */
public abstract class BasicTest {
    private Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata;


    protected abstract Collection<Class<?>> classes();

    protected Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> getMetadata(){
        if (metadata == null) {
            metadata = new BasicMetadataCollector().collectMetadata(classes());
        }

        return metadata;
    }
}
