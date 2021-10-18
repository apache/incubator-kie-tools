/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.services;

import javax.enterprise.context.ApplicationScoped;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionEditorGeneratorService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;

@ApplicationScoped
public class ConditionEditorGeneratorStandaloneService implements ConditionEditorGeneratorService {

    @Override
    public Promise<GenerateConditionResult> call(final Condition condition) {
        return Promise.resolve(new GenerateConditionResult(generateScript(condition)));
    }

    /** Condition generator method for the text condition editor
     * @param condition - condition from the graphical editor
     * @return - condition string value for the text editor
     */
    public String generateScript(Condition condition) {
        String function = condition.getFunction().trim();
        final StringBuilder script = new StringBuilder();
        script.append("return ");
        script.append("KieFunctions.");
        script.append(function);
        script.append("(");
        boolean first = true;
        for (String param : condition.getParams()) {
            if (param == null || param.isEmpty()) {
                throw new RuntimeException("Parameter is empty");
            }
            if (first) {
                script.append(param);
                first = false;
             } else {
                script.append(", ");
                script.append("\"");
                script.append(param);
                script.append("\"");
            }
        }
        script.append(");");
        return script.toString();
    }
}
