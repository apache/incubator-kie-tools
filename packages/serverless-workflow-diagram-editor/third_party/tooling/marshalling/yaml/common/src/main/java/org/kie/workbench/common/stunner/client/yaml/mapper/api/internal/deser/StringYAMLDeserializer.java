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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.lang.String}.
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class StringYAMLDeserializer implements YAMLDeserializer<String> {

  public static final StringYAMLDeserializer INSTANCE = new StringYAMLDeserializer();

  @Override
  public String deserialize(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    return deserialize(yaml.getNode(key), ctx);
  }

  @Override
  public String deserialize(YamlNode node, YAMLDeserializationContext ctx) {
    if (node == null || node.isEmpty()) {
      return null;
    }
    if (node.asScalar().value() instanceof String) {
      String result = node.<String>asScalar().value();
      if (result.equals("~")) {
        return null;
      }
      return result;
    }
    return node.asScalar().value().toString();
  }
}
