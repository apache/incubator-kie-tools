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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Subtype is tightly bound to the {@link JsonbTypeInfo}. <br>
 * Type defines class which instance will be created when processing specific alias, or when
 * processing instance of the specified type, to determine which alias should be used. <br>
 * Alias is used instead of a class name. It has to be unique value among all the defined subtypes
 * bound to the specific {@link JsonbTypeInfo}. An exception should be thrown when processing and
 * validating aliases and duplicate alias is found.
 *
 * <pre><code>
 * // Example
 * {@literal @}JsonbTypeInfo({
 *      {@literal @}JsonbSubtype(alias = "dog", type = Dog.class)
 *      {@literal @}JsonbSubtype(alias = "cat", type = Cat.class)
 * })
 * interface Animal {}
 *
 * class Dog implements Animal {
 *     public String isDog = true;
 * }
 * class Cat implements Animal {
 *     public String isCat = true;
 * }
 * class Rat implements Animal {
 *     public String isRat = true;
 * }
 *
 * jsonb.toJson(new Dog());// {"@type":"dog","isDog":true}
 * jsonb.toJson(new Cat());// {"@type":"cat","isCat":true}
 * jsonb.toJson(new Rat());// {"isRat":true}
 * </code></pre>
 */
@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface JsonbSubtype {

  /**
   * Type alias which is used instead of a class name.
   *
   * @return alias value
   */
  String alias();

  /**
   * An actual type bound to the alias.
   *
   * @return alias bound type
   */
  Class<?> type();
}
