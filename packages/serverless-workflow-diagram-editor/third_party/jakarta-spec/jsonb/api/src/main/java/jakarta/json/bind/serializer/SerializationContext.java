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

import jakarta.json.stream.JsonGenerator;

/**
 * Provides JSONB internals for custom serializers.
 *
 * @see JsonbSerializer
 * @since JSON Binding 1.0
 */
public interface SerializationContext {

  /**
   * Serializes arbitrary object to JSON, using current {@link jakarta.json.stream.JsonGenerator}
   * instance. Serialization is ran as serialization of a root type from user {@link
   * JsonbSerializer}. {@link JsonGenerator} instance is shared with JSONB and user serializer.
   *
   * @param key JSON key name.
   * @param object Object to serialize.
   * @param generator JSONP generator to serialize with.
   * @param <T> Type of serialized object.
   */
  <T> void serialize(String key, T object, JsonGenerator generator);

  /**
   * Serializes arbitrary object to JSON, using current {@link jakarta.json.stream.JsonGenerator}
   * instance. Serialization is ran as serialization of a root type from user {@link
   * JsonbSerializer}. {@link JsonGenerator} instance is shared with JSONB and user serializer.
   *
   * <p>Method without key parameter is intended to serialize inside JSON_ARRAYs.
   *
   * @param object Object to serialize.
   * @param generator JSONP generator to serialize with.
   * @param <T> Type of serialized object.
   */
  <T> void serialize(T object, JsonGenerator generator);
}
