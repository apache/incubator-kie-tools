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

import java.util.function.Function;

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;

public class EnumJsonDeserializer<E extends Enum<E>> extends JsonbDeserializer<E> {

  private final Function<String, E> func;

  private final Class<E> enumClass;

  private final StringJsonDeserializer stringJsonDeserializer = new StringJsonDeserializer();

  public EnumJsonDeserializer(Class<E> enumClass, Function<String, E> func) {
    this.enumClass = enumClass;
    this.func = func;
  }

  @Override
  public E deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
    String asString = stringJsonDeserializer.deserialize(json, ctx);
    return getEnum(asString);
  }

  public <E extends Enum<E>> E getEnum(String name) {
    E result = (E) func.apply(name);
    if (result != null) {
      return result;
    }
    throw new IllegalArgumentException(
        "[" + name + "] is not a valid enum constant for Enum type " + enumClass.getName());
  }
}
