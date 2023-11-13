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


package org.kie.workbench.common.stunner.bpmn.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serialize/Deserialize a set of {@link String} fields to a String, the delimiter is {@literal ;}
 * and Serialize/Deserialize a set of {@link String} sub-fields to a String, the delimiter is {@literal .}
 */
public class MultipleFieldStringSerializer {

    public static final String FIELD_SEPARATOR = ";";

    public static final String SUBFIELD_SEPARATOR = ".";

    public static final String serialize(String... fields) {
        return Stream.of(fields).collect(Collectors.joining(FIELD_SEPARATOR));
    }

    public static List<String> deserialize(String value) {
        return MultipleFieldStringSerializer.split(value,
                                                   FIELD_SEPARATOR);
    }

    public static final String serializeSubfields(String... fields) {
        return Stream.of(fields).collect(Collectors.joining(SUBFIELD_SEPARATOR));
    }

    public static List<String> deserializeSubfields(String value) {
        return MultipleFieldStringSerializer.split(value,
                                                   SUBFIELD_SEPARATOR);
    }

    private static List<String> split(String input,
                                      String delim) {
        if (Objects.isNull(input)) {
            throw new IllegalArgumentException("Null input");
        }

        if (Objects.isNull(delim)) {
            throw new IllegalArgumentException("Null delimiter");
        }
        return Stream.of(input.split(escape(delim))).collect(Collectors.toList());
    }

    private static String escape(String delim) {
        return "\\" + delim;
    }
}