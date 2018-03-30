package com.sysgears.seleniumbundle.core.data.annotations

import com.sysgears.seleniumbundle.core.data.DataMapper

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Defines a search query for {@link DataMapper} so it could retrieve specific data sets from test data files.
 * Uses {@link Find} as an arguments for specifying key-value pairs as search criteria.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Query {

    /**
     * Helps to locate required data sets by key-value pairs.
     */
    Find[] value()
}
