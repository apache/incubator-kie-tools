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

package org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParserImpl;

public class DeserializerJsonbTypeSerializerWrapper<T> extends JsonbDeserializer<T> {

  private final jakarta.json.bind.serializer.JsonbDeserializer<T> deserializer;
  private final Class clazz;

  public DeserializerJsonbTypeSerializerWrapper(
      jakarta.json.bind.serializer.JsonbDeserializer<T> deserializer, Class clazz) {
    this.deserializer = deserializer;
    this.clazz = clazz;
  }

  @Override
  public T deserialize(JsonValue value, DeserializationContext ctx) {
    return deserializer.deserialize(new JsonParserImpl(value), ctx, clazz);
  }
}
