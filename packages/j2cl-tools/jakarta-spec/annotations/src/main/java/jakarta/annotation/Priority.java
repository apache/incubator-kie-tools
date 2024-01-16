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

package jakarta.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

/**
 * The <code>Priority</code> annotation can be applied to any program elements to indicate in what
 * order they should be used. The effect of using the <code>Priority</code> annotation in any
 * particular instance is defined by other specifications that define the use of a specific class.
 *
 * <p>For example, the Jakarta Interceptors specification defines the use of priorities on
 * interceptors to control the order in which interceptors are called.
 *
 * <p>Priority values should generally be non-negative, with negative values reserved for special
 * meanings such as "undefined" or "not specified". A specification that defines use of the <code>
 * Priority</code> annotation may define the range of allowed priorities and any priority values
 * with special meaning.
 *
 * @since Common Annotations 1.2
 */
@Retention(RUNTIME)
@Documented
public @interface Priority {
  /** The priority value. */
  int value();
}
