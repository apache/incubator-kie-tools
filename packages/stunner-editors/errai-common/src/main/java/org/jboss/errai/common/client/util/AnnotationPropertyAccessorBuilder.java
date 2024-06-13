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

package org.jboss.errai.common.client.util;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class AnnotationPropertyAccessorBuilder {
  private final SortedMap<String, Function<Annotation, String>> accessorsByPropertyName = new TreeMap<>();

  private AnnotationPropertyAccessorBuilder() {}

  public static AnnotationPropertyAccessorBuilder create() {
    return new AnnotationPropertyAccessorBuilder();
  }

  public AnnotationPropertyAccessorBuilder with(final String propertyName, final Function<Annotation, String> accessor) {
    accessorsByPropertyName.put(propertyName, accessor);

    return this;
  }

  public AnnotationPropertyAccessor build() {
    return new AnnotationPropertyAccessor(createOrderedPropertyMap());
  }

  private Map<String, Function<Annotation, String>> createOrderedPropertyMap() {
    return (accessorsByPropertyName.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(accessorsByPropertyName));
  }
}