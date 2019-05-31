/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.rule.RuleManager;

@Dependent
public class BPMNDirectDiagramMarshaller extends BaseDirectDiagramMarshaller {

    @Inject
    public BPMNDirectDiagramMarshaller(
            final XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller,
            final DefinitionManager definitionManager,
            final RuleManager ruleManager,
            final WorkItemDefinitionBackendService workItemDefinitionService,
            final FactoryManager factoryManager,
            final GraphCommandFactory commandFactory,
            final GraphCommandManager commandManager) {

        super(diagramMetadataMarshaller,
              definitionManager,
              ruleManager,
              workItemDefinitionService,
              factoryManager,
              commandFactory,
              commandManager);
    }

    @Override
    protected org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory createToStunnerConverterFactory(
            final DefinitionResolver definitionResolver,
            final TypedFactoryManager typedFactoryManager) {
        return new org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.ConverterFactory(definitionResolver,
                                                                                                       typedFactoryManager);
    }

    @Override
    protected Class<BPMNDefinitionSet> getDefinitionSetClass() {
        return BPMNDefinitionSet.class;
    }

    @Override
    protected Class<?> getDiagramClass() {
        return BPMNDiagramImpl.class;
    }
}
