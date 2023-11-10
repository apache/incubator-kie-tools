/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.internal.serializer;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public class SerializerJsonbTypeSerializerWrapper<T> implements JsonbSerializer<T> {

  private final JsonbSerializer<T> serializer;
  private final String property;

  public SerializerJsonbTypeSerializerWrapper(JsonbSerializer<T> serializer, String property) {
    this.serializer = serializer;
    this.property = property;
  }

  @Override
  public void serialize(T obj, JsonGenerator generator, SerializationContext ctx) {
    if (obj instanceof Object[]) {
      JsonGenerator builder = generator.writeStartArray(property);
      serializer.serialize(obj, builder, ctx);
      builder.writeEnd();
    } else {
      JsonGenerator gen = generator.writeKey(property);
      serializer.serialize(obj, gen, ctx);
    }
  }
}
