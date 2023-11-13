/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package jakarta.json.stream;

import java.math.BigDecimal;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.JsonValueDecorator;

public class JsonParserImpl implements JsonParser {

  private JsonValue holder;

  public JsonParserImpl(JsonValue holder) {
    this.holder = holder;
  }

  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public Event next() {
    if (holder != null
            && (holder.getValueType() != null && holder.getValueType() == JsonValue.ValueType.OBJECT)) {
      return Event.START_OBJECT;
    }
    return null;
  }

  public JsonArray getArray() {
    if (holder.getValueType() == JsonValue.ValueType.ARRAY) {
      return holder.asJsonArray();
    }
    throw new IllegalStateException("Not an array");
  }

  public JsonObject getObject() {
    if (holder.getValueType() == JsonValue.ValueType.OBJECT) {
      if (holder instanceof JsonObject) {
        return (JsonObject) holder;
      }
      return holder.asJsonObject();
    }
    throw new IllegalStateException("Not an object");
  }

  public JsonValue getValue() {
    return holder;
  }

  @Override
  public String getString() {
    if (holder.getValueType().equals(JsonValue.ValueType.STRING)) {
      return new JsonValueDecorator(holder).asString();
    }
    throw new IllegalStateException("Not a string");
  }

  @Override
  public boolean isIntegralNumber() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getInt() {
    if (holder.getValueType().equals(JsonValue.ValueType.NUMBER)) {
      return new JsonValueDecorator(holder).asInteger();
    }
    throw new IllegalStateException("Not a int");
  }

  @Override
  public long getLong() {
    if (holder.getValueType().equals(JsonValue.ValueType.NUMBER)) {
      return new JsonValueDecorator(holder).asLong();
    }
    throw new IllegalStateException("Not a long");
  }

  @Override
  public BigDecimal getBigDecimal() {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonLocation getLocation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {}
}
