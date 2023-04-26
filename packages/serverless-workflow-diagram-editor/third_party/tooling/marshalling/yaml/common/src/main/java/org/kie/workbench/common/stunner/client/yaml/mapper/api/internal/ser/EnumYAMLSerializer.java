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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;

/**
 * Default {@link AbstractYAMLSerializer} implementation for {@link Enum}.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class EnumYAMLSerializer<E extends Enum<E>> implements YAMLSerializer<E> {

  public static final EnumYAMLSerializer<?> INSTANCE = new EnumYAMLSerializer();

  /** {@inheritDoc} */
  @Override
  public void serialize(
      YAMLWriter writer, String propertyName, E value, YAMLSerializationContext ctx) {
    writer.value(propertyName, value.name());
  }

  @Override
  public void serialize(YAMLSequenceWriter writer, E value, YAMLSerializationContext ctx) {
    if (null == value) {
      if (ctx.isSerializeNulls()) {
        writer.value("~");
      }
    } else {
      writer.value(value.name());
    }
  }
}
