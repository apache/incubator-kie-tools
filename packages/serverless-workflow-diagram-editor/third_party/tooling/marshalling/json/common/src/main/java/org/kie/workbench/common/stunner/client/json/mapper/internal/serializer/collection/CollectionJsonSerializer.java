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

package org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.collection;

import java.util.Collection;

import jakarta.json.bind.serializer.JsonSerializationContext;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorDecorator;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.JsonSerializer;
import org.kie.workbench.common.stunner.client.json.mapper.internal.serializer.JsonSerializerAdapter;

public class CollectionJsonSerializer<T> extends JsonSerializer<Collection<T>> {

  protected final JsonSerializer<T> serializer;

  public CollectionJsonSerializer(JsonSerializer<T> serializer) {
    this.serializer = serializer;
  }

  public CollectionJsonSerializer(JsonbSerializer<T> serializer) {
    this(new JsonSerializerAdapter<>(serializer));
  }

  @Override
  public void serialize(
          Collection<T> collection,
          String property,
          JsonGenerator generator,
          SerializationContext ctx) {
    if (collection != null && !collection.isEmpty()) {
      JsonGenerator builder = generator.writeStartArray(property);
      JsonSerializationContext jsonSerializationContext = (JsonSerializationContext) ctx;

      for (T t : collection) {
        JsonGeneratorDecorator arrayElmBuilder = jsonSerializationContext.createGenerator();
        serializer.serialize(t, arrayElmBuilder, ctx);
        builder.write(arrayElmBuilder.builder().build());
      }
      builder.writeEnd();
    }
  }

  @Override
  public void serialize(Collection<T> obj, JsonGenerator generator, SerializationContext ctx) {
    throw new UnsupportedOperationException("62");
  }
}
