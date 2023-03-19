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

import elemental2.core.JsArray;
import elemental2.core.JsNumber;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorDecorator;

public class GwtArrayJsonGenerator extends JsonGeneratorDecorator implements JsonArrayBuilder {

  private final JsArray array;

  public GwtArrayJsonGenerator(JsArray array, SerializationContext ctx) {
    super(null, ctx);
    this.array = array;
  }

  @Override
  public JsonGenerator writeStartObject() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeStartObject(String name) {
    throw new UnsupportedOperationException();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, JsonValue value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, BigInteger value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, BigDecimal value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, long value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, double value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator write(String name, boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeNull(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonGenerator writeEnd() {
    return this;
  }

  @Override
  public JsonGenerator write(JsonValue value) {
    array.push(value.asJsonObject());
    return this;
  }

  @Override
  public JsonGenerator write(String value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonGenerator write(BigDecimal value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonGenerator write(BigInteger value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonGenerator write(int value) {
    array.push(new JsNumber(value));
    return this;
  }

  @Override
  public JsonGenerator write(long value) {
    array.push(new JsNumber(value));
    return this;
  }

  @Override
  public JsonGenerator write(double value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonGenerator write(boolean value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonGenerator writeNull() {
    return null;
  }

  @Override
  public void close() {}

  @Override
  public void flush() {}

  @Override
  public JsonArrayBuilder add(JsonValue value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(String value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(BigDecimal value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(BigInteger value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(int value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(long value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(double value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder add(boolean value) {
    array.push(value);
    return this;
  }

  @Override
  public JsonArrayBuilder addNull() {
    return null;
  }

  @Override
  public JsonArrayBuilder add(JsonObjectBuilder builder) {
    array.push(builder.build());
    return this;
  }

  @Override
  public JsonArrayBuilder add(JsonArrayBuilder builder) {
    array.push(((GwtArrayJsonGenerator) builder).array);
    return this;
  }

  @Override
  public JsonArray build() {
    return new JsonArrayImpl(array);
  }
}
