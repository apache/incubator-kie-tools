/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer;

import java.lang.reflect.Type;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

public abstract class JsonbDeserializer<T>
    implements jakarta.json.bind.serializer.JsonbDeserializer<T> {

  public abstract T deserialize(JsonValue value, DeserializationContext ctx);

  @Override
  public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
    throw new UnsupportedOperationException("40");
  }
}
