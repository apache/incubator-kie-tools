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

import java.util.List;

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.DeserializationContext;
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.JsonbDeserializer;

public class ArrayJsonDeserializer<T> extends AbstractArrayJsonDeserializer<T[]> {

  private final JsonbDeserializer<T> deserializer;
  private final ArrayCreator<T> arrayCreator;

  public ArrayJsonDeserializer(JsonbDeserializer<T> deserializer, ArrayCreator<T> arrayCreator) {
    if (null == deserializer) {
      throw new IllegalArgumentException("deserializer cannot be null");
    }
    if (null == arrayCreator) {
      throw new IllegalArgumentException("Cannot deserialize an array without an arrayCreator");
    }
    this.deserializer = deserializer;
    this.arrayCreator = arrayCreator;
  }

  /** {@inheritDoc} */
  public T[] deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
    List<T> list = deserializeIntoList(json, deserializer, ctx);
    if (list == null) {
      return null;
    }
    return list.toArray(arrayCreator.create(list.size()));
  }

  @FunctionalInterface
  public interface ArrayCreator<T> {

    T[] create(int length);
  }
}
