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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.ConstructorException;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.SequenceNode;
import org.snakeyaml.engine.v2.nodes.Tag;

/** Base code */
public abstract class BaseConstructor {

  /** It maps the (explicit or implicit) tag to the Construct implementation. */
  protected final Map<Tag, ConstructNode> tagConstructors;

  final Map<Node, Object> constructedObjects;
  private final Set<Node> recursiveObjects;
  private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>>
      maps2fill;
  private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill;
  /** keep the settings */
  protected LoadSettings settings;

  /**
   * Create
   *
   * @param settings - the configuration option
   */
  public BaseConstructor(LoadSettings settings) {
    this.settings = settings;
    tagConstructors = new HashMap<>();
    constructedObjects = new HashMap<>();
    recursiveObjects = new HashSet<>();
    maps2fill = new ArrayList<>();
    sets2fill = new ArrayList<>();
  }

  /**
   * Ensure that the stream contains a single document and construct it
   *
   * @param optionalNode - composed Node
   * @return constructed instance
   */
  public Object constructSingleDocument(Optional<Node> optionalNode) {
    if (!optionalNode.isPresent() || Tag.NULL.equals(optionalNode.get().getTag())) {
      ConstructNode construct = tagConstructors.get(Tag.NULL);
      return construct.construct(optionalNode.orElse(null));
    } else {
      return construct(optionalNode.get());
    }
  }

  /**
   * Construct complete YAML document. Call the second step in case of recursive structures. At the
   * end cleans all the state.
   *
   * @param node root Node
   * @return Java instance
   */
  protected Object construct(Node node) {
    try {
      Object data = constructObject(node);
      fillRecursive();
      return data;
    } catch (YamlEngineException e) {
      throw e;
    } catch (RuntimeException e) {
      throw new YamlEngineException(e);
    } finally {
      constructedObjects.clear();
      recursiveObjects.clear();
    }
  }

  private void fillRecursive() {
    if (!maps2fill.isEmpty()) {
      for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : maps2fill) {
        RecursiveTuple<Object, Object> keyValueTuple = entry.getValue2();
        entry.getValue1().put(keyValueTuple.getValue1(), keyValueTuple.getValue2());
      }
      maps2fill.clear();
    }
    if (!sets2fill.isEmpty()) {
      for (RecursiveTuple<Set<Object>, Object> value : sets2fill) {
        value.getValue1().add(value.getValue2());
      }
      sets2fill.clear();
    }
  }

  /**
   * Construct object from the specified Node. Return existing instance if the node is already
   * constructed.
   *
   * @param node Node to be constructed
   * @return Java instance
   */
  protected Object constructObject(Node node) {
    Objects.requireNonNull(node, "Node cannot be null");
    if (constructedObjects.containsKey(node)) {
      return constructedObjects.get(node);
    }
    return constructObjectNoCheck(node);
  }

  /**
   * Construct object from the specified Node. It does not check if existing instance the node is
   * already constructed.
   *
   * @param node - the source
   * @return instantiated object
   */
  protected Object constructObjectNoCheck(Node node) {
    if (recursiveObjects.contains(node)) {
      throw new ConstructorException(
          null, Optional.empty(), "found unconstructable recursive node", node.getStartMark());
    }
    recursiveObjects.add(node);
    ConstructNode constructor =
        findConstructorFor(node)
            .orElseThrow(
                () ->
                    new ConstructorException(
                        null,
                        Optional.empty(),
                        "could not determine a constructor for the tag " + node.getTag(),
                        node.getStartMark()));
    Object data =
        (constructedObjects.containsKey(node))
            ? constructedObjects.get(node)
            : constructor.construct(node);

    constructedObjects.put(node, data);
    recursiveObjects.remove(node);
    if (node.isRecursive()) {
      constructor.constructRecursive(node, data);
    }
    return data;
  }

  /**
   * Select {@link ConstructNode} inside the provided {@link Node} or the one associated with the
   * {@link Tag}
   *
   * @param node {@link Node} to construct an instance from
   * @return {@link ConstructNode} implementation for the specified node
   */
  protected Optional<ConstructNode> findConstructorFor(Node node) {
    Tag tag = node.getTag();
    if (settings.getTagConstructors().containsKey(tag)) {
      return Optional.of(settings.getTagConstructors().get(tag));
    } else {
      if (tagConstructors.containsKey(tag)) {
        return Optional.of(tagConstructors.get(tag));
      } else {
        return Optional.empty();
      }
    }
  }

  /**
   * Create String from the provided scalar node
   *
   * @param node - the source
   * @return value of the scalar node
   */
  protected String constructScalar(ScalarNode node) {
    return node.getValue();
  }

  // >>>> DEFAULTS >>>>

  /**
   * Create List implementation. By default, it returns the value configured in the settings in
   * getDefaultList(). Any custom List implementation can be provided.
   *
   * @param node - the node to fill the List
   * @return empty List to fill
   */
  protected List<Object> createEmptyListForNode(SequenceNode node) {
    return settings.getDefaultList().apply(node.getValue().size());
  }

  /**
   * Create Set implementation. By default, it returns the value configured in the settings in
   * getDefaultSet(). Any custom Set implementation can be provided.
   *
   * @param node - the node to fill the Set
   * @return empty Set to fill
   */
  protected Set<Object> createEmptySetForNode(MappingNode node) {
    return settings.getDefaultSet().apply(node.getValue().size());
  }

  /**
   * Create Map implementation. By default, it returns the value configured in the settings in
   * getDefaultMap(). Any custom Map implementation can be provided.
   *
   * @param node - the node to fill the Map
   * @return empty Map to fill
   */
  protected Map<Object, Object> createEmptyMapFor(MappingNode node) {
    return settings.getDefaultMap().apply(node.getValue().size());
  }

  // <<<< DEFAULTS <<<<

  // <<<< NEW instance

  // >>>> Construct => NEW, 2ndStep(filling)

  /**
   * Create instance of List
   *
   * @param node - the source
   * @return filled List
   */
  protected List<Object> constructSequence(SequenceNode node) {
    List<Object> result = settings.getDefaultList().apply(node.getValue().size());
    constructSequenceStep2(node, result);
    return result;
  }

  /**
   * Fill the collection with the data from provided node
   *
   * @param node - the source
   * @param collection - the collection to fill
   */
  protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
    for (Node child : node.getValue()) {
      collection.add(constructObject(child));
    }
  }

  /**
   * Create instance of Set from mapping node
   *
   * @param node - the source
   * @return filled Set
   */
  protected Set<Object> constructSet(MappingNode node) {
    final Set<Object> set = settings.getDefaultSet().apply(node.getValue().size());
    constructSet2ndStep(node, set);
    return set;
  }

  /**
   * Create filled Map from the provided Node
   *
   * @param node - the source
   * @return filled Map
   */
  protected Map<Object, Object> constructMapping(MappingNode node) {
    final Map<Object, Object> mapping = settings.getDefaultMap().apply(node.getValue().size());
    constructMapping2ndStep(node, mapping);
    return mapping;
  }

  /**
   * Fill the mapping with the data from provided node
   *
   * @param node - the source
   * @param mapping - empty map to be filled
   */
  protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
    List<NodeTuple> nodeValue = node.getValue();
    for (NodeTuple tuple : nodeValue) {
      Node keyNode = tuple.getKeyNode();
      Node valueNode = tuple.getValueNode();
      Object key = constructObject(keyNode);
      if (key != null) {
        try {
          key.hashCode(); // check circular dependencies
        } catch (Exception e) {
          throw new ConstructorException(
              "while constructing a mapping",
              node.getStartMark(),
              "found unacceptable key " + key,
              tuple.getKeyNode().getStartMark(),
              e);
        }
      }
      Object value = constructObject(valueNode);
      if (keyNode.isRecursive()) {
        if (settings.getAllowRecursiveKeys()) {
          postponeMapFilling(mapping, key, value);
        } else {
          throw new YamlEngineException(
              "Recursive key for mapping is detected but it is not configured to be allowed.");
        }
      } else {
        mapping.put(key, value);
      }
    }
  }

  /**
   * if keyObject is created it 2 steps we should postpone putting it in map because it may have
   * different hash after initialization compared to clean just created one. And map of course does
   * not observe key hashCode changes.
   *
   * @param mapping - the mapping to add key/value
   * @param key - the key to add to map
   * @param value - the value behind the key
   */
  protected void postponeMapFilling(Map<Object, Object> mapping, Object key, Object value) {
    maps2fill.add(0, new RecursiveTuple(mapping, new RecursiveTuple(key, value)));
  }

  /**
   * Fill the Map with the data from the node
   *
   * @param node - the source
   * @param set - empty set to fill
   */
  protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
    List<NodeTuple> nodeValue = node.getValue();
    for (NodeTuple tuple : nodeValue) {
      Node keyNode = tuple.getKeyNode();
      Object key = constructObject(keyNode);
      if (key != null) {
        try {
          key.hashCode(); // check circular dependencies
        } catch (Exception e) {
          throw new ConstructorException(
              "while constructing a Set",
              node.getStartMark(),
              "found unacceptable key " + key,
              tuple.getKeyNode().getStartMark(),
              e);
        }
      }
      if (keyNode.isRecursive()) {
        if (settings.getAllowRecursiveKeys()) {
          postponeSetFilling(set, key);
        } else {
          throw new YamlEngineException(
              "Recursive key for mapping is detected but it is not configured to be allowed.");
        }
      } else {
        set.add(key);
      }
    }
  }

  /**
   * if keyObject is created it 2 steps we should postpone putting it into the set because it may
   * have different hash after initialization compared to clean just created one. And set of course
   * does not observe value hashCode changes.
   *
   * @param set - the set to add the key
   * @param key - the item to add to the set
   */
  protected void postponeSetFilling(Set<Object> set, Object key) {
    sets2fill.add(0, new RecursiveTuple<>(set, key));
  }

  private static class RecursiveTuple<T, K> {

    private final T value1;
    private final K value2;

    public RecursiveTuple(T value1, K value2) {
      this.value1 = value1;
      this.value2 = value2;
    }

    public K getValue2() {
      return value2;
    }

    public T getValue1() {
      return value1;
    }
  }
}
