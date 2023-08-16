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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;
import org.kie.workbench.common.forms.processing.engine.handling.CustomFieldValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ValidationResult;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.ALPHA_NUM_REGEXP;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class MultipleInstanceVariableValidator implements CustomFieldValidator<String> {

    static final String INPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR = "MultipleInstanceVariableValidator.InputAssignmentAlreadyExistsError";
    static final String OUTPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR = "MultipleInstanceVariableValidator.OutputAssignmentAlreadyExistsError";
    static final String INVALID_VARIABLE_NAME_ERROR = "MultipleInstanceVariableValidator.InvalidVariableNameError";

    private static final RegExp ENCODED_PARTS_DELIMITER = RegExp.compile("\\|");
    private static final RegExp ENCODED_NAMES_DELIMITER = RegExp.compile(",");
    private static final RegExp ENCODED_NAME_DELIMITER = RegExp.compile(":");
    private static final RegExp VARIABLE_NAME = RegExp.compile(ALPHA_NUM_REGEXP);

    private final BPMNDefinition definition;

    private final ClientTranslationService translationService;

    public MultipleInstanceVariableValidator(Node<View<BPMNDefinition>, Edge> node,
                                             ClientTranslationService translationService) {
        this.definition = node != null && node.getContent() != null ? node.getContent().getDefinition() : null;
        this.translationService = translationService;
    }

    @Override
    public ValidationResult validate(String value) {
        ValidationParams params = null;
        if (definition instanceof UserTask) {
            params = buildValidationParams((UserTask) definition);
        } else if (definition instanceof ReusableSubprocess) {
            params = buildValidationParams((ReusableSubprocess) definition);
        } else if (definition instanceof MultipleInstanceSubprocess) {
            params = new ValidationParams("");
        }
        if (params == null) {
            return ValidationResult.valid();
        }
        String variableName = getVariableName(value);
        if (!VARIABLE_NAME.test(variableName)) {
            return ValidationResult.error(translationService.getValue(INVALID_VARIABLE_NAME_ERROR));
        }
        Set<String> inputNames = decodeInputNames(params.getEncodedAssignmentsInfo());
        Set<String> outputNames = decodeOutputNames(params.getEncodedAssignmentsInfo());
        if (inputNames.contains(variableName)) {
            return ValidationResult.error(translationService.getValue(INPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR, value));
        } else if (outputNames.contains(variableName)) {
            return ValidationResult.error(translationService.getValue(OUTPUT_ASSIGNMENT_ALREADY_EXISTS_ERROR, value));
        }
        return ValidationResult.valid();
    }

    private String getVariableName(String value) {
        return value.split(ENCODED_NAME_DELIMITER.getSource())[0];
    }

    private static ValidationParams buildValidationParams(UserTask userTask) {
        return new ValidationParams(userTask.getExecutionSet().getAssignmentsinfo().getValue());
    }

    private static ValidationParams buildValidationParams(ReusableSubprocess subProcess) {
        return new ValidationParams(subProcess.getDataIOSet().getAssignmentsinfo().getValue());
    }

    private static Set<String> decodeInputNames(String encodedAssignments) {
        if (encodedAssignments == null) {
            return Collections.emptySet();
        }
        SplitResult encodedParts = ENCODED_PARTS_DELIMITER.split(encodedAssignments);
        if (encodedParts.length() > 1) {
            return extractNames(encodedParts.get(1));
        }
        return Collections.emptySet();
    }

    private static Set<String> decodeOutputNames(String encodedAssignments) {
        if (encodedAssignments == null) {
            return Collections.emptySet();
        }
        SplitResult encodedParts = ENCODED_PARTS_DELIMITER.split(encodedAssignments);
        if (encodedParts.length() > 3) {
            return extractNames(encodedParts.get(3));
        }
        return Collections.emptySet();
    }

    private static Set<String> extractNames(String encodedNames) {
        Set<String> result = new HashSet<>();
        if (!isEmpty(encodedNames)) {
            SplitResult encodedNamesSplit = ENCODED_NAMES_DELIMITER.split(encodedNames);
            SplitResult nameSplit;
            for (int i = 0; i < encodedNamesSplit.length(); i++) {
                nameSplit = ENCODED_NAME_DELIMITER.split(encodedNamesSplit.get(i));
                if (nameSplit.length() > 0) {
                    result.add(nameSplit.get(0));
                }
            }
        }
        return result;
    }

    private static class ValidationParams {

        private String encodedAssignmentsInfo;

        public ValidationParams(String encodedAssignmentsInfo) {
            this.encodedAssignmentsInfo = encodedAssignmentsInfo;
        }

        public String getEncodedAssignmentsInfo() {
            return encodedAssignmentsInfo;
        }
    }
}
