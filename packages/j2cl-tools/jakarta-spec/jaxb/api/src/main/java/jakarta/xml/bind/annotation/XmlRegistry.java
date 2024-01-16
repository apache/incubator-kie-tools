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
 * Marks a class that has {@link XmlElementDecl}s.
 *
 * @author
 *     <ul>
 *       <li>Kohsuke Kawaguchi, Sun Microsystems, Inc.
 *       <li>Sekhar Vajjhala, Sun Microsystems, Inc.
 *     </ul>
 *
 * @since 1.6, JAXB 2.0
 * @see XmlElementDecl
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlRegistry {}
