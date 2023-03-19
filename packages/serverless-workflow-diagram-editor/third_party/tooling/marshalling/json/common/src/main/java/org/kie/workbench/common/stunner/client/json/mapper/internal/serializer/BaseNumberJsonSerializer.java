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

package org.kie.workbench.common.stunner.client.json.mapper.internal.serializer;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public abstract class BaseNumberJsonSerializer<N extends Number> extends JsonSerializer<N> {

  public static final class BigDecimalJsonSerializer extends BaseNumberJsonSerializer<BigDecimal> {

    @Override
    public void serialize(
        BigDecimal obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(BigDecimal obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class BigIntegerJsonSerializer extends BaseNumberJsonSerializer<BigInteger> {

    @Override
    public void serialize(
        BigInteger obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(BigInteger obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class ByteJsonSerializer extends BaseNumberJsonSerializer<Byte> {

    @Override
    public void serialize(
        Byte obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Byte obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class DoubleJsonSerializer extends BaseNumberJsonSerializer<Double> {

    @Override
    public void serialize(
        Double obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Double obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class FloatJsonSerializer extends BaseNumberJsonSerializer<Float> {

    @Override
    public void serialize(
        Float obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Float obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class IntegerJsonSerializer extends BaseNumberJsonSerializer<Integer> {

    @Override
    public void serialize(
        Integer obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Integer obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class LongJsonSerializer extends BaseNumberJsonSerializer<Long> {

    @Override
    public void serialize(
        Long obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Long obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }

  public static final class ShortJsonSerializer extends BaseNumberJsonSerializer<Short> {

    @Override
    public void serialize(
        Short obj, String property, JsonGenerator generator, SerializationContext ctx) {
      generator.write(property, obj);
    }

    @Override
    public void serialize(Short obj, JsonGenerator generator, SerializationContext ctx) {
      generator.write(obj);
    }
  }
}
