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

package org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.array;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;

public abstract class AbstractArrayJsonDeserializer<T> extends JsonbDeserializer<T> {

  protected <T> List<T> deserializeIntoList(
      JsonValue json, JsonbDeserializer<T> internalDeser, DeserializationContext ctx) {
    if (json == null) {
      return null;
    }
    List<T> collection = new ArrayList<>();
    JsonArray array = json.asJsonArray();

    if (array.isEmpty()) {
      return collection;
    }

    for (int i = 0; i < array.size(); i++) {
      collection.add(internalDeser.deserialize(array.get(i), ctx));
    }
    return collection;
  }

  @Override
  public T deserialize(JsonValue value, DeserializationContext ctx) {
    throw new UnsupportedOperationException("41");
  }
}
