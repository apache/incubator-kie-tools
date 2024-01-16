/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Maps an enum type {@link Enum} to XML representation.
 *
 * <p>This annotation, together with {@link XmlEnumValue} provides a mapping of enum type to XML
 * representation.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @XmlEnum} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>enum type
 * </ul>
 *
 * <p>The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>This annotation can be used the following other annotations: {@link XmlType}, {@link
 *       XmlRootElement}
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information
 *
 * <p>An enum type is mapped to a schema simple type with enumeration facets. The schema type is
 * derived from the Java type to which {@code @XmlEnum.value()}. Each enum constant
 * {@code @XmlEnumValue} must have a valid lexical representation for the type
 * {@code @XmlEnum.value()}.
 *
 * <p><b>Examples:</b> See examples in {@link XmlEnumValue}
 *
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlEnum {
  /** Java type that is mapped to a XML simple type. */
  Class<?> value() default String.class;
}
