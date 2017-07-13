/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.LienzoToolboxFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public abstract class BaseDMNFlowActionsToolboxControlProviderTest {

    @Mock
    protected DefinitionManager definitionManager;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected FactoryManager factoryManager;

    @Mock
    protected ToolboxCommandFactory defaultToolboxCommandFactory;

    @Mock
    protected CommonLookups commonLookups;

    @Mock
    protected TypeDefinitionSetRegistry typeDefinitionSetRegistry;

    @Mock
    protected AdapterManager adapterManager;

    @Mock
    protected DefinitionSetAdapter definitionSetAdapter;

    @Mock
    protected DefinitionAdapter definitionAdapter;

    protected ToolboxFactory toolboxFactory;

    protected AbstractToolboxControlProvider provider;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(typeDefinitionSetRegistry);
        when(typeDefinitionSetRegistry.getDefinitionSetByType(eq(DMNDefinitionSet.class))).thenReturn(new DMNDefinitionSet());
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionSetAdapter.getDefinitions(any(DMNDefinitionSet.class))).thenReturn(new HashSet<String>() {{
            add(DMNDiagram.class.getName());
            add(InputData.class.getName());
            add(KnowledgeSource.class.getName());
            add(BusinessKnowledgeModel.class.getName());
            add(Decision.class.getName());
            add(TextAnnotation.class.getName());
            add(Association.class.getName());
            add(InformationRequirement.class.getName());
            add(KnowledgeRequirement.class.getName());
            add(AuthorityRequirement.class.getName());
        }});
        when(definitionAdapter.getId(anyObject())).thenAnswer((o) -> o.getArguments()[0].getClass().getName());

        this.toolboxFactory = new LienzoToolboxFactory();

        this.provider = getProvider();
    }

    protected abstract AbstractToolboxControlProvider getProvider();

    protected abstract void doAssertion(final boolean supports);

    @Test
    public void checkSupportsDMNDiagram() {
        doAssertion(provider.supports(new DMNDiagram.DMNDiagramBuilder().build()));
    }

    @Test
    public void checkSupportsInputData() {
        doAssertion(provider.supports(new InputData.InputDataBuilder().build()));
    }

    @Test
    public void checkSupportsKnowledgeSource() {
        doAssertion(provider.supports(new KnowledgeSource.KnowledgeSourceBuilder().build()));
    }

    @Test
    public void checkSupportsBusinessKnowledgeModel() {
        doAssertion(provider.supports(new BusinessKnowledgeModel.BusinessKnowledgeModelBuilder().build()));
    }

    @Test
    public void checkSupportsDecision() {
        doAssertion(provider.supports(new Decision.DecisionBuilder().build()));
    }

    @Test
    public void checkSupportsTextAnnotation() {
        doAssertion(provider.supports(new TextAnnotation.TextAnnotationBuilder().build()));
    }

    @Test
    public void checkSupportsAssociation() {
        doAssertion(provider.supports(new Association.AssociationBuilder().build()));
    }

    @Test
    public void checkSupportsInformationRequirement() {
        doAssertion(provider.supports(new InformationRequirement.InformationRequirementBuilder().build()));
    }

    @Test
    public void checkSupportsKnowledgeRequirement() {
        doAssertion(provider.supports(new KnowledgeRequirement.KnowledgeRequirementBuilder().build()));
    }

    @Test
    public void checkSupportsAuthorityRequirement() {
        doAssertion(provider.supports(new AuthorityRequirement.AuthorityRequirementBuilder().build()));
    }
}
