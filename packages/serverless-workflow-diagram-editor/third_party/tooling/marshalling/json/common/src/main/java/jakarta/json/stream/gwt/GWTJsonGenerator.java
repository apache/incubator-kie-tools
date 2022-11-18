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

package jakarta.json.stream.gwt;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorDecorator;

public class GWTJsonGenerator extends JsonGeneratorDecorator {

  public GWTJsonGenerator(JsonObjectBuilder builder, SerializationContext ctx) {
    super(builder, ctx);
  }

  @Override
  public JsonGenerator write(String name, String value) {
    if (value != null) {
      builder.add(name, value);
    }
    return this;
  }

  @Override
  public JsonGenerator write(String name, BigInteger value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, BigDecimal value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, int value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, long value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, double value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, boolean value) {
    builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator writeNull(String name) {
    builder.addNull(name);
    return this;
  }

  public JsonObjectBuilder builder() {
    return builder;
  }
}
