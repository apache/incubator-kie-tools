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

package jakarta.json.bind.serializer;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorDecorator;
import jakarta.json.stream.gwt.GWTJsonGenerator;
import jakarta.json.stream.gwt.JsonObjectBuilderImpl;
import jakarta.json.stream.jre.JreJsonGenerator;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.GwtIncompatible;

public class JsonSerializationContext implements SerializationContext {

  @Override
  public <T> void serialize(String key, T object, JsonGenerator generator) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> void serialize(T object, JsonGenerator generator) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public JsonGeneratorDecorator createGenerator() {
    return new JREGenerator().createGenerator(this);
  }

  private static class GWTGenerator {

    public JsonGeneratorDecorator createGenerator(SerializationContext ctx) {
      JsonObjectBuilder builder = new JsonObjectBuilderImpl();
      JsonGeneratorDecorator generator = new GWTJsonGenerator(builder, ctx);
      return generator;
    }
  }

  private static class JREGenerator extends GWTGenerator {

    @GwtIncompatible
    @Override
    public JsonGeneratorDecorator createGenerator(SerializationContext ctx) {
      JsonObjectBuilder builder = Json.createObjectBuilder();
      JsonGeneratorDecorator generator = new JreJsonGenerator(builder, ctx);
      return generator;
    }
  }
}
