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
package org.snakeyaml.engine.v2.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.snakeyaml.engine.v2.api.RepresentToNode;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.AnchorNode;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;

/** Represent basic YAML structures: scalar, sequence, mapping */
public abstract class BaseRepresenter {

  /** Keep representers which must match the class exactly */
  protected final Map<Class<?>, RepresentToNode> representers = new HashMap<>();
  /** Keep representers which match a parent of the class to be represented */
  protected final Map<Class<?>, RepresentToNode> parentClassRepresenters = new LinkedHashMap<>();
  /**
   * Keep references of already represented instances. The order is important (map can be also a
   * sequence of key-values)
   */
  protected final Map<Object, Node> representedObjects =
      new IdentityHashMap<Object, Node>() {
        @Override
        public Node put(Object key, Node value) {
          return super.put(key, new AnchorNode(value));
        }
      };
  /** in Java 'null' is not a type. So we have to keep the null representer separately */
  protected RepresentToNode nullRepresenter;
  /** scalar style */
  protected ScalarStyle defaultScalarStyle = ScalarStyle.PLAIN;
  /** flow style for collections */
  protected FlowStyle defaultFlowStyle = FlowStyle.AUTO;
  /** the current object to be converted to Node */
  protected Object objectToRepresent;

  /**
   * Represent the provided Java instance to a Node
   *
   * @param data - Java instance to be represented
   * @return The Node to be serialized
   */
  public Node represent(Object data) {
    Node node = representData(data);
    representedObjects.clear();
    objectToRepresent = null;
    return node;
  }

  /**
   * Find the representer which is suitable to represent the internal structure of the provided
   * instance to a Node
   *
   * @param data - the data to be serialized
   * @return RepresentToNode to call to create a Node
   */
  protected Optional<RepresentToNode> findRepresenterFor(Object data) {
    Class<?> clazz = data.getClass();
    // check the same class
    if (representers.containsKey(clazz)) {
      return Optional.of(representers.get(clazz));
    } else {
      // check the parents
      for (Map.Entry<Class<?>, RepresentToNode> parentRepresenterEntry :
          parentClassRepresenters.entrySet()) {
        if (parentRepresenterEntry.getKey().equals(data.getClass())) {
          return Optional.of(parentRepresenterEntry.getValue());
        }
      }
      return Optional.empty();
    }
  }

  /**
   * Find the representer and use it to create the Node from instance
   *
   * @param data - the source
   * @return Node for the provided source
   */
  protected final Node representData(Object data) {
    objectToRepresent = data;
    // check for identity
    if (representedObjects.containsKey(objectToRepresent)) {
      return representedObjects.get(objectToRepresent);
    }
    // check for null first
    if (data == null) {
      return nullRepresenter.representData(null);
    }
    RepresentToNode representer =
        findRepresenterFor(data)
            .orElseThrow(
                () -> new YamlEngineException("Representer is not defined for " + data.getClass()));
    return representer.representData(data);
  }

  /**
   * Create Scalar Node from string
   *
   * @param tag - the tag in Node
   * @param value - the source
   * @param style - the style
   * @return Node for string
   */
  protected Node representScalar(Tag tag, String value, ScalarStyle style) {
    if (style == ScalarStyle.PLAIN) {
      style = this.defaultScalarStyle;
    }
    return new ScalarNode(tag, value, style);
  }

  /**
   * Create Node for string using PLAIN scalar style if possible
   *
   * @param tag - the tag for Node
   * @param value - the surce
   * @return Node for string
   */
  protected Node representScalar(Tag tag, String value) {
    return representScalar(tag, value, ScalarStyle.PLAIN);
  }

  /**
   * Create Node
   *
   * @param tag - tag to use in Node
   * @param sequence - the source
   * @param flowStyle - the flow style
   * @return the Node from the source iterable
   */
  protected Node representSequence(Tag tag, Iterable<?> sequence, FlowStyle flowStyle) {
    int size = 10; // default for ArrayList
    if (sequence instanceof List<?>) {
      size = ((List<?>) sequence).size();
    }
    List<Node> value = new ArrayList<>(size);
    SequenceNode node = new SequenceNode(tag, value, flowStyle);
    representedObjects.put(objectToRepresent, node);
    FlowStyle bestStyle = FlowStyle.FLOW;
    for (Object item : sequence) {
      Node nodeItem = representData(item);
      if (!(nodeItem instanceof ScalarNode && ((ScalarNode) nodeItem).isPlain())) {
        bestStyle = FlowStyle.BLOCK;
      }
      value.add(nodeItem);
    }
    if (flowStyle == FlowStyle.AUTO) {
      if (defaultFlowStyle != FlowStyle.AUTO) {
        node.setFlowStyle(defaultFlowStyle);
      } else {
        node.setFlowStyle(bestStyle);
      }
    }
    return node;
  }

  /**
   * Create a tuple for one key pair
   *
   * @param entry - Map entry
   * @return the tuple where both key and value are converted to Node
   */
  protected NodeTuple representMappingEntry(Map.Entry<?, ?> entry) {
    return new NodeTuple(representData(entry.getKey()), representData(entry.getValue()));
  }

  /**
   * Create Node for the provided Map
   *
   * @param tag - the tag for Node
   * @param mapping - the source
   * @param flowStyle - the style of Node
   * @return Node for the source Map
   */
  protected Node representMapping(Tag tag, Map<?, ?> mapping, FlowStyle flowStyle) {
    List<NodeTuple> value = new ArrayList<>(mapping.size());
    MappingNode node = new MappingNode(tag, value, flowStyle);
    representedObjects.put(objectToRepresent, node);
    FlowStyle bestStyle = FlowStyle.FLOW;
    for (Map.Entry<?, ?> entry : mapping.entrySet()) {
      NodeTuple tuple = representMappingEntry(entry);
      if (!(tuple.getKeyNode() instanceof ScalarNode
          && ((ScalarNode) tuple.getKeyNode()).isPlain())) {
        bestStyle = FlowStyle.BLOCK;
      }
      if (!(tuple.getValueNode() instanceof ScalarNode
          && ((ScalarNode) tuple.getValueNode()).isPlain())) {
        bestStyle = FlowStyle.BLOCK;
      }
      value.add(tuple);
    }
    if (flowStyle == FlowStyle.AUTO) {
      if (defaultFlowStyle != FlowStyle.AUTO) {
        node.setFlowStyle(defaultFlowStyle);
      } else {
        node.setFlowStyle(bestStyle);
      }
    }
    return node;
  }
}
