/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
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
 * Annotation provides way how to set custom number format to field or JavaBean property.
 *
 * <p>The pattern format is specified in {@link java.text.DecimalFormat}
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @JsonbNumberFormat} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>field
 *   <li>getter/setter
 *   <li>type
 *   <li>parameter
 *   <li>package
 * </ul>
 *
 * @since JSON Binding 1.0
 */
@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.ANNOTATION_TYPE,
  ElementType.FIELD,
  ElementType.METHOD,
  ElementType.TYPE,
  ElementType.PARAMETER,
  ElementType.PACKAGE
})
public @interface JsonbNumberFormat {

  /** Value that indicates that default {@link java.util.Locale}. */
  String DEFAULT_LOCALE = "##default";

  /**
   * Specifies the number pattern to use.
   *
   * @return Number pattern to use.
   */
  String value() default "";

  /**
   * Custom {@link java.util.Locale} to use.
   *
   * @return Custom locale to use.
   */
  String locale() default DEFAULT_LOCALE;
}
