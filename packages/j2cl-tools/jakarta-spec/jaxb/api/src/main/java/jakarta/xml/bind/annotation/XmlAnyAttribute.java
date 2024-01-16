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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Maps a JavaBean property to a map of wildcard attributes.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @XmlAnyAttribute} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>JavaBean property
 *   <li>non static, non transient field
 * </ul>
 *
 * <p>See "Package Specification" in jakarta.xml.bind.package javadoc for additional common
 * information. The usage is subject to the following constraints:
 *
 * <ul>
 *   <li>At most one field or property in a class can be annotated with {@code @XmlAnyAttribute}.
 *   <li>The type of the property or the field must {@code java.util.Map}
 * </ul>
 *
 * <p>While processing attributes to be unmarshalled into a value class, each attribute that is not
 * statically associated with another JavaBean property, via {@link XmlAttribute}, is entered into
 * the wildcard attribute map represented by {@link Map}&lt;{@link QName},{@link Object}&gt;. The
 * attribute QName is the map's key. The key's value is the String value of the attribute.
 *
 * @author Kohsuke Kawaguchi, Sun Microsystems, Inc.
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlAnyAttribute {}
