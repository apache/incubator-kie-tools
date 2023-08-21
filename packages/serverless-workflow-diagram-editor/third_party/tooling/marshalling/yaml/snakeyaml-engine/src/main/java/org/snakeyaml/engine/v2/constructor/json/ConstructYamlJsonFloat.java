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
package org.snakeyaml.engine.v2.constructor.json;

import org.snakeyaml.engine.v2.constructor.ConstructScalar;
import org.snakeyaml.engine.v2.nodes.Node;

/** Create Double instances for float */
public class ConstructYamlJsonFloat extends ConstructScalar {

  @Override
  public Object construct(Node node) {
    String value = constructScalar(node);
    if (".inf".equals(value)) {
      return Double.POSITIVE_INFINITY;
    } else if ("-.inf".equals(value)) {
      return Double.NEGATIVE_INFINITY;
    } else if (".nan".equals(value)) {
      return Double.NaN;
    } else {
      return constructFromString(value);
    }
  }

  protected Object constructFromString(String value) {
    int sign = +1;
    char first = value.charAt(0);
    if (first == '-') {
      sign = -1;
      value = value.substring(1);
    } else if (first == '+') {
      value = value.substring(1);
    }
    double d = Double.valueOf(value);
    return Double.valueOf(d * sign);
  }
}
