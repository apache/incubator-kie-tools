/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.yaml.mapper.api.node.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLReadingException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.snakeyaml.engine.v2.api.DumpSettings;

class YamlSequenceNodeImpl implements YamlSequence, Wrappable<List<Object>> {

  private final List<YamlNode> nodes = new ArrayList<>();

  private final DumpSettings settings;

  YamlSequenceNodeImpl(DumpSettings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("unchecked")
  YamlSequenceNodeImpl(DumpSettings settings, List<Object> list) {
    this(settings);
    for (Object l : list) {
      if (l instanceof Map) {
        nodes.add(new YamlMappingNodeImpl((Map<String, Object>) l));
      } else if (l instanceof Iterable) {
        nodes.add(new YamlSequenceNodeImpl(settings, (List<Object>) l));
      } else {
        nodes.add(new YamlScalarNodeImpl(l));
      }
    }
  }

  @Override
  public boolean isEmpty() {
    return nodes.isEmpty();
  }

  @Override
  public NodeType type() {
    return NodeType.SEQUENCE;
  }

  @Override
  public YamlScalar asScalar() throws YAMLReadingException {
    throw new YAMLReadingException("Can't convert sequence to scalar");
  }

  @Override
  public YamlMapping asMapping() throws YAMLReadingException {
    throw new YAMLReadingException("Can't convert sequence to mapping");
  }

  @Override
  public YamlSequence asSequence() throws YAMLReadingException {
    return this;
  }

  @Override
  public int size() {
    return nodes.size();
  }

  @Override
  public Collection<YamlNode> values() {
    return nodes;
  }

  @Override
  public Iterator<YamlNode> iterator() {
    return Collections.unmodifiableCollection(nodes).iterator();
  }

  @Override
  public YamlMapping mapping(int index) {
    if (index < nodes.size() && nodes.get(index).type() == NodeType.MAPPING) {
      return nodes.get(index).asMapping();
    }
    throw new YAMLReadingException("Can't convert sequence to mapping");
  }

  @Override
  public YamlSequence sequence(int index) {
    if (index < nodes.size() && nodes.get(index).type() == NodeType.SEQUENCE) {
      return nodes.get(index).asSequence();
    }
    throw new YAMLReadingException("Can't convert sequence to sequence");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T scalar(int index) {
    if (index < nodes.size() && nodes.get(index).type() == NodeType.SCALAR) {
      return (T) nodes.get(index).asScalar().value();
    }
    throw new YAMLReadingException("Can't convert sequence to string");
  }

  @Override
  public YamlNode node(int index) {
    if (index < nodes.size()) {
      return nodes.get(index);
    }
    throw new YAMLReadingException("Index out of bound");
  }

  @Override
  public YamlNode addNode(YamlNode node) {
    nodes.add(node);
    return node;
  }

  @Override
  public <T> YamlScalar addScalarNode(T value) {
    YamlScalarNodeImpl<T> node = new YamlScalarNodeImpl<>(value);
    nodes.add(node);
    return node;
  }

  @Override
  public YamlMapping addMappingNode() {
    YamlMappingNodeImpl node = new YamlMappingNodeImpl(settings);
    nodes.add(node);
    return node;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Object> unwrap() {
    return nodes.stream()
        .map(node -> ((Wrappable<Object>) node).unwrap())
        .collect(Collectors.toList());
  }
}
