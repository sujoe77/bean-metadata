# bean-metadata
It is the simple library, that helps you to collect metadata of the java-beans
The easiest usage scenario is to collect metadata from one bean using BasicMetadataCollector
```java
BasicMetadataCollector metadataCollector = new BasicMetadataCollector();
BeanMetadata<FieldMetadata> beanMetadata = metadataCollector.collectBeanMetadata(Bean.class);

assertTrue(beanMetadata.isAnnotatedWith(TypeAnnotation.class));

assertEquals(2, beanMetadata.getFieldsMetadata().size());

assertEquals("bean", beanMetadata.getAnnotation(TypeAnnotation.class).value());

assertTrue(beanMetadata.hasField("id"));
assertTrue(beanMetadata.hasField("value"));

FieldMetadata id = beanMetadata.getFieldMetadata("id");

assertTrue(id.isAnnotatedWithAll(FieldAnnotation.class, AnotherFieldAnnotation.class));
assertTrue(id.isString());
```
