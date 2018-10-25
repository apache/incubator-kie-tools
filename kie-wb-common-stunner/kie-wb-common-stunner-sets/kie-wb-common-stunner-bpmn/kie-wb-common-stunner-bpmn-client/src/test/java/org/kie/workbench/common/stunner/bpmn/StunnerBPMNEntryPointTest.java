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

package org.kie.workbench.common.stunner.bpmn;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.filters.BPMNDiagramFilterProvider;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.formFilters.FormFiltersProviderFactory;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerBPMNEntryPointTest {

    private static final String UUID = "uuid";

    private StunnerBPMNEntryPoint tested;

    @Mock
    private SessionManager sessionManager;

    private ManagedInstance<StunnerFormElementFilterProvider> managedFilters;

    @Mock
    private BPMNDiagramFilterProvider bpmnDiagramFilterProvider;

    @Mock
    private Element<? extends Definition<?>> element;

    private BPMNDiagramImpl diagramDef;

    @Before
    public void setUp() throws Exception {
        diagramDef = new BPMNDiagramImpl();
        when(bpmnDiagramFilterProvider.getDefinitionType()).thenReturn((Class) BPMNDiagramImpl.class);
        managedFilters = new ManagedInstanceStub(bpmnDiagramFilterProvider);
        tested = new StunnerBPMNEntryPoint(sessionManager, managedFilters);
    }

    @Test
    public void init() {
        tested.init();
        FormFiltersProviderFactory.getFilterForDefinition(UUID, diagramDef);
        verify(bpmnDiagramFilterProvider).provideFilters(UUID, diagramDef);
    }
}

