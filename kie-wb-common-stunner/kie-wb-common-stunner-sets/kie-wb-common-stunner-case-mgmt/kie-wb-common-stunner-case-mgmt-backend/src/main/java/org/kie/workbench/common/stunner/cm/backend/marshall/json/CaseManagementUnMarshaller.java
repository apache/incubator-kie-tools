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
package org.kie.workbench.common.stunner.cm.backend.marshall.json;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.Bpmn2UnMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CaseManagementUnMarshaller extends Bpmn2UnMarshaller {

    public CaseManagementUnMarshaller(GraphObjectBuilderFactory elementBuilderFactory,
                                      DefinitionManager definitionManager,
                                      FactoryManager factoryManager,
                                      DefinitionsCacheRegistry definitionsCacheRegistry,
                                      RuleManager ruleManager,
                                      OryxManager oryxManager,
                                      CommandManager<GraphCommandExecutionContext, RuleViolation> commandManager,
                                      GraphCommandFactory commandFactory,
                                      GraphIndexBuilder<?> indexBuilder,
                                      Class<?> diagramDefinitionSetClass,
                                      Class<? extends BPMNDiagram> diagramDefinitionClass) {
        super(elementBuilderFactory,
              definitionManager,
              factoryManager,
              definitionsCacheRegistry,
              ruleManager,
              oryxManager,
              commandManager,
              commandFactory,
              indexBuilder,
              diagramDefinitionSetClass,
              diagramDefinitionClass);
    }

    @Override
    protected void marshallReusableSubprocessNode(CallActivity node,
                                                  BPMNPlane plane,
                                                  JsonGenerator generator,
                                                  float xOffset,
                                                  float yOffset,
                                                  Map<String, Object> properties) throws IOException {
        marshallAutoStart(node, properties);

        String stencil = isCase(node) ? "CaseReusableSubprocess" : "ProcessReusableSubprocess";

        doMarshallNode(node, properties, stencil, plane, generator, xOffset, yOffset);
    }

    @Override
    protected void setAdHocSubProcessProperties(AdHocSubProcess subProcess, Map<String, Object> properties) {
        marshallAutoStart(subProcess, properties);

        doSetAdHocSubProcessProperties(subProcess, properties);
    }

    void doMarshallNode(FlowNode node,
                        Map<String, Object> properties,
                        String stencil,
                        BPMNPlane plane,
                        JsonGenerator generator,
                        float xOffset,
                        float yOffset) throws IOException {
        super.marshallNode(node, properties, stencil, plane, generator, xOffset, yOffset);
    }

    void doSetAdHocSubProcessProperties(AdHocSubProcess subProcess, Map<String, Object> properties) {
        super.setAdHocSubProcessProperties(subProcess, properties);
    }

    private boolean isCase(FlowNode node) {
        return Boolean.valueOf(Utils.getMetaDataValue(node.getExtensionValues(), "case"));
    }

    private void marshallAutoStart(FlowNode node, Map<String, Object> properties) {
        // custom autostart
        String customAutoStartMetaData = Utils.getMetaDataValue(node.getExtensionValues(), "customAutoStart");
        String customAutoStart = (customAutoStartMetaData != null && customAutoStartMetaData.length() > 0) ?
                customAutoStartMetaData : "false";
        properties.put("customautostart", customAutoStart);
    }
}
