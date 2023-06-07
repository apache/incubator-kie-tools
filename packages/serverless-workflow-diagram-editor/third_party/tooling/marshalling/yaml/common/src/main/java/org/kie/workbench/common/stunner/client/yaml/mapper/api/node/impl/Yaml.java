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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.node.impl;

import java.util.Map;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public class Yaml {

  private Yaml() {}

  public static YamlMapping create() {
    return new YamlMappingNodeImpl();
  }

  public static YamlMapping create(DumpSettings settings) {
    return new YamlMappingNodeImpl(settings);
  }

  public static YamlMapping fromString(String yaml) {
    LoadSettings settings = LoadSettings.builder().build();
    return fromString(settings, yaml);
  }

  @SuppressWarnings("unchecked")
  public static YamlMapping fromString(LoadSettings settings, String yaml) {
    Load load = new Load(settings);
    Map<String, Object> map = (Map<String, Object>) load.loadFromString(yaml);
    return new YamlMappingNodeImpl(map);
  }
}
