package com.afrunt.beanmetadata;

/**
 * @author Andrii Frunt
 */
public class BasicMetadataCollector extends MetadataCollector<Metadata<BeanMetadata<FieldMetadata>, FieldMetadata>, BeanMetadata<FieldMetadata>, FieldMetadata> {
    @Override
    protected Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> newMetadata() {
        return new Metadata<>();
    }

    @Override
    protected BeanMetadata<FieldMetadata> newBeanMetadata() {
        return new BeanMetadata<>();
    }

    @Override
    protected FieldMetadata newFieldMetadata() {
        return new FieldMetadata();
    }

}
