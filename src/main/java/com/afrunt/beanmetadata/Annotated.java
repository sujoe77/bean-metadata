package com.afrunt.beanmetadata;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author Andrii Frunt
 */
public interface Annotated {
    Set<Annotation> getAnnotations();

    Annotated setAnnotations(Set<Annotation> annotations);

    default Collection<Class<? extends Annotation>> getAnnotationTypes() {
        if (getAnnotations() != null) {
            return getAnnotations().stream()
                    .map(Annotation::getClass)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    default <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return ofNullable(getAnnotations()).orElse(Collections.emptySet()).stream()
                .filter(a -> annotationType.isAssignableFrom(a.getClass()) || a.getClass().isAssignableFrom(annotationType))
                .map(a -> (T) a)
                .findFirst()
                .orElse(null);
    }

    default Annotated addAnnotation(Annotation annotation) {
        Set<Annotation> annotations = new HashSet<>(getAnnotations());
        annotations.remove(getAnnotation(annotation.getClass()));
        annotations.add(annotation);
        setAnnotations(annotations);
        return this;
    }

    default Annotated addAnnotations(Collection<Annotation> annotations) {
        annotations.forEach(this::addAnnotation);
        return this;
    }

    default Annotated addAnnotations(Annotation[] annotations) {
        addAnnotations(Arrays.asList(annotations));
        return this;
    }

    default <T extends Annotation> T removeAnnotation(Class<T> annotationType) {
        T annotation = getAnnotation(annotationType);
        HashSet<Annotation> annotations = new HashSet<>(getAnnotations());
        annotations.remove(annotation);
        setAnnotations(annotations);
        return annotation;
    }

    default Set<? extends Annotation> removeAnnotations(Collection<Class<? extends Annotation>> annotationTypes) {
        return annotationTypes.stream()
                .map(this::removeAnnotation)
                .collect(Collectors.toSet());
    }

    default Set<Annotation> removeAllAnnotations() {
        Set<Annotation> annotations = getAnnotations();
        setAnnotations(new HashSet<>());
        return annotations;
    }

    default boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
        return getAnnotation(annotation) != null;
    }

    default boolean notAnnotatedWith(Class<? extends Annotation> annotation) {
        return !isAnnotatedWith(annotation);
    }
}
