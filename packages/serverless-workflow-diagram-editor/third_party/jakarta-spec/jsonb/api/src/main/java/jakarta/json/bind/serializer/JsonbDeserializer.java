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
 * Interface representing a custom deserializer for a given type. It provides a low-level API for
 * java object deserialization from JSON stream using {@link JsonParser}. Unlike {@link
 * jakarta.json.bind.adapter.JsonbAdapter}, which acts more as converter from one java type to
 * another, deserializer provides more fine grained control over deserialization process.
 *
 * <p>{@link DeserializationContext} acts as JSONB runtime, able to deserialize any java object
 * provided.
 *
 * <p>Sample of custom Deserializer:
 *
 * <pre>
 *     class Box {
 *         public BoxInner boxInnerObject;
 *         public String name;
 *     }
 *
 *     BoxDeserializer implements JsonbDeserializer&lt;Box&gt; {
 *         public Box deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
 *             Box = new Box();
 *
 *             while (parser.hasNext()) {
 *                 Event event = parser.next();
 *
 *                 if (event == JsonParser.Event.KEY_NAME &amp;&amp; parser.getString().equals("boxInnerObject") {
 *                     // Deserialize inner object
 *                     box.boxInnerObject = ctx.deserialize(BoxInner.class, jsonParser);
 *
 *                 } else if (event == JsonParser.Event.KEY_NAME &amp;&amp; parser.getString().equals("name") {
 *                     // Deserialize name property
 *                     parser.next(); // move to VALUE
 *                     box.name = parser.getString();
 *                 }
 *             }
 *
 *             return box;
 *         }
 *     }
 * </pre>
 *
 * <p>Deserializers are registered using {@link
 * jakarta.json.bind.JsonbConfig#withDeserializers(JsonbDeserializer[])} method or using {@link
 * jakarta.json.bind.annotation.JsonbTypeDeserializer} annotation on type.
 *
 * @param <T> Type to bind deserializer for.
 * @see jakarta.json.bind.JsonbConfig
 * @see jakarta.json.bind.annotation.JsonbTypeDeserializer
 * @see JsonbSerializer
 * @see jakarta.json.bind.adapter.JsonbAdapter
 * @since JSON Binding 1.0
 */
public interface JsonbDeserializer<T> {

  /**
   * Deserialize JSON stream into object.
   *
   * @param parser Json parser.
   * @param ctx Deserialization context.
   * @param rtType Type of returned object.
   * @return Deserialized instance.
   */
  T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType);
}
