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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A container for multiple @{@link XmlElement} annotations.
 *
 * <p>Multiple annotations of the same type are not allowed on a program element. This annotation
 * therefore serves as a container annotation for multiple &#64;XmlElements as follows:
 *
 * <pre>
 * &#64;XmlElements({ @XmlElement(...),@XmlElement(...) })
 * </pre>
 *
 * <p>The {@code @XmlElements} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a JavaBean property
 *   <li>non static, non transient field
 * </ul>
 *
 * This annotation is intended for annotation a JavaBean collection property (e.g. List).
 *
 * <p><b>Usage</b>
 *
 * <p>The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>This annotation can be used with the following annotations: @{@link XmlIDREF}, @{@link
 *       XmlElementWrapper}.
 *   <li>If @XmlIDREF is also specified on the JavaBean property, then each &#64;XmlElement.type()
 *       must contain a JavaBean property annotated with {@code @XmlID}.
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information. <hr>
 *
 * <p><b>Example 1:</b> Map to a list of elements
 *
 * <pre>
 *
 *    // Mapped code fragment
 *    public class Foo {
 *        &#64;XmlElements(
 *            &#64;XmlElement(name="A", type=Integer.class),
 *            &#64;XmlElement(name="B", type=Float.class)
 *         )
 *         public List items;
 *    }
 * {@code
 *
 *    <!-- XML Representation for a List of {1,2.5}
 *            XML output is not wrapped using another element -->
 *    ...
 *    <A> 1 </A>
 *    <B> 2.5 </B>
 *    ...
 *
 *    <!-- XML Schema fragment -->
 *    <xs:complexType name="Foo">
 *      <xs:sequence>
 *        <xs:choice minOccurs="0" maxOccurs="unbounded">
 *          <xs:element name="A" type="xs:int"/>
 *          <xs:element name="B" type="xs:float"/>
 *        <xs:choice>
 *      </xs:sequence>
 *    </xs:complexType>
 *
 * }</pre>
 *
 * <p><b>Example 2:</b> Map to a list of elements wrapped with another element
 *
 * <pre>
 *
 *    // Mapped code fragment
 *    public class Foo {
 *        &#64;XmlElementWrapper(name="bar")
 *        &#64;XmlElements(
 *            &#64;XmlElement(name="A", type=Integer.class),
 *            &#64;XmlElement(name="B", type=Float.class)
 *        }
 *        public List items;
 *    }
 * {@code
 *
 *    <!-- XML Schema fragment -->
 *    <xs:complexType name="Foo">
 *      <xs:sequence>
 *        <xs:element name="bar">
 *          <xs:complexType>
 *            <xs:choice minOccurs="0" maxOccurs="unbounded">
 *              <xs:element name="A" type="xs:int"/>
 *              <xs:element name="B" type="xs:float"/>
 *            </xs:choice>
 *          </xs:complexType>
 *        </xs:element>
 *      </xs:sequence>
 *    </xs:complexType>
 * }</pre>
 *
 * <p><b>Example 3:</b> Change element name based on type using an adapter.
 *
 * <pre>
 *    class Foo {
 *       &#64;XmlJavaTypeAdapter(QtoPAdapter.class)
 *       &#64;XmlElements({
 *           &#64;XmlElement(name="A",type=PX.class),
 *           &#64;XmlElement(name="B",type=PY.class)
 *       })
 *       Q bar;
 *    }
 *
 *    &#64;XmlType abstract class P {...}
 *    &#64;XmlType(name="PX") class PX extends P {...}
 *    &#64;XmlType(name="PY") class PY extends P {...}
 * {@code
 *
 *    <!-- XML Schema fragment -->
 *    <xs:complexType name="Foo">
 *      <xs:sequence>
 *        <xs:element name="bar">
 *          <xs:complexType>
 *            <xs:choice minOccurs="0" maxOccurs="unbounded">
 *              <xs:element name="A" type="PX"/>
 *              <xs:element name="B" type="PY"/>
 *            </xs:choice>
 *          </xs:complexType>
 *        </xs:element>
 *      </xs:sequence>
 *    </xs:complexType>
 * }</pre>
 *
 * @author
 *     <ul>
 *       <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.
 *       <li>Sekhar Vajjhala, Sun Microsystems, Inc.
 *     </ul>
 *
 * @see XmlElement
 * @see XmlElementRef
 * @see XmlElementRefs
 * @see XmlJavaTypeAdapter
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlElements {
  /** Collection of @{@link XmlElement} annotations */
  XmlElement[] value();
}
