/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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

/**
 * Configuration annotation of the type information handling. <br>
 * This annotation is required on the most common parent of all classes when type information will
 * be applied.
 *
 * <pre><code>
 * // Example
 * {@literal @}JsonbTypeInfo(key = "@key")
 * interface Animal {}
 *
 * class Dog implements Animal {}
 * class Cat implements Animal {}
 * </code></pre>
 *
 * This annotation is tightly bound to {@link JsonbSubtype}. It is required to use {@link
 * JsonbSubtype} annotations to specify all the possible classes and their aliases.
 */
@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface JsonbTypeInfo {

  /** Default type information key name. */
  String DEFAULT_KEY_NAME = "@type";

  /**
   * Key used for keeping the type information (alias). Default value is {@code @type}.
   *
   * @return key name
   */
  String key() default DEFAULT_KEY_NAME;

  /**
   * Allowed aliases of the handled type.
   *
   * @return list of allowed aliases
   */
  JsonbSubtype[] value() default {};
}
