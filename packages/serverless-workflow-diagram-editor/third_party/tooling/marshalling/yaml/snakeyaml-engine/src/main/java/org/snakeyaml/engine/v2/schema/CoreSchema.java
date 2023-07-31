/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.snakeyaml.engine.v2.schema;

import java.util.HashMap;
import java.util.Map;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreBool;
import org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreFloat;
import org.snakeyaml.engine.v2.constructor.core.ConstructYamlCoreInt;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.CoreScalarResolver;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

/** Core schema */
public class CoreSchema extends JsonSchema {

  private final Map<Tag, ConstructNode> tagConstructors = new HashMap<>();

  public CoreSchema() {
    this.tagConstructors.put(Tag.BOOL, new ConstructYamlCoreBool());
    this.tagConstructors.put(Tag.INT, new ConstructYamlCoreInt());
    this.tagConstructors.put(Tag.FLOAT, new ConstructYamlCoreFloat());
  }

  /**
   * Create ScalarResolver
   *
   * @return CoreScalarResolver
   */
  @Override
  public ScalarResolver getScalarResolver() {
    return new CoreScalarResolver();
  }

  /**
   * Provide constructs to support the schema (bool, int, float)
   *
   * @return map
   */
  @Override
  public Map<Tag, ConstructNode> getSchemaTagConstructors() {
    Map<Tag, ConstructNode> json = super.getSchemaTagConstructors();
    json.putAll(tagConstructors);
    return json;
  }
}
