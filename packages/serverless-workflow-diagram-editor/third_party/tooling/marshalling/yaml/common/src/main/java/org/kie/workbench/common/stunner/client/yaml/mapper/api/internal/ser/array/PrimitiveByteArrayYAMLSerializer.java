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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.array;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.AbstractYAMLSerializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.ser.YAMLSerializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.utils.Base64Utils;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLWriter;

/**
 * Default {@link AbstractYAMLSerializer} implementation for array of byte.
 *
 * @author Nicolas Morel
 * @version $Id: $Id
 */
public class PrimitiveByteArrayYAMLSerializer extends BasicArrayYAMLSerializer<byte[]> {

  public static final PrimitiveByteArrayYAMLSerializer INSTANCE =
      new PrimitiveByteArrayYAMLSerializer();

  /** {@inheritDoc} */
  @Override
  protected boolean isEmpty(byte[] value) {
    return null == value || value.length == 0;
  }

  /** {@inheritDoc} */
  @Override
  public void serialize(
      YAMLWriter writer, String propertyName, byte[] values, YAMLSerializationContext ctx) {
    if (!ctx.isWriteEmptyYAMLArrays() && values.length == 0) {
      writer.nullValue(propertyName);
      return;
    }
    writer.value(propertyName, Base64Utils.toBase64(values));
  }

  @Override
  public void serialize(YAMLSequenceWriter writer, byte[] value, YAMLSerializationContext ctx) {
    writer.value(Base64Utils.toBase64(value));
  }
}
