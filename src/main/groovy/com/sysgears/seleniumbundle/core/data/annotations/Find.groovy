package com.sysgears.seleniumbundle.core.data.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Defines an argument of {@link Query} for locating specific data sets in test data files.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Find {

    /**
     * Key for specifying a specific data set.
     */
    String name()

    /**
     * Value for specifying a specific data set.
     */
    String value()
}
