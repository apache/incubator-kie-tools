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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;

class ProcessVariableReader {

    static String getProcessVariables(List<Property> properties) {
        return properties
                .stream()
                .filter(ProcessVariableReader::isProcessVariable)
                .map(ProcessVariableReader::toProcessVariableString)
                .collect(Collectors.joining(","));
    }

    private static String toProcessVariableString(Property p) {
        String processVariableName = getProcessVariableName(p);

        String tags = CustomElement.customTags.of(p).get();
        return Optional.ofNullable(p.getItemSubjectRef())
                .map(ItemDefinition::getStructureRef)
                .map(type -> processVariableName + ":" + type + ":" + tags)
                .orElse(processVariableName + "::" + tags);
    }

    public static String getProcessVariableName(Property p) {
        String name = p.getName();
        // legacy uses ID instead of name
        return name == null || name.isEmpty() ? p.getId() : name;
    }

    public static boolean isProcessVariable(Property p) {
        return !CaseFileVariableReader.isCaseFileVariable(p);
    }
}
