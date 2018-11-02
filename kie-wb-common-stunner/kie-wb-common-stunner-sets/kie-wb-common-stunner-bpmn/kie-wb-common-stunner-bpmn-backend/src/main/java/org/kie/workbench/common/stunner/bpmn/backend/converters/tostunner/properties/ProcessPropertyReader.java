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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;

public class ProcessPropertyReader extends BasePropertyReader {

    private final Process process;
    private final Map<String, FlowElement> flowElements;

    public ProcessPropertyReader(Process element, BPMNPlane plane, BPMNShape shape) {
        super(element, plane, shape);
        this.process = element;
        this.flowElements =
                process.getFlowElements().stream()
                        .collect(Collectors.toMap(FlowElement::getId, Function.identity()));
    }

    public String getPackage() {
        return CustomAttribute.packageName.of(element).get();
    }

    public String getVersion() {
        return CustomAttribute.version.of(element).get();
    }

    public boolean isAdHoc() {
        return CustomAttribute.adHoc.of(element).get();
    }

    @Override
    public Bounds getBounds() {
        return new BoundsImpl(
                new BoundImpl(0d, 0d),
                new BoundImpl(950d, 950d));
    }

    public String getProcessVariables() {
        return ProcessVariableReader.getProcessVariables(process.getProperties());
    }

    public String getCaseFileVariables() {
        return CaseFileVariableReader.getCaseFileVariables(process.getProperties());
    }

    public String getCaseRoles() {
        return CustomElement.caseRole.of(process).get();
    }

    public FlowElement getFlowElement(String id) {
        return flowElements.get(id);
    }
}
