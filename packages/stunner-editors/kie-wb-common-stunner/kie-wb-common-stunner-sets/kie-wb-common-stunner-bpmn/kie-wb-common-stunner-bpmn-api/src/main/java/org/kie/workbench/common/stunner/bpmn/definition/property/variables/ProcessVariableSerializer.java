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


package org.kie.workbench.common.stunner.bpmn.definition.property.variables;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessVariableSerializer {

    public static Map<String, VariableInfo> deserialize(String serializedVariables) {
        return Stream.of(serializedVariables.split(","))
                .filter(s -> !s.isEmpty()) // "" makes no sense
                .map(ProcessVariableSerializer::deserializeVariable)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Map.Entry<String, VariableInfo> deserializeVariable(String encoded) {
        String[] split = encoded.split(":");
        String identifier = split[0];
        String type = (split.length == 2 || split.length == 3) ? split[1] : "";
        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("Variable identifier cannot be empty. Given: '" + encoded + "'");
        }

        String tags = split[2];
        return new AbstractMap.SimpleEntry<>(identifier, new ProcessVariableSerializer.VariableInfo(type, tags));
    }

    static public class VariableInfo {

        public final String type;
        public final String tags;

        VariableInfo(final String type, final String tags) {
            this.type = type;
            this.tags = tags;
        }

        public String getType() {
            return type;
        }

        public String getTags() {
            return tags;
        }
    }
}
