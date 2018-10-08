package com.sysgears.seleniumbundle.core.pagemodel.annotations

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


/**
 * Indicates a static element on a page. A static element is the element that is added to the Document Object Model
 * of a page right after the page was opened contrasting to dynamic elements that are added only as a reaction to
 * user actions.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface StaticElement {
}