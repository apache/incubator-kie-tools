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

package org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.array;

import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.JsonSerializer;

public class ArrayJsonSerializer<T> extends BasicArrayJsonSerializer<T[]> {

  protected final JsonSerializer<T> serializer;

  public ArrayJsonSerializer(JsonSerializer<T> serializer) {
    this.serializer = serializer;
  }

  @Override
  public void serialize(
      T[] obj, String property, JsonGenerator generator, SerializationContext ctx) {
    if (obj != null) {
      JsonGenerator builder = generator.writeStartArray(property);
      for (int i = 0; i < obj.length; i++) {
        serializer.serialize(obj[i], builder, ctx);
      }
      builder.writeEnd();
    }
  }

  @Override
  public void serialize(T[] obj, JsonGenerator generator, SerializationContext ctx) {
    throw new UnsupportedOperationException("51");
  }
}
