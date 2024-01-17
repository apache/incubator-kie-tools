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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Generates a wrapper element around XML representation.
 *
 * <p>This is primarily intended to be used to produce a wrapper XML element around collections. The
 * annotation therefore supports two forms of serialization shown below.
 *
 * <pre>{@code
 * //Example: code fragment
 *   int[] names;
 *
 * // XML Serialization Form 1 (Unwrapped collection)
 * <names> ... </names>
 * <names> ... </names>
 *
 * // XML Serialization Form 2 ( Wrapped collection )
 * <wrapperElement>
 *    <names> value-of-item </names>
 *    <names> value-of-item </names>
 *    ....
 * </wrapperElement>
 * }</pre>
 *
 * <p>The two serialized XML forms allow a null collection to be represented either by absence or
 * presence of an element with a nillable attribute.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @XmlElementWrapper} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>JavaBean property
 *   <li>non static, non transient field
 * </ul>
 *
 * <p>The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>The property must be a collection property
 *   <li>This annotation can be used with the following annotations: {@link XmlElement}, {@link
 *       XmlElements}, {@link XmlElementRef}, {@link XmlElementRefs}, {@link XmlJavaTypeAdapter}.
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * @author
 *     <ul>
 *       <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.
 *       <li>Sekhar Vajjhala, Sun Microsystems, Inc.
 *     </ul>
 *
 * @see XmlElement
 * @see XmlElements
 * @see XmlElementRef
 * @see XmlElementRefs
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlElementWrapper {
  /**
   * Name of the XML wrapper element. By default, the XML wrapper element name is derived from the
   * JavaBean property name.
   */
  String name() default "##default";

  /**
   * XML target namespace of the XML wrapper element.
   *
   * <p>If the value is "##default", then the namespace is determined as follows:
   *
   * <ol>
   *   <li>If the enclosing package has {@link XmlSchema} annotation, and its {@link
   *       XmlSchema#elementFormDefault() elementFormDefault} is {@link XmlNsForm#QUALIFIED
   *       QUALIFIED}, then the namespace of the enclosing class.
   *   <li>Otherwise "" (which produces unqualified element in the default namespace.
   * </ol>
   */
  String namespace() default "##default";

  /**
   * If true, the absence of the collection is represented by using {@code xsi:nil='true'}.
   * Otherwise, it is represented by the absence of the element.
   */
  boolean nillable() default false;

  /**
   * Customize the wrapper element declaration to be required.
   *
   * <p>If required() is true, then the corresponding generated XML schema element declaration will
   * have {@code minOccurs="1"}, to indicate that the wrapper element is always expected.
   *
   * <p>Note that this only affects the schema generation, and not the unmarshalling or marshalling
   * capability. This is simply a mechanism to let users express their application constraints
   * better.
   *
   * @since 1.6, JAXB 2.1
   */
  boolean required() default false;
}
