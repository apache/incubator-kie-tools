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
import java.util.Optional;
import java.util.UUID;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.constructor.ConstructYamlNull;
import org.snakeyaml.engine.v2.constructor.json.ConstructOptionalClass;
import org.snakeyaml.engine.v2.constructor.json.ConstructUuidClass;
import org.snakeyaml.engine.v2.constructor.json.ConstructYamlBinary;
import org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonBool;
import org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonInt;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

/** Default schema */
public class JsonSchema implements Schema {
  // No need to extend Failsafe schema because it is empty

  private final Map<Tag, ConstructNode> tagConstructors = new HashMap<>();
  private final ScalarResolver scalarResolver = new JsonScalarResolver();

  /** Create the instance */
  public JsonSchema() {
    this.tagConstructors.put(Tag.NULL, new ConstructYamlNull());
    this.tagConstructors.put(Tag.BOOL, new ConstructYamlJsonBool());
    this.tagConstructors.put(Tag.INT, new ConstructYamlJsonInt());
    this.tagConstructors.put(Tag.FLOAT, new ConstructYamlJsonFloat());

    this.tagConstructors.put(Tag.BINARY, new ConstructYamlBinary());

    this.tagConstructors.put(new Tag(UUID.class), new ConstructUuidClass());
    this.tagConstructors.put(
        new Tag(Optional.class), new ConstructOptionalClass(getScalarResolver()));
  }

  /**
   * Create ScalarResolver
   *
   * @return JsonScalarResolver
   */
  @Override
  public ScalarResolver getScalarResolver() {
    return scalarResolver;
  }

  /**
   * Basic constructs
   *
   * @return map with constructs
   */
  @Override
  public Map<Tag, ConstructNode> getSchemaTagConstructors() {
    return tagConstructors;
  }
}
