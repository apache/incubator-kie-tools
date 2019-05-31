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

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

public class CallActivityPropertyReader extends MultipleInstanceActivityPropertyReader {

    protected final CallActivity callActivity;

    public CallActivityPropertyReader(CallActivity callActivity, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(callActivity, diagram, definitionResolver);
        this.callActivity = callActivity;
    }

    public String getCalledElement() {
        return callActivity.getCalledElement();
    }

    public boolean isIndependent() {
        return CustomAttribute.independent.of(element).get();
    }

    public boolean isWaitForCompletion() {
        return CustomAttribute.waitForCompletion.of(element).get();
    }

    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isCase() {
        return CustomElement.isCase.of(element).get();
    }

    public boolean isAdHocAutostart() {
        return CustomElement.autoStart.of(element).get();
    }
}
