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
 * Controls whether fields or Javabean properties are serialized by default.
 *
 * <p><b> Usage </b>
 *
 * <p>{@code @XmlAccessorType} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>package
 *   <li>a top level class
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * <p>This annotation provides control over the default serialization of properties and fields in a
 * class.
 *
 * <p>The annotation {@code @XmlAccessorType} on a package applies to all classes in the package.
 * The following inheritance semantics apply:
 *
 * <ul>
 *   <li>If there is a {@code @XmlAccessorType} on a class, then it is used.
 *   <li>Otherwise, if a {@code @XmlAccessorType} exists on one of its super classes, then it is
 *       inherited.
 *   <li>Otherwise, the {@code @XmlAccessorType} on a package is inherited.
 * </ul>
 *
 * <p><b> Defaulting Rules: </b>
 *
 * <p>By default, if {@code @XmlAccessorType} on a package is absent, then the following package
 * level annotation is assumed.
 *
 * <pre>
 *   &#64;XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
 * </pre>
 *
 * <p>By default, if {@code @XmlAccessorType} on a class is absent, and none of its super classes is
 * annotated with {@code @XmlAccessorType}, then the following default on the class is assumed:
 *
 * <pre>
 *   &#64;XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
 * </pre>
 *
 * <p>This annotation can be used with the following annotations: {@link XmlType}, {@link
 * XmlRootElement}, {@link XmlAccessorOrder}, {@link XmlSchema}, {@link XmlSchemaType}, {@link
 * XmlSchemaTypes}, , {@link XmlJavaTypeAdapter}. It can also be used with the following annotations
 * at the package level: {@link XmlJavaTypeAdapter}.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 * @see XmlAccessType
 */
@Inherited
@Retention(RUNTIME)
@Target({PACKAGE, TYPE})
public @interface XmlAccessorType {

  /**
   * Specifies whether fields or properties are serialized.
   *
   * @see XmlAccessType
   */
  XmlAccessType value() default XmlAccessType.PUBLIC_MEMBER;
}
