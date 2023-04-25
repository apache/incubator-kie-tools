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

import jakarta.json.JsonValue;

public class ContextedJsonGenerator extends JsonGeneratorDecorator {

  private final String name;
  private final JsonGenerator generator;

  public ContextedJsonGenerator(String name, JsonGenerator generator) {
    super(null, null);
    this.name = name;
    this.generator = generator;
  }

  @Override
  public JsonGenerator writeStartObject() {
    return generator.writeStartObject(name);
  }

  @Override
  public JsonGenerator writeStartObject(String name) {
    return generator.writeStartObject(name);
  }

  @Override
  public JsonGenerator writeKey(String name) {
    return generator.writeKey(name);
  }

  @Override
  public JsonGenerator writeStartArray() {
    return generator.writeStartObject(name);
  }

  @Override
  public JsonGenerator writeStartArray(String name) {
    return generator.writeStartArray(name);
  }

  @Override
  public JsonGenerator write(String name, JsonValue value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, String value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, BigInteger value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, BigDecimal value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, int value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, long value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, double value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator write(String name, boolean value) {
    return generator.write(name, value);
  }

  @Override
  public JsonGenerator writeNull(String name) {
    return generator.writeNull(name);
  }

  @Override
  public JsonGenerator writeEnd() {
    return generator.writeEnd();
  }

  @Override
  public JsonGenerator write(JsonValue value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(String value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(BigDecimal value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(BigInteger value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(int value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(long value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(double value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator write(boolean value) {
    return write(name, value);
  }

  @Override
  public JsonGenerator writeNull() {
    return writeNull(name);
  }

  @Override
  public void close() {
    generator.close();
  }

  @Override
  public void flush() {
    generator.flush();
  }
}
