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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Base implementation of {@link AbstractYAMLSerializer} for {@link Number}.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseNumberYAMLSerializer<N extends Number> implements YAMLSerializer<N> {

  /** {@inheritDoc} */
  @Override
  public void serialize(
      YamlMapping writer, String propertyName, N value, YAMLSerializationContext ctx) {
    writer.addScalarNode(propertyName, value);
  }

  @Override
  public void serialize(YamlSequence writer, N value, YAMLSerializationContext ctx) {
    if (null == value) {
      if (ctx.isSerializeNulls()) {
        writer.addScalarNode("~");
      }
    } else {
      writer.addScalarNode(value);
    }
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link BigDecimal} */
  public static final class BigDecimalYAMLSerializer extends BaseNumberYAMLSerializer<BigDecimal> {

    public static final BigDecimalYAMLSerializer INSTANCE = new BigDecimalYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link BigInteger} */
  public static final class BigIntegerYAMLSerializer extends BaseNumberYAMLSerializer<BigInteger> {

    public static final BigIntegerYAMLSerializer INSTANCE = new BigIntegerYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Byte} */
  public static final class ByteYAMLSerializer extends BaseNumberYAMLSerializer<Byte> {

    public static final ByteYAMLSerializer INSTANCE = new ByteYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Double} */
  public static final class DoubleYAMLSerializer extends BaseNumberYAMLSerializer<Double> {

    public static final DoubleYAMLSerializer INSTANCE = new DoubleYAMLSerializer();

    @Override
    public void serialize(
        YamlMapping writer, String propertyName, Double value, YAMLSerializationContext ctx) {
      // writer has a special method to write double, let's use instead of default Number method.
      writer.addScalarNode(propertyName, value);
    }
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Float} */
  public static final class FloatYAMLSerializer extends BaseNumberYAMLSerializer<Float> {

    public static final FloatYAMLSerializer INSTANCE = new FloatYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Integer} */
  public static final class IntegerYAMLSerializer extends BaseNumberYAMLSerializer<Integer> {

    public static final IntegerYAMLSerializer INSTANCE = new IntegerYAMLSerializer();

    @Override
    public void serialize(YamlSequence writer, Integer value, YAMLSerializationContext ctx) {
      if (null == value) {
        if (ctx.isSerializeNulls()) {
          writer.addScalarNode("~");
        }
      } else {
        writer.addScalarNode(value);
      }
    }
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Long} */
  public static final class LongYAMLSerializer extends BaseNumberYAMLSerializer<Long> {

    public static final LongYAMLSerializer INSTANCE = new LongYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Short} */
  public static final class ShortYAMLSerializer extends BaseNumberYAMLSerializer<Short> {

    public static final ShortYAMLSerializer INSTANCE = new ShortYAMLSerializer();
  }

  /** Default implementation of {@link BaseNumberYAMLSerializer} for {@link Number} */
  public static final class NumberYAMLSerializer extends BaseNumberYAMLSerializer<Number> {

    public static final NumberYAMLSerializer INSTANCE = new NumberYAMLSerializer();
  }
}
