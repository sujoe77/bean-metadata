# bean-metadata
It is the simple library, that helps you to collect metadata of the java-beans.
[jACH project](https://github.com/afrunt/jach) is built on top of this framework, so there you can find the best example of use 

Add bean-metadata to your project. for maven projects just add this dependency:
```xml
<dependency>
  <groupId>com.afrunt</groupId>
  <artifactId>bean-metadata</artifactId>
  <version>0.1-dev</version>
</dependency>
```

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
You can collect the metadata from collection of beans
```java
BasicMetadataCollector metadataCollector = new BasicMetadataCollector();
Metadata<BeanMetadata<FieldMetadata>, FieldMetadata> metadata = metadataCollector.collectMetadata(BEANS);
```
But the most flexible scenario is to extend the MetadataCollector and types of metadata
```java
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
```
