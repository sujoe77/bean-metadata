package com.afrunt.beanmetadata;

/**
 * @author Andrii Frunt
 */
public class BeanMetadataException extends RuntimeException {
    public BeanMetadataException() {
    }

    public BeanMetadataException(String message) {
        super(message);
    }

    public BeanMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanMetadataException(Throwable cause) {
        super(cause);
    }
}
