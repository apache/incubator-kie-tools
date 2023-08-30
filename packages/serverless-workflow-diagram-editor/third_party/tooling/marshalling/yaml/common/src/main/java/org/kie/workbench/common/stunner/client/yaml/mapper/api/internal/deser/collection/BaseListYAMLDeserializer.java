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

import java.util.List;

import org.kie.workbench.common.stunner.client.yaml.mapper.api.YAMLDeserializer;

/**
 * Base {@link YAMLDeserializer} implementation for {@link java.util.List}.
 *
 * @param <L> {@link java.util.List} type
 * @param <T> Type of the elements inside the {@link java.util.List}
 * @author Nicolas Morel
 * @version $Id: $
 */
public abstract class BaseListYAMLDeserializer<L extends List<T>, T>
    extends BaseCollectionYAMLDeserializer<L, T> {

  /**
   * Constructor for BaseListYAMLDeserializer.
   *
   * @param deserializer {@link YAMLDeserializer} used to map the objects inside the {@link
   *     java.util.List}.
   */
  public BaseListYAMLDeserializer(YAMLDeserializer<T> deserializer) {
    super(deserializer);
  }
}
