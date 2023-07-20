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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.StringYAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.YAMLDeserializationContext;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.utils.Base64Utils;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;

/**
 * Default {@link YAMLDeserializer} implementation for array of byte.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class PrimitiveByteArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<byte[]> {

  public static final PrimitiveByteArrayYAMLDeserializer INSTANCE =
      new PrimitiveByteArrayYAMLDeserializer();

  /** {@inheritDoc} */
  @Override
  public byte[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {

    String result = StringYAMLDeserializer.INSTANCE.deserialize(yaml.getNode(key), ctx);
    if (result != null) {
      return Base64Utils.fromBase64(result);
    }
    return new byte[0];
  }
}
