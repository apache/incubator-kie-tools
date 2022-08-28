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
import java.util.HashMap;
import java.util.Map;

import jakarta.json.JsonObject;
import jakarta.json.JsonObjectDecorator;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonbPropertyDeserializer;

public abstract class AbstractBeanJsonDeserializer<T> extends JsonbDeserializer<T>
    implements jakarta.json.bind.serializer.JsonbDeserializer<T> {

  protected Map<String, JsonbPropertyDeserializer<T>> properties = new HashMap();

  @Override
  public T deserialize(JsonValue value, DeserializationContext ctx) {
    if (value instanceof JsonObject) {
      return deserialize((JsonObject) value, ctx);
    }
    return deserialize(value.asJsonObject(), ctx);
  }

  @Override
  public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
    JsonParser.Event event = parser.next();
    if (event == JsonParser.Event.START_OBJECT) {
      return deserialize(parser.getObject(), ctx);
    }
    return null;
  }

  public T deserialize(JsonObject jsonObject, DeserializationContext ctx) {
    if (jsonObject == null) {
      return null;
    }
    T instance = newInstance();
    if (!jsonObject.isEmpty()) {
      JsonObjectDecorator jsonObjectDecorator = new JsonObjectDecorator(jsonObject);
      properties.forEach(
          (key, value) -> {
            if (jsonObjectDecorator.containsKey(key)) {
              value.accept(instance, jsonObjectDecorator, ctx);
            }
          });
    }
    return instance;
  }

  protected abstract T newInstance();
}
