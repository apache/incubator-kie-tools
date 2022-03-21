package org.uberfire.workbench.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation represents a Resource that should be visible to end users. Any other resource not annotated with it
 * should not be shown. In case you need to inject a Resource that contains @VisibleAsset in an object that doesn't need
 * this concept you can add @Default qualifier to it.
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, PARAMETER, FIELD, METHOD})
public @interface VisibleAsset {

}
