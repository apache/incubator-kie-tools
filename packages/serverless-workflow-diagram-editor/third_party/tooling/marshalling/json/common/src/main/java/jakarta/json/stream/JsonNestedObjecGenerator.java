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

package jakarta.json.stream;

import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.jre.JreJsonGenerator;

public class JsonNestedObjecGenerator extends JreJsonGenerator {

  private final JsonObjectBuilder parent;
  private final String name;

  public JsonNestedObjecGenerator(
      JsonObjectBuilder parent, JsonObjectBuilder builder, String name, SerializationContext ctx) {
    super(builder, ctx);
    this.parent = parent;
    this.name = name;
  }

  @Override
  public JsonGenerator writeEnd() {
    parent.add(name, builder);
    return this;
  }
}
