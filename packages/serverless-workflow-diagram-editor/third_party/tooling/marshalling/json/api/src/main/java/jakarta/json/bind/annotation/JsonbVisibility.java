/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

package jakarta.json.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.json.bind.config.PropertyVisibilityStrategy;

/**
 * Annotation provides way how to customize visibility strategy of the JSON Binding.
 *
 * <p>It allows for example to specify, that only public getters and setter should be visible.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @JsonbVisibility} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>type
 *   <li>package
 * </ul>
 *
 * @since JSON Binding 1.0
 */
@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.PACKAGE})
public @interface JsonbVisibility {

  /**
   * Custom property visibility strategy used to resolve visibility of the members.
   *
   * @return Visibility strategy to use.
   */
  Class<? extends PropertyVisibilityStrategy> value();
}
