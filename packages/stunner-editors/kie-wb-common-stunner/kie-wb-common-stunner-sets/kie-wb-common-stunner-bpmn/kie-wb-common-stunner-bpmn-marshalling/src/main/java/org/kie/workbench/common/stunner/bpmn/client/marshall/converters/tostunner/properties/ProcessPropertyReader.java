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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public class ProcessPropertyReader extends BasePropertyReader {

    private final Process process;
    private final Map<String, FlowElement> flowElements;

    public ProcessPropertyReader(Process element, BPMNDiagram diagram, BPMNShape shape, double resolutionFactor) {
        super(element, diagram, shape, resolutionFactor);
        this.process = element;
        this.flowElements =
                process.getFlowElements().stream()
                        .collect(Collectors.toMap(FlowElement::getId, Function.identity()));
    }

    public String getPackage() {
        return CustomAttribute.packageName.of(element).get();
    }

    public String getProcessType() {
        return process.getProcessType().getName();
    }

    public String getVersion() {
        return CustomAttribute.version.of(element).get();
    }

    public boolean isAdHoc() {
        return CustomAttribute.adHoc.of(element).get();
    }

    @Override
    public Bounds getBounds() {
        return Bounds.create(0d, 0d, 950d, 950d);
    }

    public String getProcessVariables() {
        return ProcessVariableReader.getProcessVariables(process.getProperties());
    }

    public String getCaseIdPrefix() {
        return CustomElement.caseIdPrefix.of(process).get();
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

    public String getGlobalVariables() {
        return CustomElement.globalVariables.of(process).get();
    }

    public List<DefaultImport> getDefaultImports() {
        return CustomElement.defaultImports.of(process).get();
    }

    public String getSlaDueDate() {
        return CustomElement.slaDueDate.of(process).get();
    }
}
