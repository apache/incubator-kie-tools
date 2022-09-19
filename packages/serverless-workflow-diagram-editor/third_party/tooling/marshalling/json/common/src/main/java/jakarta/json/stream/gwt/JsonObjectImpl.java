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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.Reflect;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonNumberImpl;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStringImpl;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jsinterop.base.Js;


public class JsonObjectImpl implements JsonObject {

  public Object __holder__; // TODO make it private

  public JsonObjectImpl(String json) {
    this(Global.JSON.parse(json));
  }

  public JsonObjectImpl(Object holder) {
    this.__holder__ = holder;
  }

  @Override
  public JsonArray getJsonArray(String name) {
    JsArray array = Js.uncheckedCast(Reflect.get(__holder__, name));
    return new JsonArrayImpl(array);
  }

  @Override
  public JsonObject getJsonObject(String name) {
    if (Reflect.has(__holder__, name)) {
      return new JsonObjectImpl(Reflect.get(__holder__, name));
    }
    return null;
  }

  @Override
  public JsonNumber getJsonNumber(String name) {
    return new JsonNumberImpl(Js.asPropertyMap(__holder__).get(name));
  }

  @Override
  public JsonString getJsonString(String name) {
    return new JsonStringImpl(Js.asPropertyMap(__holder__).get(name).toString());
  }

  @Override
  public String getString(String name) {
    return Js.asPropertyMap(__holder__).get(name).toString();
  }

  @Override
  public String getString(String name, String defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt(String name) {
    return Integer.valueOf(Js.asPropertyMap(__holder__).get(name).toString());
  }

  @Override
  public int getInt(String name, int defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean getBoolean(String name) {
    return Boolean.valueOf(Js.asPropertyMap(__holder__).get(name).toString());
  }

  @Override
  public boolean getBoolean(String name, boolean defaultValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isNull(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ValueType getValueType() {
    if (JsArray.isArray(__holder__)) {
      return ValueType.ARRAY;
    }
    String type = Js.typeof(__holder__).toLowerCase(Locale.ROOT);
    if (type.equals("number")) {
      return ValueType.NUMBER;
    } else if (type.equals("string")) {
      return ValueType.STRING;
    } else if (type.equals("boolean")) {
      if (__holder__.toString().toLowerCase(Locale.ROOT).equals("true")) {
        return ValueType.TRUE;
      } else {
        return ValueType.FALSE;
      }
    } else if (type.equals("null")) {
      return ValueType.NULL;
    } else if (type.equals("object")) {
      return ValueType.OBJECT;
    }

    throw new IllegalStateException("Unknown type: " + type);
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return Global.JSON.stringify(__holder__).equals("{}");
  }

  @Override
  public boolean containsKey(Object key) {
    return Js.asPropertyMap(__holder__).has(key.toString());
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonValue get(Object key) {
    return new JsonObjectImpl(Js.asPropertyMap(__holder__).get(key.toString()));
  }

  @Override
  public JsonValue put(String key, JsonValue value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonValue remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map<? extends String, ? extends JsonValue> m) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    return Reflect.ownKeys(__holder__).asList().stream().map(Reflect.OwnKeysArrayUnionType::asString).collect(Collectors.toSet());
  }

  @Override
  public Collection<JsonValue> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<String, JsonValue>> entrySet() {
    throw new UnsupportedOperationException();
  }

  public Double asBoxedDouble() {
    return Js.uncheckedCast(__holder__);
  }

  @Override
  public JsonObject asJsonObject() {
    return Js.uncheckedCast(__holder__);
  }

  @Override
  public JsonArray asJsonArray() {
    if (getValueType() == ValueType.ARRAY) {
      return new JsonArrayImpl(Js.uncheckedCast(__holder__));
    }
    throw new JsonbException("JsonValue is not an array");
  }

  @Override
  public String toString() {
    return Global.JSON.stringify(__holder__);
  }
}
