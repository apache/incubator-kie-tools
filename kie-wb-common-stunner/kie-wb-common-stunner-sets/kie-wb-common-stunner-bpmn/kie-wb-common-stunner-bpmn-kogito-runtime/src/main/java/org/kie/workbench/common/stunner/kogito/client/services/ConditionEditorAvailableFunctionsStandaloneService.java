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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.ConditionEditorAvailableFunctionsService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;

@ApplicationScoped
public class ConditionEditorAvailableFunctionsStandaloneService implements ConditionEditorAvailableFunctionsService {

    @Override
    public Promise<List<FunctionDef>> call(final Input input) {
        return Promise.resolve(getFunctions(input));
    }

    /** Method for getting actual functions list for each type of object
     * @param input - object type.
     * @return - list with available functions.
     */
    private List<FunctionDef> getFunctions(Input input) {
        String definitionClass;
        if (input.clazz.equals(Date.class.getName()) || input.clazz.equals(Object.class.getName())) {
            definitionClass = Object.class.getName();
        } else if (input.clazz.equals(Boolean.class.getName())) {
            definitionClass = Boolean.class.getName();
        } else if (input.clazz.equals(String.class.getName())) {
            definitionClass = String.class.getName();
        } else {
            definitionClass = Number.class.getName();
        }
        List<FunctionDef> functionDefList = new ArrayList<>();
        for (FunctionDef functionDef : FunctionsRegistry.getInstance().getFunctions()) {
            String param = functionDef.getParams().get(0).getType();
            if (param.equals(definitionClass)) {
                functionDefList.add(functionDef);
            }
        }
        return functionDefList;
    }
}
