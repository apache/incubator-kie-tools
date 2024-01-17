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
 * Maps a class or an enum type to an XML element.
 *
 * <p><b>Usage</b>
 *
 * <p>The &#64;XmlRootElement annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a top level class
 *   <li>an enum type
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * <p>When a top level class or an enum type is annotated with the &#64;XmlRootElement annotation,
 * then its value is represented as XML element in an XML document.
 *
 * <p>This annotation can be used with the following annotations: {@link XmlType}, {@link XmlEnum},
 * {@link XmlAccessorType}, {@link XmlAccessorOrder}.
 *
 * <p><b>Example 1: </b> Associate an element with XML Schema type
 *
 * <pre>
 *     // Example: Code fragment
 *     &#64;XmlRootElement
 *     class Point {
 *        int x;
 *        int y;
 *        Point(int _x,int _y) {x=_x;y=_y;}
 *     }
 * </pre>
 *
 * <pre>
 *     //Example: Code fragment corresponding to XML output
 *     marshal( new Point(3,5), System.out);
 * </pre>
 *
 * <pre>{@code
 * <!-- Example: XML output -->
 * <point>
 *   <x> 3 </x>
 *   <y> 5 </y>
 * </point>
 * }</pre>
 *
 * The annotation causes an global element declaration to be produced in the schema. The global
 * element declaration is associated with the XML schema type to which the class is mapped.
 *
 * <pre>{@code
 * <!-- Example: XML schema definition -->
 * <xs:element name="point" type="point"/>
 * <xs:complexType name="point">
 *   <xs:sequence>
 *     <xs:element name="x" type="xs:int"/>
 *     <xs:element name="y" type="xs:int"/>
 *   </xs:sequence>
 * </xs:complexType>
 * }</pre>
 *
 * <p><b>Example 2: Orthogonality to type inheritance </b>
 *
 * <p>An element declaration annotated on a type is not inherited by its derived types. The
 * following example shows this.
 *
 * <pre>
 *     // Example: Code fragment
 *     &#64;XmlRootElement
 *     class Point3D extends Point {
 *         int z;
 *         Point3D(int _x,int _y,int _z) {super(_x,_y);z=_z;}
 *     }
 *
 *     //Example: Code fragment corresponding to XML output *
 *     marshal( new Point3D(3,5,0), System.out );
 * {@code
 *
 *     <!-- Example: XML output -->
 *     <!-- The element name is point3D not point -->
 *     <point3D>
 *       <x>3</x>
 *       <y>5</y>
 *       <z>0</z>
 *     </point3D>
 *
 *     <!-- Example: XML schema definition -->
 *     <xs:element name="point3D" type="point3D"/>
 *     <xs:complexType name="point3D">
 *       <xs:complexContent>
 *         <xs:extension base="point">
 *           <xs:sequence>
 *             <xs:element name="z" type="xs:int"/>
 *           </xs:sequence>
 *         </xs:extension>
 *       </xs:complexContent>
 *     </xs:complexType>
 * }</pre>
 *
 * <b>Example 3: </b> Associate a global element with XML Schema type to which the class is mapped.
 *
 * <pre>
 *     //Example: Code fragment
 *     &#64;XmlRootElement(name="PriceElement")
 *     public class USPrice {
 *         &#64;XmlElement
 *         public java.math.BigDecimal price;
 *     }
 * {@code
 *
 *     <!-- Example: XML schema definition -->
 *     <xs:element name="PriceElement" type="USPrice"/>
 *     <xs:complexType name="USPrice">
 *       <xs:sequence>
 *         <xs:element name="price" type="xs:decimal"/>
 *       </sequence>
 *     </xs:complexType>
 * }</pre>
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRootElement {
  /**
   * namespace name of the XML element.
   *
   * <p>If the value is "##default", then the XML namespace name is derived from the package of the
   * class ( {@link XmlSchema} ). If the package is unnamed, then the XML namespace is the default
   * empty namespace.
   */
  String namespace() default "##default";

  /**
   * local name of the XML element.
   *
   * <p>If the value is "##default", then the name is derived from the class name.
   */
  String name() default "##default";
}
