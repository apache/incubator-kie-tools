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

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.JsonValueDecorator;
import jakarta.json.bind.serializer.DeserializationContext;

public class BooleanJsonDeserializer extends JsonbDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
    if (json == null) {
      return null;
    }
    return new JsonValueDecorator(json).asBoolean();
  }
}
