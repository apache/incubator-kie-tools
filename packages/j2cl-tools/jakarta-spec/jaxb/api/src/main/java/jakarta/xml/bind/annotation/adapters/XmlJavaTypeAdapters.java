/*
 * Copyright (c) 2004, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.xml.bind.annotation.adapters;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A container for multiple @{@link XmlJavaTypeAdapter} annotations.
 *
 * <p>Multiple annotations of the same type are not allowed on a program element. This annotation
 * therefore serves as a container annotation for multiple &#64;XmlJavaTypeAdapter as follows:
 *
 * <pre>
 * &#64;XmlJavaTypeAdapters ({ @XmlJavaTypeAdapter(...),@XmlJavaTypeAdapter(...) })
 * </pre>
 *
 * <p>The {@code @XmlJavaTypeAdapters} annotation is useful for defining {@link XmlJavaTypeAdapter}
 * annotations for different types at the package level.
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information.
 *
 * @author
 *     <ul>
 *       <li>Sekhar Vajjhala, Sun Microsystems, Inc.
 *     </ul>
 *
 * @see XmlJavaTypeAdapter
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({PACKAGE})
public @interface XmlJavaTypeAdapters {
  /** Collection of @{@link XmlJavaTypeAdapter} annotations */
  XmlJavaTypeAdapter[] value();
}
