/*
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.ui.shared.api.annotations;

import static org.jboss.errai.ui.shared.api.annotations.DataField.ConflictStrategy.USE_TEMPLATE;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import jsinterop.annotations.JsType;

/**
 * <p>
 * Indicates that the target Java reference (field, method parameter, or constructor parameter) in a {@link Templated}
 * component corresponds to an HTML element in the class's companion template. <b>This annotation can only be used in
 * classes that have been annotated with {@link Templated}, or in super-classes of such types.</b>
 * <p>
 * A field or parameter annotated with {@link DataField} should be one of the following:
 * <ul>
 * <li>A {@link Templated} component
 * <li>A native {@link JsType} wrapping an HTML element
 * <li>An Elemental HTML element wrapper
 * <li>A gwt-user {@link Element}
 * <li>A {@link Widget}
 *
 * <p>
 * The matching of Java references to HTML elements is performed as follows:
 * <ol>
 * <li>A <i>name</i> for the Java reference is determined. If the {@code @DataField} annotation has a {@link #value}
 * argument, it is used as the reference name. For fields, the default name is the field name. Method and constructor
 * parameters have no default name, so they must always specify a value.
 * <li>If there is an element in the HTML template with attribute <tt>data-field=<i>name</i></tt>, the Java reference
 * will point to this element. If there is more than one such element, the Java reference will point to the first.
 * <li>Otherwise, if there is an element in the HTML template with attribute <tt>id=<i>name</i></tt>, the Java reference
 * will point to this element. If there is more than one such element, the Java reference will point to the first.
 * <li>Otherwise, if there is an element in the HTML template with a CSS style class <tt><i>name</i></tt>, the Java
 * reference will point to this element. If there is more than one such element, the Java reference will point to the
 * first. For elements with more than one CSS style, each style name is considered individually. For example:
 *
 * <pre>
 *  &lt;div class="eat drink be-merry"&gt;
 * </pre>
 *
 * matches Java references named <tt>eat</tt>, <tt>drink</tt>, or <tt>be-merry</tt>.
 * <li>If no matching element is found by this point, it is an error.
 * </ol>
 * <p>
 * If more than one Java reference matches the same HTML element in the template, it is an error. For example, given a
 * template containing the element <tt>&lt;div class="eat drink be-merry"&gt;</tt>, the following Java code is in error:
 *
 * <pre>
 * &#64;Templated
 * public class ErroneousTemplate extends Composite {
 *   &#64;Inject
 *   &#64;DataField
 *   private Label eat;
 *
 *   &#64;Inject
 *   &#64;DataField
 *   private Label drink;
 * }
 * </pre>
 *
 * because both fields <tt>eat</tt> and <tt>drink</tt> refer to the same HTML <tt>div</tt> element.
 * <p>
 * When used on a method or constructor parameter, this must be accompanied by a corresponding {@link Inject}
 * annotation; however, when used on a field, construction may be performed manually or via {@link Inject}.
 *
 * <p>
 * <b>See also:</b> {@link Templated}, {@link EventHandler}, {@link Bound}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Christian Sadilek <csadilek@redhat.com>
 * @author Jonathan Fuerth <jfuerth@redhat.com>
 * @author Max Barkley <mbarkley@redhat.com>
 */
@Inherited
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataField {

  /**
   * Represents the different strategies used to resolve conflicts between attribute values of the template HTML element
   * and attributes of the {@link DataField}.
   */
  public static enum ConflictStrategy {
    /**
     * <p>
     * Uses attribute values from the {@link DataField} in the templated Java class when available. Under this strategy
     * an attribute value from the template is only used if the {@link DataField} does not already have a value for the
     * respective attribute.
     *
     * <p>
     * <b>Exceptions:</b>
     * <ul>
     * <li>CSS class names from the template and the {@link DataField} are all kept regardless of
     * {@link ConflictStrategy}.
     * <li>CSS styles (declared in the {@code style} property) are merged together, and this strategy is only applied to
     * conflicting property values (where the property value from the {@link DataField} in the templated Java class is used).
     * </ul>
     */
    USE_BEAN,
    /**
     * <p>
     * Uses attribute values from the HTML template element when available. Under this strategy all attribute values
     * from the template are used and values from the {@link DataField} are used if the templated HTML element does not
     * already have a value for the respective attribute.
     *
     * <p>
     * <b>Exceptions:</b>
     * <ul>
     * <li>CSS class names from the template and the {@link DataField} are all kept regardless of
     * {@link ConflictStrategy}.
     * <li>CSS styles (declared in the {@code style} property) are merged together, and this strategy is only applied to
     * conflicting property values (where the property value from the template HTML element is used).
     * </ul>
     */
    USE_TEMPLATE
  }

  /**
   * Holds a {@link ConflictStrategy} for a particular HTML element attribute.
   */
  public static @interface AttributeRule {
    /**
     * The name of an HTML Element attribute.
     */
    String name();

    /**
     * A {@link ConflictStrategy} used to merge values of the named HTML element attribute.
     */
    ConflictStrategy strategy();
  }

  /**
   * Specify the name of the <code>data-field</code> in the corresponding HTML template, which the annotated element
   * represents.
   */
  String value() default "";

  /**
   * Used to declare how values of attributes are merged from the HTML template element to this {@link DataField}.
   */
  AttributeRule[] attributeRules() default {};

  /**
   * Declare a default strategy used to mergve attribute values from the HTML template element to this
   * {@link DataField}. This strategy will be used for every attribute except ones overriden in {@link #attributeRules()}.
   */
  ConflictStrategy defaultStrategy() default USE_TEMPLATE;
}
