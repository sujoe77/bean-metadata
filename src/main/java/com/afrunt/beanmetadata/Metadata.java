package com.afrunt.beanmetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrii Frunt
 */
public class Metadata<BM extends BeanMetadata<FM>, FM extends FieldMetadata> {

    private Set<BM> beansMetadata = new HashSet<>();

    public Set<BM> getBeansMetadata() {
        return beansMetadata;
    }

    public Metadata<BM, FM> setBeansMetadata(Set<BM> beansMetadata) {
        this.beansMetadata = beansMetadata;
        return this;
    }
}
