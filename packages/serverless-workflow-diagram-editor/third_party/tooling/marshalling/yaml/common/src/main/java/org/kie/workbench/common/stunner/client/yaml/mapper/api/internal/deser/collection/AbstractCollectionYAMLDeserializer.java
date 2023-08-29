/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.yaml.mapper.api.internal.deser.collection;

import java.util.AbstractCollection;
import java.util.ArrayList;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Default {@link YAMLDeserializer} implementation for {@link java.util.AbstractCollection}. The
 * deserialization process returns an {@link java.util.ArrayList}.
 *
 * @param <T> Type of the elements inside the {@link java.util.AbstractCollection}
 * @author Nicolas Morel
 * @version $Id: $
 */
public class AbstractCollectionYAMLDeserializer<T>
    extends BaseCollectionYAMLDeserializer<AbstractCollection<T>, T> {

  /**
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     AbstractCollection}.
   */
  private AbstractCollectionYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }

  /**
   * newInstance
   *
   * @param deserializer {@link YAMLDeserializer} used to deserialize the objects inside the {@link
   *     java.util.AbstractCollection}.
   * @param <T> Type of the elements inside the {@link java.util.AbstractCollection}
   * @return a new instance of {@link AbstractCollectionYAMLDeserializer}
   */
  public static <T> AbstractCollectionYAMLDeserializer<T> newInstance(
      YAMLDeserializer<T> deserializer) {
    return new AbstractCollectionYAMLDeserializer<>(deserializer);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractCollection<T> newCollection() {
    return new ArrayList<>();
  }
}
