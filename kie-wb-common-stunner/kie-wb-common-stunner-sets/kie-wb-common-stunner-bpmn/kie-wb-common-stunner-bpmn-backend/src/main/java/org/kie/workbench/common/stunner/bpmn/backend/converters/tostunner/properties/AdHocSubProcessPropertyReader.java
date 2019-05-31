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

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

public class AdHocSubProcessPropertyReader extends SubProcessPropertyReader {

    private final AdHocSubProcess process;

    public AdHocSubProcessPropertyReader(AdHocSubProcess element, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(element, diagram, definitionResolver);
        this.process = element;
    }

    public ScriptTypeValue getAdHocCompletionCondition() {
        if (process.getCompletionCondition() instanceof FormalExpression) {
            FormalExpression completionCondition = (FormalExpression) process.getCompletionCondition();
            return new ScriptTypeValue(
                    Scripts.scriptLanguageFromUri(completionCondition.getLanguage(), Scripts.LANGUAGE.MVEL.language()),
                    completionCondition.getBody()
            );
        } else {
            return new ScriptTypeValue(Scripts.LANGUAGE.MVEL.language(), "autocomplete");
        }
    }

    public String getAdHocOrdering() {
        return process.getOrdering().toString();
    }

    public boolean isAdHocAutostart() {
        return CustomElement.autoStart.of(element).get();
    }
}
