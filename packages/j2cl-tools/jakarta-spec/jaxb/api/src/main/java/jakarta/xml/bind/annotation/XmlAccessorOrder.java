/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Controls the ordering of fields and properties in a class.
 *
 * <h2>Usage </h2>
 *
 * <p>{@code @XmlAccessorOrder} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>package
 *   <li>a top level class
 * </ul>
 *
 * <p>See "Package Specification" in {@code jakarta.xml.bind} package javadoc for additional common
 * information.
 *
 * <p>The effective {@link XmlAccessOrder} on a class is determined as follows:
 *
 * <ul>
 *   <li>If there is a {@code @XmlAccessorOrder} on a class, then it is used.
 *   <li>Otherwise, if a {@code @XmlAccessorOrder} exists on one of its super classes, then it is
 *       inherited (by the virtue of {@link Inherited})
 *   <li>Otherwise, the {@code @XmlAccessorOrder} on the package of the class is used, if it's
 *       there.
 *   <li>Otherwise {@link XmlAccessOrder#UNDEFINED}.
 * </ul>
 *
 * <p>This annotation can be used with the following annotations: {@link XmlType}, {@link
 * XmlRootElement}, {@link XmlAccessorType}, {@link XmlSchema}, {@link XmlSchemaType}, {@link
 * XmlSchemaTypes}, , {@link XmlJavaTypeAdapter}. It can also be used with the following annotations
 * at the package level: {@link XmlJavaTypeAdapter}.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 * @see XmlAccessOrder
 */
@Inherited
@Retention(RUNTIME)
@Target({PACKAGE, TYPE})
public @interface XmlAccessorOrder {
  XmlAccessOrder value() default XmlAccessOrder.UNDEFINED;
}
