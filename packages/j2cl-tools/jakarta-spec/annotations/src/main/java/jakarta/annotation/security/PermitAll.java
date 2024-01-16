/*
 * Copyright (c) 2005, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jakarta.annotation.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that all security roles are allowed to invoke the specified method(s) &#8212; i.e.,
 * that the specified method(s) are "unchecked". It can be specified on a class or on methods.
 * Specifying it on the class means that it applies to all methods of the class. If specified at the
 * method level, it only affects that method. If the <code>RolesAllowed</code> annotation is
 * specified at the class level and this annotation is applied at the method level, the <code>
 * PermitAll</code> annotation overrides the <code>RolesAllowed</code> annotation for the specified
 * method.
 *
 * @see jakarta.annotation.security.RolesAllowed
 * @see jakarta.annotation.security.DenyAll
 * @since Common Annotations 1.0
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface PermitAll {}
