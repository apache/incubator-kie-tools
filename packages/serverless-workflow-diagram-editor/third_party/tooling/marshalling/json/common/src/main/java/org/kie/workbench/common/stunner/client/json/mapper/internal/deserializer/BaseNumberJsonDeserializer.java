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

package org.kie.workbench.common.stunner.client.json.mapper.internal.deserializer;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.json.JsonValueDecorator;
import jakarta.json.bind.serializer.DeserializationContext;

public abstract class BaseNumberJsonDeserializer<N extends Number> extends JsonbDeserializer<N> {

  public static final class BigDecimalJsonDeserializer
      extends BaseNumberJsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asBigDecimal();
    }
  }

  public static final class BigIntegerJsonDeserializer
      extends BaseNumberJsonDeserializer<BigInteger> {

    @Override
    public BigInteger deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asBigInteger();
    }
  }

  public static final class ByteJsonDeserializer extends BaseNumberJsonDeserializer<Byte> {

    @Override
    public Byte deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asByte();
    }
  }

  public static final class DoubleJsonDeserializer extends BaseNumberJsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asDouble();
    }
  }

  public static final class FloatJsonDeserializer extends BaseNumberJsonDeserializer<Float> {

    @Override
    public Float deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asFloat();
    }
  }

  public static final class IntegerJsonDeserializer extends BaseNumberJsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asInteger();
    }
  }

  public static final class LongJsonDeserializer extends BaseNumberJsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asLong();
    }
  }

  public static final class ShortJsonDeserializer extends BaseNumberJsonDeserializer<Short> {

    @Override
    public Short deserialize(JsonValue json, DeserializationContext ctx) throws JsonException {
      if (json == null) {
        return null;
      }
      return new JsonValueDecorator(json).asShort();
    }
  }
}
