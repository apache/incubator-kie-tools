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
package org.snakeyaml.engine.v2.constructor.core;

import org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;

/** Create Double instances for float */
public class ConstructYamlCoreFloat extends ConstructYamlJsonFloat {

  protected String constructScalar(Node node) {
    // to lower case to parse the special values in any case
    return ((ScalarNode) node).getValue().toLowerCase();
  }
}
