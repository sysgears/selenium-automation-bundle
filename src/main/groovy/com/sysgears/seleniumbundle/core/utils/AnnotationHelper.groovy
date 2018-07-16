package com.sysgears.seleniumbundle.core.utils

import groovy.util.logging.Slf4j

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * Helps to get configurations for test methods.
 */
@Slf4j
class AnnotationHelper {

    /**
     * Gets a list of all annotations defined for the method parameters.
     *
     * @param method target method
     *
     * @return list of maps with annotations
     */
    static <T extends Annotation> List<Map<String, T>> getParameterAnnotations(Method method) {
        method.getParameters().collect { param ->
            param.getAnnotations().collectEntries {
                [it.annotationType().simpleName.toLowerCase(), it]
            }
        }
    }

    /**
     * Gets an annotation of a specified type for a given class or its parent classes.
     *
     * @param clazz class to get annotation for
     * @param annotationClass class of annotation
     *
     * @return object of a given annotation, <code>null</code> if class hasn't such annotation
     */
    static Annotation getClassAnnotation(Class clazz, Class<Annotation> annotationClass) {
        clazz.getAnnotation(annotationClass) ?:
                clazz.superclass ? getClassAnnotation(clazz.superclass, annotationClass) : null
    }
}
