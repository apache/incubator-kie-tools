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

package org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.impl;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.YamlSequence;
import com.amihaiemil.eoyaml.YamlSequenceBuilder;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.stream.YAMLSequenceWriter;

public class DefaultYAMLSequenceWriter implements YAMLSequenceWriter {

  private YamlSequenceBuilder writer = Yaml.createYamlSequenceBuilder();

  @Override
  public YAMLSequenceWriter value(String value) {
    writer = writer.add(value);
    return this;
  }

  @Override
  public YAMLSequenceWriter value(YamlNode value) {
    writer = writer.add(value);
    return this;
  }

  @Override
  public YamlSequence getWriter() {
    return writer.build();
  }
}
