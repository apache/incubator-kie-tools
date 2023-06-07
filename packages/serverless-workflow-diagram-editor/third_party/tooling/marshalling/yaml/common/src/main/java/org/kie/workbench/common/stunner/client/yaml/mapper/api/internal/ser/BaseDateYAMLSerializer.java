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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

/**
 * Base implementation of {@link AbstractYAMLSerializer} for dates.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseDateYAMLSerializer<D extends Date> extends AbstractYAMLSerializer<D> {

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(D value) {
    return null == value || value.getTime() == 0l;
  }

  /** Default implementation of {@link BaseDateYAMLSerializer} for {@link Date} */
  public static final class DateYAMLSerializer extends BaseDateYAMLSerializer<Date> {

    public static final DateYAMLSerializer INSTANCE = new DateYAMLSerializer();

    @Override
    public void serialize(
        YamlMapping writer, String propertyName, Date value, YAMLSerializationContext ctx) {
      if ((ctx.isWriteDatesAsTimestamps())) {
        writer.addScalarNode(propertyName, String.valueOf(value.getTime()));
      } else {
        String date = value.toString(); // TODO use a better format
        writer.addScalarNode(propertyName, date);
      }
    }

    @Override
    public void serialize(YamlSequence writer, Date value, YAMLSerializationContext ctx) {
      writer.addScalarNode(String.valueOf(value.getTime()));
    }
  }

  /** Default implementation of {@link BaseDateYAMLSerializer} for {@link java.sql.Date} */
  public static final class SqlDateYAMLSerializer extends BaseDateYAMLSerializer<java.sql.Date> {

    private static final SqlDateYAMLSerializer INSTANCE = new SqlDateYAMLSerializer();

    private SqlDateYAMLSerializer() {}

    /** @return an instance of {@link SqlDateYAMLSerializer} */
    public static SqlDateYAMLSerializer getInstance() {
      return INSTANCE;
    }

    @Override
    public void serialize(
        YamlMapping writer,
        String propertyName,
        java.sql.Date value,
        YAMLSerializationContext ctx) {
      writer.addScalarNode(propertyName, value.toString());
    }

    @Override
    public void serialize(YamlSequence writer, java.sql.Date value, YAMLSerializationContext ctx) {
      writer.addScalarNode(value.toString());
    }
  }

  /** Default implementation of {@link BaseDateYAMLSerializer} for {@link Date} */
  public static final class SqlTimeYAMLSerializer extends BaseDateYAMLSerializer<Time> {

    private static final SqlTimeYAMLSerializer INSTANCE = new SqlTimeYAMLSerializer();

    private SqlTimeYAMLSerializer() {}

    /** @return an instance of {@link SqlTimeYAMLSerializer} */
    public static SqlTimeYAMLSerializer getInstance() {
      return INSTANCE;
    }

    @Override
    public void serialize(
        YamlMapping writer, String propertyName, Time value, YAMLSerializationContext ctx) {
      writer.addScalarNode(propertyName, value.toString());
    }

    @Override
    public void serialize(YamlSequence writer, Time value, YAMLSerializationContext ctx) {
      writer.addScalarNode(value.toString());
    }
  }

  /** Default implementation of {@link BaseDateYAMLSerializer} for {@link Timestamp} */
  public static final class SqlTimestampYAMLSerializer extends BaseDateYAMLSerializer<Timestamp> {

    private static final SqlTimestampYAMLSerializer INSTANCE = new SqlTimestampYAMLSerializer();

    private SqlTimestampYAMLSerializer() {}

    /** @return an instance of {@link SqlTimestampYAMLSerializer} */
    public static SqlTimestampYAMLSerializer getInstance() {
      return INSTANCE;
    }

    @Override
    public void serialize(
        YamlMapping writer, String propertyName, Timestamp value, YAMLSerializationContext ctx) {
      if (ctx.isWriteDatesAsTimestamps()) {
        writer.addScalarNode(propertyName, String.valueOf(value.getTime()));
      } else {
        String date = value.toString(); // TODO use a better format
        writer.addScalarNode(propertyName, date);
      }
    }

    @Override
    public void serialize(YamlSequence writer, Timestamp value, YAMLSerializationContext ctx) {
      writer.addScalarNode(String.valueOf(value.getTime()));
    }
  }
}
