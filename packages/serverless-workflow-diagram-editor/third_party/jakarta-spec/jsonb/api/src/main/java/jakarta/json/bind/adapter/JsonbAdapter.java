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

package jakarta.json.bind.adapter;

/**
 * Allows to define custom mapping for given java type. The target type could be string or some
 * mappable java type.
 *
 * <p>On serialization "Original" type is converted into "Adapted" type. After that "Adapted" type
 * is serialized to JSON the standard way.
 *
 * <p>On deserialization it works the reverse way: JSON data are deserialized into "Adapted" type
 * which is converted to "Original" type after that.
 *
 * <p>Adapters are registered using {@link
 * jakarta.json.bind.JsonbConfig#withAdapters(JsonbAdapter[])} method or using {@link
 * jakarta.json.bind.annotation.JsonbTypeAdapter} annotation on class field.
 *
 * @param <Original> The type that JSONB doesn't know how to handle
 * @param <Adapted> The type that JSONB knows how to handle out of the box
 *     <p>Adapter runtime "Original" and "Adapted" generic types are inferred from subclassing
 *     information, which is mandatory for adapter to work.
 *     <p>Sample 1:
 *     <pre>{@code
 * // Generic information is provided by subclassing.
 * class BoxToCrateAdapter implements JsonbAdapter<Box<Integer>, Crate<String>> {...};
 * jsonbConfig.withAdapters(new BoxToCrateAdapter());
 *
 * // Generic information is provided by subclassing with anonymous class
 * jsonbConfig.withAdapters(new JsonbAdapter<Box<Integer>, Crate<String>> {...});
 * }</pre>
 *     <p>Sample 2:
 *     <pre>{@code
 * BoxToCrateAdapter<T> implements JsonbAdapter<Box<T>, Integer> {...};
 *
 * // Bad way: Generic type information is lost due to type erasure
 * jsonbConfig.withAdapters(new BoxToCrateAdapter<Integer>());
 *
 * // Proper way: Anonymous class holds generic type information
 * jsonbConfig.withAdapters(new BoxToCrateAdapter<Integer>(){});
 * }</pre>
 *
 * @see jakarta.json.bind.JsonbConfig
 * @see jakarta.json.bind.annotation.JsonbTypeAdapter
 * @since JSON Binding 1.0
 */
public interface JsonbAdapter<Original, Adapted> {

  /**
   * This method is used on serialization only. It contains a conversion logic from type Original to
   * type Adapted. After conversion Adapted type will be mapped to JSON the standard way.
   *
   * @param obj Object to convert or {@code null}.
   * @return Converted object which will be serialized to JSON or {@code null}.
   * @throws Exception if there is an error during the conversion.
   */
  Adapted adaptToJson(Original obj) throws Exception;

  /**
   * This method is used on deserialization only. It contains a conversion logic from type Adapted
   * to type Original.
   *
   * @param obj Object to convert or {@code null}.
   * @return Converted object representing pojo to be set into object graph or {@code null}.
   * @throws Exception if there is an error during the conversion.
   */
  Original adaptFromJson(Adapted obj) throws Exception;
}
