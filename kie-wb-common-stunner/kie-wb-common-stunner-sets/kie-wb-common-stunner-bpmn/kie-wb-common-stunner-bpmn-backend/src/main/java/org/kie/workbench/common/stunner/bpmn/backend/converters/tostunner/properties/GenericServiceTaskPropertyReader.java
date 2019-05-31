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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;

public class GenericServiceTaskPropertyReader extends TaskPropertyReader {

    private final ServiceTask task;

    public GenericServiceTaskPropertyReader(ServiceTask task, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.task = task;
    }

    public GenericServiceTaskValue getGenericServiceTask() {
        GenericServiceTaskValue value = new GenericServiceTaskValue();
        if (CustomAttribute.serviceImplementation.of(task).get() != null) {
            String candidate = CustomAttribute.serviceImplementation.of(task).get();
            if (!candidate.equals("Java")) {
                candidate = "WebService";
            }
            value.setServiceImplementation(candidate);
        }
        if (CustomAttribute.serviceOperation.of(task).get() != null) {
            value.setServiceOperation(CustomAttribute.serviceOperation.of(task).get());
        }
        if (CustomAttribute.serviceInterface.of(task).get() != null) {
            value.setServiceInterface(CustomAttribute.serviceInterface.of(task).get());
        }
        return value;
    }
}
