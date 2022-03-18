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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Optional;

import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

public class ScriptTaskPropertyReader extends TaskPropertyReader {

    private final ScriptTask task;

    public ScriptTaskPropertyReader(ScriptTask task, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.task = task;
    }

    public ScriptTypeValue getScript() {
        return new ScriptTypeValue(
                Scripts.scriptLanguageFromUri(task.getScriptFormat(), Scripts.LANGUAGE.JAVA.language()),
                Optional.ofNullable(task.getScript()).orElse(null)
        );
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutoStart() {
        return CustomElement.autoStart.of(element).get();
    }
}
