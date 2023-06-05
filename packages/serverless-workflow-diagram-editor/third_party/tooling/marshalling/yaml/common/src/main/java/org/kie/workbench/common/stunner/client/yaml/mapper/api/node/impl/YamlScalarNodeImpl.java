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

import org.kie.workbench.common.stunner.client.yaml.mapper.api.exception.YAMLReadingException;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.NodeType;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlMapping;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlScalar;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.node.YamlSequence;

class YamlScalarNodeImpl<T> implements YamlScalar<T>, Wrappable<T> {

  private final T value;

  YamlScalarNodeImpl(T value) {
    this.value = value;
  }

  @Override
  public boolean isEmpty() {
    return value == null;
  }

  @Override
  public NodeType type() {
    return NodeType.SCALAR;
  }

  @Override
  public YamlScalar<T> asScalar() throws YAMLReadingException {
    return this;
  }

  @Override
  public YamlMapping asMapping() throws YAMLReadingException {
    throw new YAMLReadingException("Can't convert scalar to mapping");
  }

  @Override
  public YamlSequence asSequence() throws YAMLReadingException {
    throw new YAMLReadingException("Can't convert scalar to sequence");
  }

  @Override
  public T value() {
    return value;
  }

  @Override
  public T unwrap() {
    return value;
  }
}
