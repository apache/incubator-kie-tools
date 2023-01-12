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

import java.util.HashMap;
import java.util.Map;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.Pair;


public class JsonbSubtypeDeserializer<T> extends JsonbDeserializer<T> {

  private Map<String, JsonbDeserializer> types = new HashMap<>();
  private final String typeFieldName;

  public JsonbSubtypeDeserializer(String typeFieldName, Pair<String, JsonbDeserializer>... pairs) {
    this.typeFieldName = typeFieldName;
    for (Pair<String, JsonbDeserializer> pair : pairs) {
      types.put(pair.k, pair.v);
    }
  }

  @Override
  public T deserialize(JsonValue value, DeserializationContext ctx) {
    JsonObject valueHolder =
            (value instanceof JsonObject) ? ((JsonObject) value) : value.asJsonObject();
    if (valueHolder.containsKey(typeFieldName)) {
      if (types.containsKey(valueHolder.getString(typeFieldName))) {
        return (T) types.get(valueHolder.getString(typeFieldName)).deserialize(value, ctx);
      }
    }
    throw new Error("Unknown type " + value);
  }
}
