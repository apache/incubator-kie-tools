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

import java.util.Map;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

/** Interface to be implemented by any Schema */
public interface Schema {

  /**
   * Provide the way to connect a tag to a node by the contents of the scalar node. It is used
   * either during implicit tag resolution for parsing or for dumping
   *
   * @return tag resolver for parse and dump
   */
  ScalarResolver getScalarResolver();

  /**
   * Provide the way to construct the resolved tag. This map will override the default values in
   * tagConstructors
   *
   * @return constructors for the tags in schema
   */
  Map<Tag, ConstructNode> getSchemaTagConstructors();
}
