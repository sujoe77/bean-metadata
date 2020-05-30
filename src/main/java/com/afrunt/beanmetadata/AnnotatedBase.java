package com.afrunt.beanmetadata;

import java8.util.Optional;
import java8.util.function.Consumer;
import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.lang.annotation.Annotation;
import java.util.*;

public abstract class AnnotatedBase extends TypedBase implements Annotated {
    @Override
    public abstract Map<Class<? extends Annotation>, Annotation> getAnnotationsMap();

    @Override
    public abstract Annotated setAnnotationsMap(Map<Class<? extends Annotation>, Annotation> annotationsMap);

    @Override
    public Set<Annotation> getAnnotations() {
        return new HashSet<>(getAnnotationsMap().values());
    }

    @Override
    public Annotated setAnnotations(Set<Annotation> annotations) {
        Map<Class<? extends Annotation>, Annotation> map = new HashMap<>();
        for (Annotation a : annotations) {
            map.put(a.annotationType(), a);
        }
        setAnnotationsMap(map);
        return this;
    }

    @Override
    public Collection<Class<? extends Annotation>> getAnnotationTypes() {
        return new HashSet<>(getAnnotationsMap().keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return (T) getAnnotationsMap().get(annotationType);
    }

    @Override
    public <T extends Annotation> Optional<T> getOptionalAnnotation(Class<T> annotationType) {
        return Optional.ofNullable(getAnnotation(annotationType));
    }

    @Override
    public Annotated addAnnotation(Annotation annotation) {
        HashMap<Class<? extends Annotation>, Annotation> map = new HashMap<>(getAnnotationsMap());
        map.put(annotation.annotationType(), annotation);
        setAnnotationsMap(map);
        return this;
    }

    @Override
    public Annotated addAnnotations(Collection<Annotation> annotations) {
        StreamSupport.stream(annotations).forEach(new Consumer<Annotation>() {
            @Override
            public void accept(Annotation annotation) {
                addAnnotation(annotation);
            }
        });
        return this;
    }

    @Override
    public Annotated addAnnotations(Annotation[] annotations) {
        addAnnotations(Arrays.asList(annotations));
        return this;
    }

    @Override
    public <T extends Annotation> T removeAnnotation(Class<T> annotationType) {
        T annotation = getAnnotation(annotationType);
        HashMap<Class<? extends Annotation>, Annotation> map = new HashMap<>(getAnnotationsMap());
        map.remove(annotationType);
        setAnnotationsMap(map);
        return annotation;
    }

    @Override
    public Set<? extends Annotation> removeAnnotations(Collection<Class<? extends Annotation>> annotationTypes) {
        return StreamSupport.stream(annotationTypes).map(new Function<Class<? extends Annotation>, Annotation>() {
            @Override
            public Annotation apply(Class<? extends Annotation> tClass) {
                return removeAnnotation(tClass);
            }
        }).collect(Collectors.<Annotation>toSet());
    }

    @Override
    public Set<Annotation> removeAllAnnotations() {
        Set<Annotation> annotations = getAnnotations();
        setAnnotationsMap(new HashMap<Class<? extends Annotation>, Annotation>());
        return annotations;
    }

    @Override
    public boolean isAnnotatedWith(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isAnnotatedWithAll(Class<? extends Annotation>... annotations) {
        return getAnnotationsMap().keySet().containsAll(Arrays.asList(annotations));
    }

    @Override
    public boolean notAnnotatedWith(Class<? extends Annotation> annotation) {
        return !isAnnotatedWith(annotation);
    }
}
