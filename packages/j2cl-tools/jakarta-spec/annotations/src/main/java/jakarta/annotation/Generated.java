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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>Generated</code> annotation is used to mark source code that has been generated. It can
 * also be used to differentiate user written code from generated code in a single file.
 *
 * <p>The <code>value</code> element must have the name of the code generator. The recommended
 * convention is to use the fully qualified name of the code generator in the value field, for
 * example <code>com.company.package.classname</code>.
 *
 * <p>The <code>date</code> element is used to indicate the date the source was generated. The
 * <code>date</code> element must follow the ISO 8601 standard. For example, the <code>date</code>
 * element could have the value <code>2001-07-04T12:08:56.235-0700</code>, which represents
 * 2001-07-04 12:08:56 local time in the U.S. Pacific time zone.
 *
 * <p>The <code>comment</code> element is a place holder for any comments that the code generator
 * may want to include in the generated code.
 *
 * @since 1.6, Common Annotations 1.0
 */
@Documented
@Retention(SOURCE)
@Target({PACKAGE, TYPE, ANNOTATION_TYPE, METHOD, CONSTRUCTOR, FIELD, LOCAL_VARIABLE, PARAMETER})
public @interface Generated {
  /**
   * The value element must have the name of the code generator. The recommended convention is to
   * use the fully qualified name of the code generator. For example: <code>
   * com.acme.generator.CodeGen</code>.
   */
  String[] value();

  /** Date when the source was generated. */
  String date() default "";

  /**
   * A place holder for any comments that the code generator may want to include in the generated
   * code.
   */
  String comments() default "";
}
