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
import org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer.BaseNumberJsonDeserializer;

public class PrimitiveLongArrayJsonDeserializer extends AbstractArrayJsonDeserializer<long[]> {

  private final BaseNumberJsonDeserializer.LongJsonDeserializer deser =
      new BaseNumberJsonDeserializer.LongJsonDeserializer();

  @Override
  public long[] deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
    List<Long> list = deserializeIntoList(json, deser, ctx);

    long[] result = new long[list.size()];
    int i = 0;
    for (Long value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
