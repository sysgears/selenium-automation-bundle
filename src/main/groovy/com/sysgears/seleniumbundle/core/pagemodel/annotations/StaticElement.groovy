package com.sysgears.seleniumbundle.core.pagemodel.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


/**
 * Indicates a static element on a page. A static element is the element which is added to the page DOM right after
 * the page was opened, contrary to dynamic elements which are added later as a reaction to user actions.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface StaticElement {
}