package com.sysgears.seleniumbundle.core.implicitinit.annotations

import com.sysgears.seleniumbundle.core.implicitinit.ParameterMapper

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Indicates that a field should be initialized implicitly with {@link ParameterMapper}. {@link ParameterMapper} has the
 * 'pattern' and 'required' parameters which allow for setting constraints for a field value and specify whether the
 * field is mandatory.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface ImplicitInit {

    /**
     * Pattern for parameter validation.
     */
    String pattern() default ".*"

    /**
     * If set to true, validation of the parameter will fail if its value is null.
     */
    boolean isRequired() default false
}