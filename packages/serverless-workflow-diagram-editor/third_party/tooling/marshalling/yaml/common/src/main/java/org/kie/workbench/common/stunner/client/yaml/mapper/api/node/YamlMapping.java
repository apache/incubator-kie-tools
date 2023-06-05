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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.node;

import java.util.Collection;

public interface YamlMapping extends YamlNode {

  Collection<String> keys();

  Collection<YamlNode> values();

  YamlMapping getMappingNode(String key);

  YamlSequence getSequenceNode(String key);

  <T> YamlScalar<T> getScalarNode(String key);

  YamlNode getNode(String key);

  YamlNode addNode(String key, YamlNode node);

  <T> YamlScalar<T> addScalarNode(String key, T value);

  YamlSequence addSequenceNode(String key);

  YamlMapping addMappingNode(String key);
}
