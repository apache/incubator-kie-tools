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
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Maps a JavaBean property to a XML element derived from property name.
 *
 * <p><b>Usage</b>
 *
 * <p>{@code @XmlElement} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a JavaBean property
 *   <li>non static, non transient field
 *   <li>within {@link XmlElements}
 * </ul>
 *
 * The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>This annotation can be used with following annotations: {@link XmlID}, {@link XmlIDREF},
 *       {@link XmlList}, {@link XmlSchemaType}, {@link XmlValue}, {@link XmlAttachmentRef}, {@link
 *       XmlMimeType}, {@link XmlInlineBinaryData}, {@link XmlElementWrapper}, {@link
 *       XmlJavaTypeAdapter}
 *   <li>if the type of JavaBean property is a collection type of array, an indexed property, or a
 *       parameterized list, and this annotation is used with {@link XmlElements} then,
 *       {@code @XmlElement.type()} must be DEFAULT.class since the collection item type is already
 *       known.
 * </ul>
 *
 * <p>A JavaBean property, when annotated with @XmlElement annotation is mapped to a local element
 * in the XML Schema complex type to which the containing class is mapped.
 *
 * <p><b>Example 1: </b> Map a public non static non final field to local element
 *
 * <pre>
 *     //Example: Code fragment
 *     public class USPrice {
 *        {@literal @}XmlElement(name="itemprice")
 *         public java.math.BigDecimal price;
 *     }
 * {@code
 *
 *     <!-- Example: Local XML Schema element -->
 *     <xs:complexType name="USPrice"/>
 *       <xs:sequence>
 *         <xs:element name="itemprice" type="xs:decimal" minOccurs="0"/>
 *       </sequence>
 *     </xs:complexType>
 *   }</pre>
 *
 * <p><b> Example 2: </b> Map a field to a nillable element.
 *
 * <pre>
 *     //Example: Code fragment
 *     public class USPrice {
 *        {@literal @}XmlElement(nillable=true)
 *         public java.math.BigDecimal price;
 *     }
 * {@code
 *
 *     <!-- Example: Local XML Schema element -->
 *     <xs:complexType name="USPrice">
 *       <xs:sequence>
 *         <xs:element name="price" type="xs:decimal" nillable="true" minOccurs="0"/>
 *       </sequence>
 *     </xs:complexType>
 *   }</pre>
 *
 * <p><b> Example 3: </b> Map a field to a nillable, required element.
 *
 * <pre>
 *     //Example: Code fragment
 *     public class USPrice {
 *        {@literal @}XmlElement(nillable=true, required=true)
 *         public java.math.BigDecimal price;
 *     }
 * {@code
 *
 *     <!-- Example: Local XML Schema element -->
 *     <xs:complexType name="USPrice">
 *       <xs:sequence>
 *         <xs:element name="price" type="xs:decimal" nillable="true" minOccurs="1"/>
 *       </sequence>
 *     </xs:complexType>
 *   }</pre>
 *
 * <p><b>Example 4: </b>Map a JavaBean property to an XML element with anonymous type.
 *
 * <p>See Example 6 in @{@link XmlType}.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface XmlElement {
  /**
   * Name of the XML Schema element.
   *
   * <p>If the value is "##default", then element name is derived from the JavaBean property name.
   */
  String name() default "##default";

  /**
   * Customize the element declaration to be nillable.
   *
   * <p>If nillable() is true, then the JavaBean property is mapped to a XML Schema nillable element
   * declaration.
   */
  boolean nillable() default false;

  /**
   * Customize the element declaration to be required.
   *
   * <p>If required() is true, then Javabean property is mapped to an XML schema element declaration
   * with minOccurs="1". maxOccurs is "1" for a single valued property and "unbounded" for a
   * multivalued property.
   *
   * <p>If required() is false, then the Javabean property is mapped to XML Schema element
   * declaration with minOccurs="0". maxOccurs is "1" for a single valued property and "unbounded"
   * for a multivalued property.
   */
  boolean required() default false;

  /**
   * XML target namespace of the XML Schema element.
   *
   * <p>If the value is "##default", then the namespace is determined as follows:
   *
   * <ol>
   *   <li>If the enclosing package has {@link XmlSchema} annotation, and its {@link
   *       XmlSchema#elementFormDefault() elementFormDefault} is {@link XmlNsForm#QUALIFIED
   *       QUALIFIED}, then the namespace of the enclosing class.
   *   <li>Otherwise {@literal ''} (which produces unqualified element in the default namespace.
   * </ol>
   */
  String namespace() default "##default";

  /**
   * Default value of this element.
   *
   * <p>The
   *
   * <pre>'\u0000'</pre>
   *
   * value specified as a default of this annotation element is used as a poor-man's substitute for
   * null to allow implementations to recognize the 'no default value' state.
   */
  String defaultValue() default "\u0000";

  /** The Java class being referenced. */
  Class<?> type() default DEFAULT.class;

  /**
   * Used in {@link XmlElement#type()} to signal that the type be inferred from the signature of the
   * property.
   */
  final class DEFAULT {
    private DEFAULT() {}
  }
}
