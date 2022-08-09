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

public class PrimitiveByteArrayJsonDeserializer extends AbstractArrayJsonDeserializer<byte[]> {

  private final BaseNumberJsonDeserializer.ByteJsonDeserializer deser = new BaseNumberJsonDeserializer.ByteJsonDeserializer();

  @Override
  public byte[] deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
    List<Byte> list = deserializeIntoList(json, deser, ctx);

    byte[] result = new byte[list.size()];
    int i = 0;
    for (Byte value : list) {
      if (null != value) {
        result[i] = value;
      }
      i++;
    }
    return result;
  }
}
