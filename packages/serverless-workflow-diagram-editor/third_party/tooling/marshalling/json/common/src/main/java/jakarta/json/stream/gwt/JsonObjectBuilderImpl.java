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

import elemental2.core.JsNumber;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jsinterop.base.JsPropertyMap;

public class JsonObjectBuilderImpl implements JsonObjectBuilder {

  private final JsPropertyMap object = JsPropertyMap.of();

  public JsonObjectBuilderImpl() {}

  @Override
  public JsonObjectBuilder add(String name, JsonValue value) {
    if (value instanceof JsonArrayImpl) {
      object.set(name, ((JsonArrayImpl) value).array);
    } else {
      object.set(name, value);
    }
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, String value) {
    if (value != null) {
      object.set(name, value);
    }
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, BigInteger value) {
    if (value != null) {
      object.set(name, value.toString());
    }
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, BigDecimal value) {
    if (value != null) {
      object.set(name, value.toString());
    }
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, int value) {
    object.set(name, value);
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, long value) {
    object.set(name, new JsNumber(value));
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, double value) {
    object.set(name, value);
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, boolean value) {
    object.set(name, value);
    return this;
  }

  @Override
  public JsonObjectBuilder addNull(String name) {
    object.set(name, null);
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, JsonObjectBuilder builder) {
    object.set(name, ((JsonObjectBuilderImpl) builder).object);
    return this;
  }

  @Override
  public JsonObjectBuilder add(String name, JsonArrayBuilder builder) {
    JsonArrayImpl impl = (JsonArrayImpl) builder.build();
    object.set(name, impl.array);
    return this;
  }

  @Override
  public JsonObject build() {
    return new JsonObjectImpl(object);
  }
}
