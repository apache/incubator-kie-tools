package org.uberfire.client.views.pfly.menu;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import org.gwtbootstrap3.client.ui.NavbarBrand;

/**
 * Denotes that the target class or producer of type {@link NavbarBrand} provides the main branding link that appears above
 * the application's menu bar. Each application that uses PatternFly should have one such type or producer.
 * <p>
 * To fit with the overall layout, <b>the NavbarBrand widget that bears this qualifier must have a height of 25px.</b>
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MainBrand {

}
