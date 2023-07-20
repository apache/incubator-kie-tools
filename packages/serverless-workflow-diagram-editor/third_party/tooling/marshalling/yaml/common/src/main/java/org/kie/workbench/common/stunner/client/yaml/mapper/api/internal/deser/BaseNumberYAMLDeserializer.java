/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLDeserializationException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;

/**
 * Base implementation of {@link YAMLDeserializer} for {@link java.lang.Number}.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseNumberYAMLDeserializer<N extends Number> implements YAMLDeserializer<N> {

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link BigDecimal} */
  public static final class BigDecimalYAMLDeserializer
      extends BaseNumberYAMLDeserializer<BigDecimal> {

    public static final BigDecimalYAMLDeserializer INSTANCE = new BigDecimalYAMLDeserializer();

    @Override
    public BigDecimal deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public BigDecimal deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<String> scalar = value.asScalar();
      return new BigDecimal(scalar.value());
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link BigInteger} */
  public static final class BigIntegerYAMLDeserializer
      extends BaseNumberYAMLDeserializer<BigInteger> {

    public static final BigIntegerYAMLDeserializer INSTANCE = new BigIntegerYAMLDeserializer();

    @Override
    public BigInteger deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public BigInteger deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<String> scalar = value.asScalar();
      return new BigInteger(scalar.value());
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Byte} */
  public static final class ByteYAMLDeserializer extends BaseNumberYAMLDeserializer<Byte> {

    public static final ByteYAMLDeserializer INSTANCE = new ByteYAMLDeserializer();

    @Override
    public Byte deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Byte deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return 0;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof Integer) {
        return ((Integer) scalar.value()).byteValue();
      }

      return Byte.valueOf((String) scalar.value());
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Double} */
  public static final class DoubleYAMLDeserializer extends BaseNumberYAMLDeserializer<Double> {

    public static final DoubleYAMLDeserializer INSTANCE = new DoubleYAMLDeserializer();

    @Override
    public Double deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Double deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof Integer) {
        return ((Integer) scalar.value()).doubleValue();
      }
      return (Double) scalar.value();
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Float} */
  public static final class FloatYAMLDeserializer extends BaseNumberYAMLDeserializer<Float> {

    public static final FloatYAMLDeserializer INSTANCE = new FloatYAMLDeserializer();

    @Override
    public Float deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Float deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof Double) {
        return ((Double) scalar.value()).floatValue();
      }
      if (scalar.value() instanceof Integer) {
        return ((Integer) scalar.value()).floatValue();
      }
      return (Float) scalar.value();
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Integer} */
  public static final class IntegerYAMLDeserializer extends BaseNumberYAMLDeserializer<Integer> {

    public static final IntegerYAMLDeserializer INSTANCE = new IntegerYAMLDeserializer();

    @Override
    public Integer deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Integer deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value == null || value.isEmpty()) {
        return null;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof String) {
        return Integer.parseInt((String) scalar.value());
      }
      return (Integer) scalar.value();
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Long} */
  public static final class LongYAMLDeserializer extends BaseNumberYAMLDeserializer<Long> {

    public static final LongYAMLDeserializer INSTANCE = new LongYAMLDeserializer();

    @Override
    public Long deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Long deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof Integer) {
        return ((Integer) scalar.value()).longValue();
      }
      return (Long) scalar.value();
    }
  }

  /** Default implementation of {@link BaseNumberYAMLDeserializer} for {@link Short} */
  public static final class ShortYAMLDeserializer extends BaseNumberYAMLDeserializer<Short> {

    public static final ShortYAMLDeserializer INSTANCE = new ShortYAMLDeserializer();

    @Override
    public Short deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx)
        throws YAMLDeserializationException {
      YamlNode value = yaml.getNode(key);
      return deserialize(value, ctx);
    }

    @Override
    public Short deserialize(YamlNode value, YAMLDeserializationContext ctx) {
      if (value.isEmpty()) {
        return null;
      }
      YamlScalar<?> scalar = value.asScalar();
      if (scalar.value() instanceof Integer) {
        return ((Integer) scalar.value()).shortValue();
      }
      return (Short) scalar.value();
    }
  }
}
