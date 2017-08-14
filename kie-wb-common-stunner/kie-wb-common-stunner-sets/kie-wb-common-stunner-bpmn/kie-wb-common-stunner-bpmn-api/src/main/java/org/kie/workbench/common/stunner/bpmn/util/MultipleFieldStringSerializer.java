/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serialize/Deserialize a set of {@link String} fields to a String.
 */
public class MultipleFieldStringSerializer {

  public static final String SEPARATOR = ";";

  public static final String serialize(String... fields) {
    return Stream.of(fields).collect(Collectors.joining(SEPARATOR));
  }

  public static List<String> deserialize(String value) {
    return MultipleFieldStringSerializer.split(value,
                             SEPARATOR);
  }

  private static List<String> split(String input, String delim){
    if(Objects.isNull(input)){
      throw new IllegalArgumentException("Null input");
    }

    if(Objects.isNull(delim)){
      throw new IllegalArgumentException("Null delimiter");
    }
    return Stream.of(input.split(delim)).collect(Collectors.toList());
  }
}
