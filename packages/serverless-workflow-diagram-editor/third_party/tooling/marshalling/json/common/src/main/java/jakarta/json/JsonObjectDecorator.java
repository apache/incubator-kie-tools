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

package jakarta.json;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class JsonObjectDecorator implements JsonObject {

  public JsonObject holder;

  public JsonObjectDecorator(JsonObject holder) {
    this.holder = holder;
  }

  @Override
  public JsonArray getJsonArray(String name) {
    return holder.getJsonArray(name);
  }

  @Override
  public JsonObject getJsonObject(String name) {
    return holder.getJsonObject(name);
  }

  @Override
  public JsonNumber getJsonNumber(String name) {

    return holder.getJsonNumber(name);
  }

  @Override
  public JsonString getJsonString(String name) {
    return holder.getJsonString(name);
  }

  @Override
  public String getString(String name) {
    return holder.getString(name);
  }

  @Override
  public String getString(String name, String defaultValue) {
    return holder.getString(name, defaultValue);
  }

  @Override
  public int getInt(String name) {
    if (holder.containsKey(name)) {
      return holder.getInt(name);
    } else {
      return 0;
    }
  }

  public Integer getInteger(String name) {
    if (holder.containsKey(name)) {
      return holder.getInt(name);
    }
    return null;
  }

  @Override
  public int getInt(String name, int defaultValue) {
    return holder.getInt(name, defaultValue);
  }

  @Override
  public boolean getBoolean(String name) {
    if (holder.containsKey(name)) {
      return holder.getBoolean(name);
    } else {
      return false;
    }
  }

  public Boolean getBooleanBoxed(String name) {
    if (holder.containsKey(name)) {
      return holder.getBoolean(name);
    } else {
      return null;
    }
  }

  @Override
  public boolean getBoolean(String name, boolean defaultValue) {
    return holder.getBoolean(name, defaultValue);
  }

  @Override
  public boolean isNull(String name) {
    return holder.isNull(name);
  }

  @Override
  public ValueType getValueType() {
    return holder.getValueType();
  }

  @Override
  public int size() {
    return holder.size();
  }

  @Override
  public boolean isEmpty() {
    return holder.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return holder.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return holder.containsValue(value);
  }

  @Override
  public JsonValue get(Object key) {
    if (!holder.containsKey(key)) {
      return null;
    }
    return holder.get(key);
  }

  @Override
  public JsonValue put(String key, JsonValue value) {
    return holder.put(key, value);
  }

  @Override
  public JsonValue remove(Object key) {
    return holder.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends JsonValue> m) {
    holder.putAll(m);
  }

  @Override
  public void clear() {
    holder.clear();
  }

  @Override
  public Set<String> keySet() {
    return holder.keySet();
  }

  @Override
  public Collection<JsonValue> values() {
    return holder.values();
  }

  @Override
  public Set<Entry<String, JsonValue>> entrySet() {
    return holder.entrySet();
  }
}
