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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions.parser;

import java.text.MessageFormat;

import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;

import static org.kie.workbench.common.services.datamodeller.util.StringEscapeUtils.escapeJavaNonUTFChars;

public class ConditionGenerator {

    private static final String PARAMETER_NULL_EMPTY = "Parameter can not be null nor empty";
    private static final String MISSING_CONDITION_ERROR = "A condition must be provided";
    private static final String FUNCTION_NOT_FOUND_ERROR = "Function {0} was not found in current functions definitions";

    public String generateScript(Condition condition) throws GenerateConditionException {
        if (condition == null) {
            throw new GenerateConditionException(MISSING_CONDITION_ERROR);
        }

        if (!isValidFunction(condition.getFunction())) {
            throw new GenerateConditionException(MessageFormat.format(FUNCTION_NOT_FOUND_ERROR, condition.getFunction()));
        }
        final String function = condition.getFunction().trim();
        final StringBuilder script = new StringBuilder();
        script.append("return ");
        script.append(ConditionParser.KIE_FUNCTIONS);
        script.append(function);
        script.append("(");
        boolean first = true;
        for (String param : condition.getParams()) {
            if (param == null || param.isEmpty()) {
                throw new GenerateConditionException(PARAMETER_NULL_EMPTY);
            }
            if (first) {
                //first parameter is always a process variable name.
                script.append(param);
                first = false;
            } else {
                //the other parameters are always string parameters.
                script.append(", ");
                script.append("\"");
                script.append(escapeJavaNonUTFChars(param));
                script.append("\"");
            }
        }
        script.append(");");
        return script.toString();
    }

    private boolean isValidFunction(String function) {
        return !FunctionsRegistry.getInstance().getFunctions(function).isEmpty();
    }
}
