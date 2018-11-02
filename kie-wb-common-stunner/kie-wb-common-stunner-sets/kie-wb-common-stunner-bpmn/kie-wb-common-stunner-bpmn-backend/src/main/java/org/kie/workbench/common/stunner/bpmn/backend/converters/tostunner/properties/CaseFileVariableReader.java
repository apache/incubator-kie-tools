/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;

class CaseFileVariableReader {

    static String getCaseFileVariables(List<Property> properties) {
        return properties
                .stream()
                .filter(CaseFileVariableReader::isCaseFileVariable)
                .map(CaseFileVariableReader::toCaseFileVariableString)
                .collect(Collectors.joining(","));
    }

    private static String toCaseFileVariableString(Property p) {
        String variableName = getCaseFileVariableName(p);
        String caseFileVariableName = variableName.substring(CaseFileVariables.CASE_FILE_PREFIX.length());

        return Optional.ofNullable(p.getItemSubjectRef())
                .map(ItemDefinition::getStructureRef)
                .map(type -> caseFileVariableName + ":" + type)
                .orElse(caseFileVariableName);
    }

    private static String getCaseFileVariableName(Property p) {
        String name = p.getName();
        // legacy uses ID instead of name
        return name == null ? p.getId() : name;
    }

    public static boolean isCaseFileVariable(Property p) {
        String name = getCaseFileVariableName(p);
        return name.startsWith(CaseFileVariables.CASE_FILE_PREFIX);
    }
}
