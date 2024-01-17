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
 * Maps a class or an enum type to a XML Schema type.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @XmlType} annnotation can be used with the following program elements:
 *
 * <ul>
 *   <li>a top level class
 *   <li>an enum type
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * <h2>Mapping a Class </h2>
 *
 * <p>A class maps to a XML Schema type. A class is a data container for values represented by
 * properties and fields. A schema type is a data container for values represented by schema
 * components within a schema type's content model (e.g. model groups, attributes etc).
 *
 * <p>To be mapped, a class must either have a public no-arg constructor or a static no-arg factory
 * method. The static factory method can be specified in {@code factoryMethod()} and {@code
 * factoryClass()} annotation elements. The static factory method or the no-arg constructor is used
 * during unmarshalling to create an instance of this class. If both are present, the static factory
 * method overrides the no-arg constructor.
 *
 * <p>A class maps to either a XML Schema complex type or a XML Schema simple type. The XML Schema
 * type is derived based on the mapping of JavaBean properties and fields contained within the
 * class. The schema type to which the class is mapped can either be named or anonymous. A class can
 * be mapped to an anonymous schema type by annotating the class with {@code @XmlType(name="")}.
 *
 * <p>Either a global element, local element or a local attribute can be associated with an
 * anonymous type as follows:
 *
 * <ul>
 *   <li><b>global element: </b> A global element of an anonymous type can be derived by annotating
 *       the class with @{@link XmlRootElement}. See Example 3 below.
 *   <li><b>local element: </b> A JavaBean property that references a class annotated
 *       with @XmlType(name="") and is mapped to the element associated with the anonymous type. See
 *       Example 4 below.
 *   <li><b>attribute: </b> A JavaBean property that references a class annotated
 *       with @XmlType(name="") and is mapped to the attribute associated with the anonymous type.
 *       See Example 5 below.
 * </ul>
 *
 * <b> Mapping to XML Schema Complex Type </b>
 *
 * <ul>
 *   <li>If class is annotated with {@code @XmlType(name="") }, it is mapped to an anonymous type
 *       otherwise, the class name maps to a complex type name. The {@code XmlName()} annotation
 *       element can be used to customize the name.
 *   <li>Properties and fields that are mapped to elements are mapped to a content model within a
 *       complex type. The annotation element {@code propOrder()} can be used to customize the
 *       content model to be {@code xs:all} or {@code xs:sequence}. It is used for specifying the
 *       order of XML elements in {@code xs:sequence}.
 *   <li>Properties and fields can be mapped to attributes within the complex type.
 *   <li>The targetnamespace of the XML Schema type can be customized using the annotation element
 *       {@code namespace()}.
 * </ul>
 *
 * <p><b> Mapping class to XML Schema simple type </b>
 *
 * <p>A class can be mapped to a XML Schema simple type using the {@code @XmlValue} annotation. For
 * additional details and examples, see @{@link XmlValue} annotation type.
 *
 * <p>The following table shows the mapping of the class to a XML Schema complex type or simple
 * type. The notational symbols used in the table are:
 *
 * <ul>
 *   <li>{@literal ->} : represents a mapping
 *   <li>[x]+ : one or more occurrences of x
 *   <li>[ {@code @XmlValue} property ]: JavaBean property annotated with {@code @XmlValue}
 *   <li>X : don't care
 * </ul>
 *
 * <blockquote>
 *
 * <table class="striped">
 *     <caption>Mapping class to XML Schema simple type</caption>
 *     <thead>
 *       <tr>
 *         <th scope="col">Target</th>
 *         <th scope="col">propOrder</th>
 *         <th scope="col">ClassBody</th>
 *         <th scope="col">ComplexType</th>
 *         <th scope="col">SimpleType</th>
 *       </tr>
 *     </thead>
 *
 *     <tbody>
 *       <tr>
 *         <td>Class</td>
 *         <td>{}</td>
 *         <th scope="row">[property]+ {@literal ->} elements</th>
 *         <td>complexcontent<br>xs:all</td>
 *         <td> </td>
 *       </tr>
 *
 *       <tr>
 *         <td>Class</td>
 *         <td>non empty</td>
 *         <th scope="row">[property]+ {@literal ->} elements</th>
 *         <td>complexcontent<br>xs:sequence</td>
 *         <td> </td>
 *       </tr>
 *
 *       <tr>
 *         <td>Class</td>
 *         <td>X</td>
 *         <th scope="row">no property {@literal ->} element</th>
 *         <td>complexcontent<br>empty sequence</td>
 *         <td> </td>
 *       </tr>
 *
 *       <tr>
 *         <td>Class</td>
 *         <td>X</td>
 *         <th scope="row">1 [{@code @XmlValue} property] {@literal &&} <br> [property]+ {@literal ->} attributes</th>
 *         <td>simplecontent</td>
 *         <td> </td>
 *       </tr>
 *
 *       <tr>
 *         <td>Class</td>
 *         <td>X</td>
 *         <th scope="row">1 [{@code @XmlValue} property] {@literal &&} <br> no properties {@literal ->} attribute</th>
 *         <td> </td>
 *         <td>simpletype</td>
 *       </tr>
 *     </tbody>
 *   </table>
 *
 * </blockquote>
 *
 * <h3>Mapping an enum type </h3>
 *
 * An enum type maps to a XML schema simple type with enumeration facets. The following annotation
 * elements are ignored since they are not meaningful: {@code propOrder()} , {@code factoryMethod()}
 * , {@code factoryClass()} .
 *
 * <h3>Usage with other annotations </h3>
 *
 * <p>This annotation can be used with the following annotations: {@link XmlRootElement}, {@link
 * XmlAccessorOrder}, {@link XmlAccessorType}, {@link XmlEnum}. However, {@link XmlAccessorOrder}
 * and {@link XmlAccessorType} are ignored when this annotation is used on an enum type.
 *
 * <p><b> Example 1: </b> Map a class to a complex type with xs:sequence with a customized ordering
 * of JavaBean properties.
 *
 * <pre>
 *   &#64;XmlType(propOrder={"street", "city" , "state", "zip", "name" })
 *   public class USAddress {
 *     String getName() {..};
 *     void setName(String) {..};
 *
 *     String getStreet() {..};
 *     void setStreet(String) {..};
 *
 *     String getCity() {..};
 *     void setCity(String) {..};
 *
 *     String getState() {..};
 *     void setState(String) {..};
 *
 *     java.math.BigDecimal getZip() {..};
 *     void setZip(java.math.BigDecimal) {..};
 *   }
 * {@code
 *
 *   <!-- XML Schema mapping for USAddress -->
 *   <xs:complexType name="USAddress">
 *     <xs:sequence>
 *       <xs:element name="street" type="xs:string"/>
 *       <xs:element name="city" type="xs:string"/>
 *       <xs:element name="state" type="xs:string"/>
 *       <xs:element name="zip" type="xs:decimal"/>
 *       <xs:element name="name" type="xs:string"/>
 *     </xs:all>
 *   </xs:complexType>
 * }</pre>
 *
 * <p><b> Example 2: </b> Map a class to a complex type with xs:all
 *
 * <pre>
 * &#64;XmlType(propOrder={})
 * public class USAddress { ...}
 * {@code
 *
 * <!-- XML Schema mapping for USAddress -->
 * <xs:complexType name="USAddress">
 *   <xs:all>
 *     <xs:element name="name" type="xs:string"/>
 *     <xs:element name="street" type="xs:string"/>
 *     <xs:element name="city" type="xs:string"/>
 *     <xs:element name="state" type="xs:string"/>
 *     <xs:element name="zip" type="xs:decimal"/>
 *   </xs:sequence>
 * </xs:complexType>
 * }</pre>
 *
 * <p><b> Example 3: </b> Map a class to a global element with an anonymous type.
 *
 * <pre>
 *   &#64;XmlRootElement
 *   &#64;XmlType(name="")
 *   public class USAddress { ...}
 * {@code
 *
 *   <!-- XML Schema mapping for USAddress -->
 *   <xs:element name="USAddress">
 *     <xs:complexType>
 *       <xs:sequence>
 *         <xs:element name="name" type="xs:string"/>
 *         <xs:element name="street" type="xs:string"/>
 *         <xs:element name="city" type="xs:string"/>
 *         <xs:element name="state" type="xs:string"/>
 *         <xs:element name="zip" type="xs:decimal"/>
 *       </xs:sequence>
 *     </xs:complexType>
 *   </xs:element>
 * }</pre>
 *
 * <p><b> Example 4: </b> Map a property to a local element with anonymous type.
 *
 * <pre>
 *   //Example: Code fragment
 *   public class Invoice {
 *       USAddress addr;
 *           ...
 *       }
 *
 *   &#64;XmlType(name="")
 *   public class USAddress { ... }
 *   }
 * {@code
 *
 *   <!-- XML Schema mapping for USAddress -->
 *   <xs:complexType name="Invoice">
 *     <xs:sequence>
 *       <xs:element name="addr">
 *         <xs:complexType>
 *           <xs:element name="name", type="xs:string"/>
 *           <xs:element name="city", type="xs:string"/>
 *           <xs:element name="city" type="xs:string"/>
 *           <xs:element name="state" type="xs:string"/>
 *           <xs:element name="zip" type="xs:decimal"/>
 *         </xs:complexType>
 *       ...
 *     </xs:sequence>
 *   </xs:complexType>
 * }</pre>
 *
 * <p><b> Example 5: </b> Map a property to an attribute with anonymous type.
 *
 * <pre>
 *
 *     //Example: Code fragment
 *     public class Item {
 *         public String name;
 *         &#64;XmlAttribute
 *         public USPrice price;
 *     }
 *
 *     // map class to anonymous simple type.
 *     &#64;XmlType(name="")
 *     public class USPrice {
 *         &#64;XmlValue
 *         public java.math.BigDecimal price;
 *     }
 * {@code
 *
 *     <!-- Example: XML Schema fragment -->
 *     <xs:complexType name="Item">
 *       <xs:sequence>
 *         <xs:element name="name" type="xs:string"/>
 *         <xs:attribute name="price">
 *           <xs:simpleType>
 *             <xs:restriction base="xs:decimal"/>
 *           </xs:simpleType>
 *         </xs:attribute>
 *       </xs:sequence>
 *     </xs:complexType>
 * }</pre>
 *
 * <p><b> Example 6: </b> Define a factoryClass and factoryMethod
 *
 * <pre>
 *      &#64;XmlType(name="USAddressType", factoryClass=USAddressFactory.class,
 *      factoryMethod="getUSAddress")
 *      public class USAddress {
 *
 *          private String city;
 *          private String name;
 *          private String state;
 *          private String street;
 *          private int    zip;
 *
 *      public USAddress(String name, String street, String city,
 *          String state, int zip) {
 *          this.name = name;
 *          this.street = street;
 *          this.city = city;
 *          this.state = state;
 *          this.zip = zip;
 *      }
 *  }
 *
 *  public class USAddressFactory {
 *      public static USAddress getUSAddress(){
 *       return new USAddress("Mark Baker", "23 Elm St",
 *          "Dayton", "OH", 90952);
 *  }
 *
 * </pre>
 *
 * <p><b> Example 7: </b> Define factoryMethod and use the default factoryClass
 *
 * <pre>
 *      &#64;XmlType(name="USAddressType", factoryMethod="getNewInstance")
 *      public class USAddress {
 *
 *          private String city;
 *          private String name;
 *          private String state;
 *          private String street;
 *          private int    zip;
 *
 *          private USAddress() {}
 *
 *          public static USAddress getNewInstance(){
 *              return new USAddress();
 *          }
 *      }
 * </pre>
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @see XmlElement
 * @see XmlAttribute
 * @see XmlValue
 * @see XmlSchema
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlType {
  /** Name of the XML Schema type which the class is mapped. */
  String name() default "##default";

  /**
   * Specifies the order for XML Schema elements when class is mapped to a XML Schema complex type.
   *
   * <p>Refer to the table for how the propOrder affects the mapping of class
   *
   * <p>The propOrder is a list of names of JavaBean properties in the class. Each name in the list
   * is the name of a Java identifier of the JavaBean property. The order in which JavaBean
   * properties are listed is the order of XML Schema elements to which the JavaBean properties are
   * mapped.
   *
   * <p>All of the JavaBean properties being mapped to XML Schema elements must be listed.
   *
   * <p>A JavaBean property or field listed in propOrder must not be transient or annotated with
   * {@code @XmlTransient}.
   *
   * <p>The default ordering of JavaBean properties is determined by @{@link XmlAccessorOrder}.
   */
  String[] propOrder() default {""};

  /**
   * Name of the target namespace of the XML Schema type. By default, this is the target namespace
   * to which the package containing the class is mapped.
   */
  String namespace() default "##default";

  /**
   * Class containing a no-arg factory method for creating an instance of this class. The default is
   * this class.
   *
   * <p>If {@code factoryClass} is DEFAULT.class and {@code factoryMethod} is "", then there is no
   * static factory method.
   *
   * <p>If {@code factoryClass} is DEFAULT.class and {@code factoryMethod} is not "", then {@code
   * factoryMethod} is the name of a static factory method in this class.
   *
   * <p>If {@code factoryClass} is not DEFAULT.class, then {@code factoryMethod} must not be "" and
   * must be the name of a static factory method specified in {@code factoryClass}.
   */
  Class<?> factoryClass() default DEFAULT.class;

  /**
   * Used in {@link XmlType#factoryClass()} to signal that either factory mehod is not used or that
   * it's in the class with this {@link XmlType} itself.
   */
  final class DEFAULT {
    private DEFAULT() {}
  }

  /**
   * Name of a no-arg factory method in the class specified in {@code factoryClass} factoryClass().
   */
  String factoryMethod() default "";
}
