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
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Maps a Java type to a simple schema built-in type.
 *
 * <p><b>Usage</b>
 *
 * <p>{@code @XmlSchemaType} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a JavaBean property
 *   <li>field
 *   <li>package
 * </ul>
 *
 * <p>{@code @XmlSchemaType} annotation defined for Java type applies to all references to the Java
 * type from a property/field. A {@code @XmlSchemaType} annotation specified on the property/field
 * overrides the {@code @XmlSchemaType} annotation specified at the package level.
 *
 * <p>This annotation can be used with the following annotations: {@link XmlElement}, {@link
 * XmlAttribute}.
 *
 * <p><b>Example 1: </b> Customize mapping of XMLGregorianCalendar on the field.
 *
 * <pre>
 *     //Example: Code fragment
 *     public class USPrice {
 *         &#64;XmlElement
 *         &#64;XmlSchemaType(name="date")
 *         public XMLGregorianCalendar date;
 *     }
 * {@code
 *
 *     <!-- Example: Local XML Schema element -->
 *     <xs:complexType name="USPrice"/>
 *       <xs:sequence>
 *         <xs:element name="date" type="xs:date"/>
 *       </sequence>
 *     </xs:complexType>
 * }</pre>
 *
 * <p><b> Example 2: </b> Customize mapping of XMLGregorianCalendar at package level
 *
 * <pre>
 *     package foo;
 *     &#64;jakarta.xml.bind.annotation.XmlSchemaType(
 *          name="date", type=javax.xml.datatype.XMLGregorianCalendar.class)
 *     }
 * </pre>
 *
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PACKAGE})
public @interface XmlSchemaType {
  String name();

  String namespace() default "http://www.w3.org/2001/XMLSchema";
  /**
   * If this annotation is used at the package level, then value of the type() must be specified.
   */
  Class<?> type() default DEFAULT.class;

  /**
   * Used in {@link XmlSchemaType#type()} to signal that the type be inferred from the signature of
   * the property.
   */
  final class DEFAULT {
    private DEFAULT() {};
  }
}
