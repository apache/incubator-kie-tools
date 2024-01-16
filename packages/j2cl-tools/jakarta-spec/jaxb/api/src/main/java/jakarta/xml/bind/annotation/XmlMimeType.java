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
 * Associates the MIME type that controls the XML representation of the property.
 *
 * <p>This annotation is used in conjunction with datatypes such as {@code java.awt.Image} or {@link
 * Source} that are bound to base64-encoded binary in XML.
 *
 * <p>If a property that has this annotation has a sibling property bound to the xmime:contentType
 * attribute, and if in the instance the property has a value, the value of the attribute takes
 * precedence and that will control the marshalling.
 *
 * @author Kohsuke Kawaguchi
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface XmlMimeType {
  /**
   * The textual representation of the MIME type, such as "image/jpeg" "image/*", "text/xml;
   * charset=iso-8859-1" and so on.
   */
  String value();
}
