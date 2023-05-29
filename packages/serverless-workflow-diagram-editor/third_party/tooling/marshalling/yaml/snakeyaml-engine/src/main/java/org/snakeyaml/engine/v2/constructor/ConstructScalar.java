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
package org.snakeyaml.engine.v2.constructor;

import java.util.HashMap;
import java.util.Map;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;

/** Share common code for scalar constructs */
public abstract class ConstructScalar implements ConstructNode {

  protected static final Map<String, Boolean> BOOL_VALUES = new HashMap<>();

  static {
    BOOL_VALUES.put("true", Boolean.TRUE);
    BOOL_VALUES.put("false", Boolean.FALSE);
  }

  /**
   * Create String from the provided scalar node
   *
   * @param node - the source
   * @return value of the scalar node
   */
  protected String constructScalar(Node node) {
    return ((ScalarNode) node).getValue();
  }
}
