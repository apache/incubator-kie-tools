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

package jakarta.json.bind.serializer;

import java.lang.reflect.Type;

import jakarta.json.stream.JsonParser;

/**
 * Provides JSONB Mapper functionality on top of JSONP parser.
 *
 * @see JsonbDeserializer
 * @since JSON Binding 1.0
 */
public interface DeserializationContext {

  /**
   * Deserialize JSON stream into instance of provided class using {@link
   * jakarta.json.stream.JsonParser}. JsonParser cursor have to be at KEY_NAME before START_OBJECT /
   * START_ARRAY, or at START_OBJECT / START_ARRAY to call this method. After deserialization is
   * complete JsonParser will be at END_OBJECT / END_ARRAY for deserialized JSON structure.
   *
   * <p>If method is called for the same type, which is deserializer bound to, deserializer
   * recursion is suppressed. Otherwise deserializers are reentrant during deserialization process
   * started by this method. {@link JsonParser} instance of JSONB runtime is shared with custom
   * deserializer.
   *
   * @param clazz Type to deserialize into. No arg constructor required.
   * @param parser JSONP parser to drive.
   * @param <T> Type of class.
   * @return Deserialized instance.
   */
  <T> T deserialize(Class<T> clazz, JsonParser parser);

  /**
   * Deserialize JSON stream into instance of provided class using {@link
   * jakarta.json.stream.JsonParser}. JsonParser cursor have to be at KEY_NAME before START_OBJECT /
   * START_ARRAY, or at START_OBJECT / START_ARRAY to call this method. After deserialization is
   * complete JsonParser will be at END_OBJECT / END_ARRAY for deserialized JSON structure.
   *
   * <p>If method is called for the same type, which is deserializer bound to, deserializer
   * recursion is suppressed. Otherwise deserializers are reentrant during deserialization process
   * started by this method. {@link JsonParser} instance of JSONB runtime is shared with custom
   * deserializer.
   *
   * @param type Type to deserialize into. No arg constructor required.
   * @param parser JSONP parser to drive.
   * @param <T> Type to deserialize into.
   * @return Deserialized instance.
   */
  <T> T deserialize(Type type, JsonParser parser);
}
