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

import java.util.Optional;

import org.snakeyaml.engine.v2.constructor.ConstructScalar;
import org.snakeyaml.engine.v2.exceptions.ConstructorException;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeType;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

/** Create instances of Optional */
public class ConstructOptionalClass extends ConstructScalar {

  private final ScalarResolver scalarResolver;

  public ConstructOptionalClass(ScalarResolver scalarResolver) {
    this.scalarResolver = scalarResolver;
  }

  @Override
  public Object construct(Node node) {
    if (node.getNodeType() != NodeType.SCALAR) {
      throw new ConstructorException(
          "while constructing Optional",
          Optional.empty(),
          "found non scalar node",
          node.getStartMark());
    }
    String value = constructScalar(node);
    Tag implicitTag = scalarResolver.resolve(value, true);
    if (implicitTag.equals(Tag.NULL)) {
      return Optional.empty();
    } else {
      return Optional.of(value);
    }
  }
}
