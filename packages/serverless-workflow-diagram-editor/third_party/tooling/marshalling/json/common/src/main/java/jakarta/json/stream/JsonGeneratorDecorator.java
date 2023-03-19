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

import java.math.BigDecimal;
import java.math.BigInteger;

import elemental2.core.JsArray;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.JsonSerializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.gwt.GwtArrayJsonGenerator;

public class JsonGeneratorDecorator implements JsonGenerator {

  protected final JsonObjectBuilder builder;

  protected final JsonSerializationContext ctx;

  public JsonGeneratorDecorator(JsonObjectBuilder builder, SerializationContext ctx) {
    this.builder = builder;
    this.ctx = (JsonSerializationContext) ctx;
  }

  @Override
  public JsonGenerator writeStartObject() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeStartObject(String name) {
    JsonObjectBuilder objBuilder = ctx.createGenerator().builder();
    return new JsonNestedObjecGenerator(builder, objBuilder, name, ctx);
  }

  @Override
  public JsonGenerator writeKey(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeStartArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeStartArray(String name) {
    JsArray array = new JsArray();
    GwtArrayJsonGenerator generator = new GwtArrayJsonGenerator(array, ctx);
    builder.add(name, generator);
    return generator;
  }

  @Override
  public JsonGenerator write(String name, JsonValue value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, String value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, BigInteger value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, BigDecimal value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, int value) {
    builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Integer value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Character value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Float value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Short value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, long value) {
    builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Long value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, double value) {
    builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Double value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator write(String name, boolean value) {
    builder.add(name, value);
    return this;
  }

  public JsonGenerator write(String name, Boolean value) {
    if (value != null) builder.add(name, value);
    return this;
  }

  @Override
  public JsonGenerator writeNull(String name) {
    builder.addNull(name);
    return this;
  }

  @Override
  public JsonGenerator writeEnd() {
    builder.build();
    return this;
  }

  @Override
  public JsonGenerator write(JsonValue value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(BigDecimal value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(BigInteger value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(long value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(double value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeNull() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {}

  @Override
  public void flush() {}

  public JsonObjectBuilder builder() {
    return builder;
  }
}
