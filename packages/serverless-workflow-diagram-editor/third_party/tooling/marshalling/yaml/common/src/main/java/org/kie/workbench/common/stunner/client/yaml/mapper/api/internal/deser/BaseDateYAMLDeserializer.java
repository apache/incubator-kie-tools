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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;

/**
 * Base implementation of {@link YAMLDeserializer} for dates.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseDateYAMLDeserializer<D extends Date> implements YAMLDeserializer<D> {

  /** {@inheritDoc} */
  @Override
  public D deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return deserialize(yaml.getNode(key), ctx);
  }

  /** Default implementation of {@link BaseDateYAMLDeserializer} for {@link Date} */
  public static final class DateYAMLDeserializer extends BaseDateYAMLDeserializer<Date> {

    public static final DateYAMLDeserializer INSTANCE = new DateYAMLDeserializer();

    @Override
    public Date deserialize(YamlNode date, YAMLDeserializationContext ctx) {
      if (date == null) {
        return null;
      }
      YamlScalar<String> scalar = date.asScalar();
      return new Date(Long.parseLong(scalar.value()));
    }
  }

  /** Default implementation of {@link BaseDateYAMLDeserializer} for {@link java.sql.Date} */
  public static final class SqlDateYAMLDeserializer
      extends BaseDateYAMLDeserializer<java.sql.Date> {

    public static final SqlDateYAMLDeserializer INSTANCE = new SqlDateYAMLDeserializer();

    private static final String SQL_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public java.sql.Date deserialize(YamlNode date, YAMLDeserializationContext ctx) {
      if (date == null) {
        return null;
      }
      YamlScalar<String> scalar = date.asScalar();
      return new java.sql.Date(Long.parseLong(scalar.value()));
    }
  }

  /** Default implementation of {@link BaseDateYAMLDeserializer} for {@link Time} */
  public static final class SqlTimeYAMLDeserializer extends BaseDateYAMLDeserializer<Time> {

    public static final SqlTimeYAMLDeserializer INSTANCE = new SqlTimeYAMLDeserializer();

    @Override
    public Time deserialize(YamlNode date, YAMLDeserializationContext ctx) {
      YamlScalar<String> scalar = date.asScalar();
      return Time.valueOf(scalar.value());
    }
  }

  /** Default implementation of {@link BaseDateYAMLDeserializer} for {@link Timestamp} */
  public static final class SqlTimestampYAMLDeserializer
      extends BaseDateYAMLDeserializer<Timestamp> {

    public static final SqlTimestampYAMLDeserializer INSTANCE = new SqlTimestampYAMLDeserializer();

    @Override
    public Timestamp deserialize(YamlNode date, YAMLDeserializationContext ctx) {
      YamlScalar<String> scalar = date.asScalar();
      return Timestamp.valueOf(scalar.value());
    }
  }
}
