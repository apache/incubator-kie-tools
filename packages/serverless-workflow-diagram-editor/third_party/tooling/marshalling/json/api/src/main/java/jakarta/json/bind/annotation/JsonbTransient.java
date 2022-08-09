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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;


/**
 * Prevents mapping of a Java Bean property, field or type to JSON representation.
 *
 * <p><b>Usage</b>
 *
 * <p>The {@code @JsonbTransient} annotation can be used with the following program elements:
 *
 * <ul>
 *   <li>getter/setter
 *   <li>field
 * </ul>
 *
 * <p>{@code @JsonbTransient} is mutually exclusive with all other JSON Binding defined annotations.
 *
 * <p>If a class field is annotated with {@code @JsonbTransient}, exception is thrown when this
 * field, getter or setter is annotated with other JSON Binding annotations.
 *
 * <p>If a getter is annotated with {@code @JsonbTransient}, exception is thrown if when the field
 * or this getter are annotated with other JSON Binding annotations. Exception is not thrown if JSON
 * Binding annotations are presented on the setter.
 *
 * <p>If a setter is annotated with {@code @JsonbTransient}, exception is thrown if when the field
 * or this setter are annotated with other JSON Binding annotations. Exception is not thrown if JSON
 * Binding annotations are presented on the getter.
 *
 * @since JSON Binding 1.0
 */
@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ANNOTATION_TYPE, FIELD, METHOD})
public @interface JsonbTransient {}
