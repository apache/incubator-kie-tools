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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Associates a namespace prefix with a XML namespace URI.
 *
 * <p><b>Usage</b>
 *
 * <p>{@code @XmlNs} annotation is intended for use from other program annotations.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * <p><b>Example:</b>See {@code XmlSchema} annotation type for an example.
 *
 * @author Sekhar Vajjhala, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({})
public @interface XmlNs {
  /** Namespace prefix */
  String prefix();

  /** Namespace URI */
  String namespaceURI();
}
