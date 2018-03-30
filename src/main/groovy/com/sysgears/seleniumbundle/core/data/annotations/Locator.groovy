package com.sysgears.seleniumbundle.core.data.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Maps a specific data entry from the structured data set to a method argument.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@interface Locator {

    /**
     * Path to a specific value is specified with dot separator like data.users.admin.
     */
    String value()
}
