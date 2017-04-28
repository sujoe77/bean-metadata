package com.afrunt.beanmetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrii Frunt
 */
public class ClassUtil {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED_MAP;
    private static final Map<Class<?>, Class<?>> BOXED_TO_PRIMITIVE_MAP;

    static {
        Map<Class<?>, Class<?>> primitiveToBoxedMap = new HashMap<>();

        primitiveToBoxedMap.put(Boolean.TYPE, Boolean.class);
        primitiveToBoxedMap.put(Byte.TYPE, Byte.class);
        primitiveToBoxedMap.put(Character.TYPE, Character.class);

        primitiveToBoxedMap.put(Short.TYPE, Short.class);
        primitiveToBoxedMap.put(Integer.TYPE, Integer.class);
        primitiveToBoxedMap.put(Long.TYPE, Long.class);

        primitiveToBoxedMap.put(Double.TYPE, Double.class);
        primitiveToBoxedMap.put(Float.TYPE, Float.class);
        primitiveToBoxedMap.put(Void.TYPE, Void.class);

        Map<Class<?>, Class<?>> boxedToPrimitiveMap = new HashMap<>();

        for (Class<?> key : primitiveToBoxedMap.keySet()) {
            boxedToPrimitiveMap.put(primitiveToBoxedMap.get(key), key);
        }

        PRIMITIVE_TO_BOXED_MAP = Collections.unmodifiableMap(primitiveToBoxedMap);
        BOXED_TO_PRIMITIVE_MAP = Collections.unmodifiableMap(boxedToPrimitiveMap);
    }

    public static boolean isCompatiblePrimitives(Class<?> c1, Class<?> c2) {
        Class<?> boxedForPrimitive = PRIMITIVE_TO_BOXED_MAP.get(c1);

        if (boxedForPrimitive != null) {
            return c2.equals(boxedForPrimitive);
        }

        boxedForPrimitive = PRIMITIVE_TO_BOXED_MAP.get(c2);

        if (boxedForPrimitive != null) {
            return c1.equals(boxedForPrimitive);
        }

        Class<?> primitiveForBoxed = BOXED_TO_PRIMITIVE_MAP.get(c1);

        if (primitiveForBoxed != null) {
            return c2.equals(primitiveForBoxed);
        }

        primitiveForBoxed = BOXED_TO_PRIMITIVE_MAP.get(c2);

        if (primitiveForBoxed != null) {
            return c1.equals(primitiveForBoxed);
        }

        return c1.equals(c2);
    }

    private ClassUtil() {

    }
}
