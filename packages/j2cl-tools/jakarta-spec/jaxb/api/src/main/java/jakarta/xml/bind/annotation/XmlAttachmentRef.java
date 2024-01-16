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
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field/property that its XML form is a uri reference to mime content. The mime content is
 * optimally stored out-of-line as an attachment.
 *
 * <p>A field/property must always map to the {@link DataHandler} class.
 *
 * <h2>Usage</h2>
 *
 * <pre>
 * &#64;{@link XmlRootElement}
 * class Foo {
 *   &#64;{@link XmlAttachmentRef}
 *   &#64;{@link XmlAttribute}
 *   {@link DataHandler} data;
 *
 *   &#64;{@link XmlAttachmentRef}
 *   &#64;{@link XmlElement}
 *   {@link DataHandler} body;
 * }
 * </pre>
 *
 * The above code maps to the following XML:
 *
 * <pre>{@code
 * <xs:element name="foo" xmlns:ref="http://ws-i.org/profiles/basic/1.1/xsd">
 *   <xs:complexType>
 *     <xs:sequence>
 *       <xs:element name="body" type="ref:swaRef" minOccurs="0" />
 *     </xs:sequence>
 *     <xs:attribute name="data" type="ref:swaRef" use="optional" />
 *   </xs:complexType>
 * </xs:element>
 * }</pre>
 *
 * <p>The above binding supports WS-I AP 1.0 <a
 * href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html#Referencing_Attachments_from_the_SOAP_Envelope">WS-I
 * Attachments Profile Version 1.0.</a>
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface XmlAttachmentRef {}
