package com.afrunt.beanmetadata;

import com.afrunt.beanmetadata.annotation.RemoveInheritedAnnotations;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

/**
 * @author Andrii Frunt
 */
public abstract class MetadataCollector<M extends Metadata<BM, FM>, BM extends BeanMetadata<FM>, FM extends FieldMetadata> {
    public M collectMetadata(Collection<Class<?>> classes) {
        M metadata = newMetadata();

        metadata.setBeansMetadata(
                classes.stream().map(this::collectTypeMetadata).collect(Collectors.toSet())
        );

        return metadata;
    }

    protected abstract M newMetadata();

    protected abstract BM newBeanMetadata();

    protected abstract FM newFieldMetadata();

    protected BM collectTypeMetadata(Class<?> cl) {
        BM tm = newBeanMetadata();
        tm.setType(cl);

        return collectBeanMetadataFromClassHierarchy(classHierarchy(cl), tm);
    }

    protected BM collectBeanMetadataFromClassHierarchy(List<Class<?>> hierarchy, BM typeMetadata) {
        if (!hierarchy.isEmpty()) {
            Class<?> type = hierarchy.get(0);
            List<Class<?>> tailOfHierarchy = hierarchy.subList(1, hierarchy.size());
            return collectBeanMetadataFromClassHierarchy(tailOfHierarchy, collectTypeMetadataFromClass(type, typeMetadata));
        } else {
            return typeMetadata;
        }
    }

    protected BM collectTypeMetadataFromClass(Class<?> cl, BM beanMetadata) {
        beanMetadata = collectBeanMetadata(cl, beanMetadata);

        Set<Method> annotatedGetters = collectFieldsGetters(cl);

        beanMetadata.addFieldsMetadata(collectFieldsMetadata(cl, annotatedGetters, beanMetadata));

        return beanMetadata;
    }

    private BM collectBeanMetadata(Class<?> cl, BM beanMetadata) {
        beanMetadata = addAnnotations(beanMetadata, cl.getDeclaredAnnotations());
        return beanMetadata;
    }

    private Collection<Class<? extends Annotation>> annotationsToRemove(Annotated annotated, Annotation[] declaredAnnotations) {
        return annotationsToRemove(Arrays.asList(declaredAnnotations), annotated);
    }

    protected <T extends Annotated> T addAnnotations(T annotated, Annotation[] declaredAnnotations) {
        annotated.removeAnnotations(annotationsToRemove(annotated, declaredAnnotations));
        Collection<Annotation> filteredDeclaredAnnotations = filterSkippedAnnotations(declaredAnnotations);
        annotated.addAnnotations(filteredDeclaredAnnotations);
        return annotated;
    }

    @SuppressWarnings("unchecked")
    private Collection<Class<? extends Annotation>> annotationsToRemove(Collection<Annotation> declaredAnnotations, Annotated annotated) {
        RemoveInheritedAnnotations removeInherited = (RemoveInheritedAnnotations) declaredAnnotations.stream().filter(a -> a instanceof RemoveInheritedAnnotations).findFirst().orElse(null);

        if (removeInherited != null) {
            List<Class<? extends Annotation>> removeOnlyClasses = Arrays.asList(removeInherited.removeOnly());

            if (removeOnlyClasses.isEmpty()) {
                return annotated.getAnnotationTypes();
            } else {
                return removeOnlyClasses.stream()
                        .filter(annotated::isAnnotatedWith)
                        .map(c -> (Class<Annotation>) c)
                        .collect(Collectors.toList());
            }

        } else {
            return Collections.emptyList();
        }
    }

    private Set<Method> collectFieldsGetters(Class<?> cl) {
        return Arrays.stream(cl.getDeclaredMethods())
                .filter(this::isValidGetter)
                .collect(Collectors.toSet());
    }

    protected Set<FM> collectFieldsMetadata(Class<?> cl, Set<Method> getters, BM typeMetadata) {
        Set<FM> result = new HashSet<>();
        for (Method getter : getters) {
            String fieldName = fieldNameFromGetter(getter);
            FM fm = typeMetadata.getOrCreateFieldMetadataByName(fieldName, newFieldMetadata());
            fm.setFieldType(getter.getReturnType());
            fm.setGetter(getter);
            result.add(collectFieldMetadata(cl, getter, fm));
        }
        return result;
    }

    protected FM collectFieldMetadata(Class<?> cl, Method getter, FM fieldMetadata) {
        //TODO: everything related to field annotation starts here
        fieldMetadata = addAnnotations(fieldMetadata, getter.getDeclaredAnnotations());
        fieldMetadata.setSetter(ofNullable(findSetterForGetter(cl, getter)).orElse(fieldMetadata.getSetter()));

        return fieldMetadata;
    }


    protected Collection<Annotation> filterSkippedAnnotations(Annotation[] annotations) {
        return filterSkippedAnnotations(Arrays.asList(annotations));
    }

    protected Collection<Annotation> filterSkippedAnnotations(Collection<Annotation> annotations) {
        return annotations.stream()
                .filter(a -> !isSkippedAnnotation(a))
                .collect(Collectors.toList());
    }

    protected boolean isSkippedAnnotation(Annotation annotation) {
        return annotation instanceof RemoveInheritedAnnotations;
    }

    protected boolean isValidGetter(Method m) {
        String name = m.getName();
        return name.startsWith("get")
                && !"".equals(name.replaceFirst("get", ""))
                && !m.getReturnType().equals(Void.class)
                && m.getParameterCount() == 0;
    }

    protected List<Class<?>> classHierarchy(Class<?> cl) {
        List<Class<?>> hierarchy = new ArrayList<>();
        while (!Object.class.equals(cl)) {
            hierarchy.add(cl);
            cl = cl.getSuperclass();
        }
        Collections.reverse(hierarchy);
        return hierarchy;
    }

    protected Set<Method> collectFieldGettersForFieldNames(Class<?> cl, Set<String> fieldNames) {
        return fieldNames.stream()
                .map(fieldName -> findGetterForFieldName(cl, fieldName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    protected Method findGetterForFieldName(Class<?> cl, String fieldName) {
        return Arrays.stream(cl.getDeclaredMethods())
                .filter(m -> getterNameFromFieldName(fieldName).equals(m.getName()) && isValidGetter(m))
                .findFirst()
                .orElse(null);
    }

    protected Method findSetterForGetter(Class<?> cl, Method getter) {
        try {
            String setterName = "set" + StringUtils.capitalize(fieldNameFromGetter(getter));
            Method setter = cl.getMethod(setterName, getter.getReturnType());

            if (setter.getParameterCount() == 1) {
                return setter;
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            //Field without setter
            return null;
        }
    }

    private String getterNameFromFieldName(String fieldName) {
        return "get" + StringUtils.capitalize(fieldName);
    }

    private String fieldNameFromGetter(Method getter) {
        return uncapitalize(getter.getName().substring(3));
    }
}
