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

import java.util.Optional;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;

public class FlowElementPropertyReader extends BasePropertyReader {

    private final FlowElement flowElement;

    public FlowElementPropertyReader(FlowElement element, BPMNPlane plane, BPMNShape shape) {
        super(element, plane, shape);
        this.flowElement = element;
    }

    public String getName() {
        String extendedName = CustomElement.name.of(element).get();
        return extendedName == null || extendedName.isEmpty() ?
                Optional.ofNullable(flowElement.getName()).orElse("")
                : extendedName;
    }
}
