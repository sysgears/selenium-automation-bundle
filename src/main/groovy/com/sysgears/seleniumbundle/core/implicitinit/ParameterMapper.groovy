package com.sysgears.seleniumbundle.core.implicitinit

import com.sysgears.seleniumbundle.core.implicitinit.annotations.ImplicitInit
import groovy.util.logging.Slf4j

import java.lang.reflect.Field

/**
 * Provides methods for initializing and validating objects fields that have the {@link ImplicitInit} annotation.
 */
@Slf4j
class ParameterMapper {

    /**
     * Initialize all the object fields that have the {@link ImplicitInit} annotation.
     *
     * @param object object to be initialized
     * @param parameters map of parameters to be used for object initialization
     *
     * @throws IllegalArgumentException is thrown in case a value is missing for a mandatory parameter
     * or the value doesn't match the validation pattern
     */
    void initParameters(Object object, Map<String, ?> arguments) throws IllegalArgumentException {
        getFieldsToInitialize(object.getClass()).each { field ->
            def name = field.name
            def value = arguments.entrySet().find { it.key.equalsIgnoreCase(name) }?.value
            def annotation = field.getAnnotation(ImplicitInit.class)
            def pattern = annotation.pattern()

            if (annotation.isRequired() && !value) {
                log.error("Parameter [$name] is mandatory, but it is not defined.")
                throw new IllegalArgumentException("Parameter [$name], but it is not defined.")
            }

            if (value && pattern && !validate(value, pattern)) {
                log.error("Parameter [$name] has an invalid value [$value].")
                throw new IllegalArgumentException("Parameter [$name] has an invalid value [$value].")
            }

            object."$name" = value
        }
    }

    /**
     * Checks if the given value matches the pattern.
     *
     * @param argument value to verify
     * @param pattern validation pattern
     *
     * @return true if argument matches the pattern, false otherwise
     */
    private boolean validate(String value, String pattern) {
        value.matches(pattern)
    }

    /**
     * Checks if all the values from a given list match the pattern.
     *
     * @param arguments list of values to verify
     * @param pattern validation pattern
     *
     * @return true if all arguments from the list match the pattern, false otherwise
     */
    private boolean validate(List<String> values, String pattern) {
        values.every { validate(it, pattern) }
    }

    /**
     * Checks if all the values from a given map match the pattern.
     *
     * @param arguments map of values to verify
     * @param pattern validation pattern
     *
     * @return true if all arguments from map match the pattern, false otherwise
     */
    private boolean validate(Map arguments, String pattern) {

        // Casting to ConfigObject is used to flatten the nested maps
        def values = (arguments as ConfigObject).flatten().values().toList()
        validate(values, pattern)
    }

    /**
     * Gets all declared fields with {@link ImplicitInit} annotation of a given class and its superclasses.
     *
     * @param clazz class to get fields from
     *
     * @return list of fields of a class and all its superclasses which are annotated with {@link ImplicitInit}
     */
    private List<Field> getFieldsToInitialize(Class clazz) {
        def parent = clazz.getSuperclass()
        def fields = clazz.getDeclaredFields().findAll { it.getAnnotation(ImplicitInit.class) }

        parent ? fields += getFieldsToInitialize(parent) : fields
    }
}
