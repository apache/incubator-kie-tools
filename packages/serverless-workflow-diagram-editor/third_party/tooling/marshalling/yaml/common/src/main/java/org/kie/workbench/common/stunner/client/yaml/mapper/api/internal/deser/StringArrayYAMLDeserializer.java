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

import java.util.ArrayList;
import java.util.List;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.array.AbstractArrayYAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for array of {@link java.lang.String}.
 *
 * <p>Not working in production mode, cast problem. Can maybe work with disableCastChecking
 *
 * @author Nicolas Morel
 * @version $Id: $
 */
public class StringArrayYAMLDeserializer extends AbstractArrayYAMLDeserializer<String[]> {

  private static final StringArrayYAMLDeserializer INSTANCE = new StringArrayYAMLDeserializer();

  private StringArrayYAMLDeserializer() {}

  /**
   * getInstance
   *
   * @return an instance of {@link StringArrayYAMLDeserializer}
   */
  public static StringArrayYAMLDeserializer getInstance() {
    return INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public String[] doDeserializeArray(YamlMapping yaml, String key, YAMLDeserializationContext ctx) {
    List<String> list = new ArrayList<>();
    YamlSequence yamlSequence = yaml.yamlSequence(key);

    for (int i = 0; i < yamlSequence.size(); i++) {
      list.add(yamlSequence.string(i));
    }
    return list.toArray(new String[list.size()]);
  }
}
