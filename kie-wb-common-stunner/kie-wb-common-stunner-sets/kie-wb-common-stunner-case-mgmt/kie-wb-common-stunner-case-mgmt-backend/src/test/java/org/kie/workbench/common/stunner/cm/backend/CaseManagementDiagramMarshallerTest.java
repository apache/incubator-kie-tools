/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.GraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.OryxManager;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDiagramMarshallerTest {

    @Mock
    private XMLEncoderDiagramMetadataMarshaller diagramMetadataMarshaller;

    @Mock
    private GraphObjectBuilderFactory graphBuilderFactory;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private GraphIndexBuilder<?> indexBuilder;

    @Mock
    private OryxManager oryxManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private GraphCommandManager graphCommandManager;

    @Mock
    private GraphCommandFactory commandFactory;

    @Mock
    private RuleManager rulesManager;

    private CaseManagementDiagramMarshaller marshaller;

    @Before
    public void setup() {
        this.marshaller = new CaseManagementDiagramMarshaller(diagramMetadataMarshaller,
                                                              graphBuilderFactory,
                                                              definitionManager,
                                                              indexBuilder,
                                                              oryxManager,
                                                              factoryManager,
                                                              rulesManager,
                                                              graphCommandManager,
                                                              commandFactory);
    }

    @Test
    public void getDiagramDefinitionSetClass() {
        // It is important that CaseManagementDiagramMarshaller declares it relates to the CaseManagementDefinitionSet
        // otherwise all sorts of things break. This test attempts to drawer the importance of this to future changes
        // should someone decide to change the apparent innocuous method in CaseManagementDiagramMarshaller.
        assertEquals(CaseManagementDefinitionSet.class,
                     marshaller.getDiagramDefinitionSetClass());
    }
}
