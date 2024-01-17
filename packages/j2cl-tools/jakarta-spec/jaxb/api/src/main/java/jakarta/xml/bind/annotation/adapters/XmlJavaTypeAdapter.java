/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation.adapters;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSchemaTypes;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Use an adapter that implements {@link XmlAdapter} for custom marshaling.
 *
 * <p><b> Usage: </b>
 *
 * <p>The {@code @XmlJavaTypeAdapter} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a JavaBean property
 *   <li>field
 *   <li>parameter
 *   <li>package
 *   <li>from within {@link XmlJavaTypeAdapters}
 * </ul>
 *
 * <p>When {@code @XmlJavaTypeAdapter} annotation is defined on a class, it applies to all
 * references to the class.
 *
 * <p>When {@code @XmlJavaTypeAdapter} annotation is defined at the package level it applies to all
 * references from within the package to {@code @XmlJavaTypeAdapter.type()}.
 *
 * <p>When {@code @XmlJavaTypeAdapter} annotation is defined on the field, property or parameter,
 * then the annotation applies to the field, property or the parameter only.
 *
 * <p>A {@code @XmlJavaTypeAdapter} annotation on a field, property or parameter overrides the
 * {@code @XmlJavaTypeAdapter} annotation associated with the class being referenced by the field,
 * property or parameter.
 *
 * <p>A {@code @XmlJavaTypeAdapter} annotation on a class overrides the {@code @XmlJavaTypeAdapter}
 * annotation specified at the package level for that class.
 *
 * <p>This annotation can be used with the following other annotations: {@link XmlElement}, {@link
 * XmlAttribute}, {@link XmlElementRef}, {@link XmlElementRefs}, {@link XmlAnyElement}. This can
 * also be used at the package level with the following annotations: {@link XmlAccessorType}, {@link
 * XmlSchema}, {@link XmlSchemaType}, {@link XmlSchemaTypes}.
 *
 * <p><b> Example: </b> See example in {@link XmlAdapter}
 *
 * @author
 *     <ul>
 *       <li>Sekhar Vajjhala, Sun Microsystems Inc.
 *       <li>Kohsuke Kawaguchi, Sun Microsystems Inc.
 *     </ul>
 *
 * @since 1.6, JAXB 2.0
 * @see XmlAdapter
 */
@Retention(RUNTIME)
@Target({PACKAGE, FIELD, METHOD, TYPE, PARAMETER})
public @interface XmlJavaTypeAdapter {
  /**
   * Points to the class that converts a value type to a bound type or vice versa. See {@link
   * XmlAdapter} for more details.
   */
  @SuppressWarnings({"rawtypes"})
  Class<? extends XmlAdapter> value();

  /**
   * If this annotation is used at the package level, then value of the type() must be specified.
   */
  Class<?> type() default DEFAULT.class;

  /**
   * Used in {@link XmlJavaTypeAdapter#type()} to signal that the type be inferred from the
   * signature of the field, property, parameter or the class.
   */
  final class DEFAULT {
    private DEFAULT() {}
  }
}
