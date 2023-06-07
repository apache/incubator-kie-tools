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
package org.snakeyaml.engine.v2.nodes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.exceptions.Mark;

/**
 * Represents a map.
 *
 * <p>A map is a collection of unsorted key-value pairs.
 */
public class MappingNode extends CollectionNode<NodeTuple> {

  private List<NodeTuple> value;

  /**
   * Create
   *
   * @param tag - tag of the node
   * @param resolved - true when the tag is implicitly resolved
   * @param value - the value
   * @param flowStyle - the flow style of the node
   * @param startMark - start
   * @param endMark - end
   */
  public MappingNode(
      Tag tag,
      boolean resolved,
      List<NodeTuple> value,
      FlowStyle flowStyle,
      Optional<Mark> startMark,
      Optional<Mark> endMark) {
    super(tag, flowStyle, startMark, endMark);
    Objects.requireNonNull(value);
    this.value = value;
    this.resolved = resolved;
  }

  /**
   * Create
   *
   * @param tag - tag of the node
   * @param value - the value
   * @param flowStyle - the flow style of the node
   */
  public MappingNode(Tag tag, List<NodeTuple> value, FlowStyle flowStyle) {
    this(tag, true, value, flowStyle, Optional.empty(), Optional.empty());
  }

  @Override
  public NodeType getNodeType() {
    return NodeType.MAPPING;
  }

  /**
   * Returns the entries of this map.
   *
   * @return List of entries.
   */
  public List<NodeTuple> getValue() {
    return value;
  }

  /**
   * Applications may need to replace the content (Spring Boot). Merging was removed, but it may be
   * implemented.
   *
   * @param merged - merged data to replace the internal value
   */
  public void setValue(List<NodeTuple> merged) {
    Objects.requireNonNull(merged);
    value = merged;
  }

  @Override
  public String toString() {
    String values;
    StringBuilder buf = new StringBuilder();
    for (NodeTuple node : getValue()) {
      buf.append("{ key=");
      buf.append(node.getKeyNode());
      buf.append("; value=");
      if (node.getValueNode() instanceof CollectionNode) {
        // to avoid overflow in case of recursive structures
        buf.append(System.identityHashCode(node.getValueNode()));
      } else {
        buf.append(node);
      }
      buf.append(" }");
    }
    values = buf.toString();
    return "<" + this.getClass().getName() + " (tag=" + getTag() + ", values=" + values + ")>";
  }
}
