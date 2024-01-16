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

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a property that refers to classes with {@link XmlElement} or JAXBElement.
 *
 * <p>Compared to an element property (property with {@link XmlElement} annotation), a reference
 * property has a different substitution semantics. When a sub-class is assigned to a property, an
 * element property produces the same tag name with @xsi:type, whereas a reference property produces
 * a different tag name (the tag name that's on the the sub-class.)
 *
 * <p>This annotation can be used with the following annotations: {@link XmlJavaTypeAdapter}, {@link
 * XmlElementWrapper}.
 *
 * @author
 *     <ul>
 *       <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.
 *       <li>Sekhar Vajjhala, Sun Microsystems, Inc.
 *     </ul>
 *
 * @see XmlElementWrapper
 * @see XmlElementRef
 * @since 1.6, JAXB 2.0
 */
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface XmlElementRefs {
  XmlElementRef[] value();
}
