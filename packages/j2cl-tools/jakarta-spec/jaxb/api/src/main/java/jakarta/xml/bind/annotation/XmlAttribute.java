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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Maps a JavaBean property to a XML attribute.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @XmlAttribute} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>JavaBean property
 *   <li>field
 * </ul>
 *
 * <p>A static final field is mapped to a XML fixed attribute.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information. The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>If type of the field or the property is a collection type, then the collection item type
 *       must be mapped to schema simple type.
 *       <pre>
 *     // Examples
 *     &#64;XmlAttribute List&lt;Integer&gt; items; //legal
 *     &#64;XmlAttribute List&lt;Bar&gt; foo; // illegal if Bar does not map to a schema simple type
 * </pre>
 *   <li>If the type of the field or the property is a non collection type, then the type of the
 *       property or field must map to a simple schema type.
 *       <pre>
 *     // Examples
 *     &#64;XmlAttribute int foo; // legal
 *     &#64;XmlAttribute Foo foo; // illegal if Foo does not map to a schema simple type
 * </pre>
 *   <li>This annotation can be used with the following annotations: {@link XmlID}, {@link
 *       XmlIDREF}, {@link XmlList}, {@link XmlSchemaType}, {@link XmlValue}, {@link
 *       XmlAttachmentRef}, {@link XmlMimeType}, {@link XmlInlineBinaryData}, {@link
 *       jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter}.
 * </ul>
 *
 * <p><b>Example 1: </b>Map a JavaBean property to an XML attribute.
 *
 * <pre>
 *     //Example: Code fragment
 *     public class USPrice {
 *         &#64;XmlAttribute
 *         public java.math.BigDecimal getPrice() {...} ;
 *         public void setPrice(java.math.BigDecimal ) {...};
 *     }
 * {@code
 *
 *     <!-- Example: XML Schema fragment -->
 *     <xs:complexType name="USPrice">
 *       <xs:sequence>
 *       </xs:sequence>
 *       <xs:attribute name="price" type="xs:decimal"/>
 *     </xs:complexType>
 * }</pre>
 *
 * <p><b>Example 2: </b>Map a JavaBean property to an XML attribute with anonymous type. See Example
 * 7 in @{@link XmlType}.
 *
 * <p><b>Example 3: </b>Map a JavaBean collection property to an XML attribute.
 *
 * <pre>
 *     // Example: Code fragment
 *     class Foo {
 *         ...
 *         &#64;XmlAttribute List&lt;Integer&gt; items;
 *     }
 * {@code
 *
 *     <!-- Example: XML Schema fragment -->
 *     <xs:complexType name="foo">
 *     	 ...
 *       <xs:attribute name="items">
 *         <xs:simpleType>
 *           <xs:list itemType="xs:int"/>
 *         </xs:simpleType>
 *     </xs:complexType>
 *
 * }</pre>
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @see XmlType
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlAttribute {
  /**
   * Name of the XML Schema attribute. By default, the XML Schema attribute name is derived from the
   * JavaBean property name.
   */
  String name() default "##default";

  /**
   * Specifies if the XML Schema attribute is optional or required. If true, then the JavaBean
   * property is mapped to a XML Schema attribute that is required. Otherwise it is mapped to a XML
   * Schema attribute that is optional.
   */
  boolean required() default false;

  /** Specifies the XML target namespace of the XML Schema attribute. */
  String namespace() default "##default";
}
