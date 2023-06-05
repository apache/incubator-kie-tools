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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLReadingException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlNode;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;

class YamlMappingNodeImpl implements YamlMapping, Wrappable<Map<String, Object>> {

  private final DumpSettings settings;

  private final Map<String, YamlNode> holder = new LinkedHashMap<>();

  YamlMappingNodeImpl() {
    this(DumpSettings.builder().build());
  }

  YamlMappingNodeImpl(DumpSettings settings) {
    this.settings = settings;
  }

  YamlMappingNodeImpl(Map<String, Object> map) {
    this(DumpSettings.builder().build(), map);
  }

  @SuppressWarnings("unchecked")
  YamlMappingNodeImpl(DumpSettings settings, Map<String, Object> map) {
    this(settings);
    if (map != null) {
      for (Entry<String, Object> entry : map.entrySet()) {
        String k = entry.getKey();
        Object v = entry.getValue();
        if (v instanceof Map) {
          holder.put(k, new YamlMappingNodeImpl(settings, (Map<String, Object>) v));
        } else if (v instanceof Iterable) {
          holder.put(k, new YamlSequenceNodeImpl(settings, (List<Object>) v));
        } else {
          holder.put(k, new YamlScalarNodeImpl(v));
        }
      }
    }
  }

  @Override
  public Collection<String> keys() {
    return new HashSet<>(holder.keySet());
  }

  @Override
  public Collection<YamlNode> values() {
    return Collections.unmodifiableCollection(holder.values());
  }

  @Override
  public YamlMapping getMappingNode(String key) {
    if (holder.containsKey(key)) {
      return holder.get(key).asMapping();
    }
    throw new YAMLReadingException("Key " + key + " not found");
  }

  @Override
  public YamlSequence getSequenceNode(String key) {
    if (holder.containsKey(key)) {
      return holder.get(key).asSequence();
    }
    throw new YAMLReadingException("Key " + key + " not found");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> YamlScalar<T> getScalarNode(String key) {
    if (holder.containsKey(key)) {
      return (YamlScalar<T>) holder.get(key).asScalar();
    }
    throw new YAMLReadingException("Key " + key + " not found");
  }

  @Override
  public YamlNode getNode(String key) {
    if (holder.containsKey(key)) {
      return holder.get(key);
    }
    throw new YAMLReadingException("Key " + key + " not found");
  }

  @Override
  public YamlNode addNode(String key, YamlNode node) {
    holder.put(key, node);
    return node;
  }

  @Override
  public <T> YamlScalar<T> addScalarNode(String key, T value) {
    YamlScalarNodeImpl<T> node = new YamlScalarNodeImpl<>(value);
    holder.put(key, node);
    return node;
  }

  @Override
  public YamlSequence addSequenceNode(String key) {
    YamlSequenceNodeImpl node = new YamlSequenceNodeImpl(settings);
    holder.put(key, node);
    return node;
  }

  @Override
  public YamlMapping addMappingNode(String key) {
    YamlMappingNodeImpl node = new YamlMappingNodeImpl(settings);
    holder.put(key, node);
    return node;
  }

  @Override
  public boolean isEmpty() {
    return holder.isEmpty();
  }

  @Override
  public NodeType type() {
    return NodeType.MAPPING;
  }

  @Override
  public <T> YamlScalar<T> asScalar() throws YAMLReadingException {
    throw new YAMLReadingException("Node is not scalar");
  }

  @Override
  public YamlMapping asMapping() throws YAMLReadingException {
    return this;
  }

  @Override
  public YamlSequence asSequence() throws YAMLReadingException {
    throw new YAMLReadingException("Node is not sequence");
  }

  @Override
  public String toString() {
    if (holder.isEmpty()) {
      return "";
    }
    return new Dump(settings).dumpToString(unwrap()).trim();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> unwrap() {
    Map<String, Object> unwapped = new LinkedHashMap<>();
    for (Entry<String, YamlNode> entry : holder.entrySet()) {
      unwapped.put(entry.getKey(), ((Wrappable<Object>) entry.getValue()).unwrap());
    }
    return unwapped;
  }
}
