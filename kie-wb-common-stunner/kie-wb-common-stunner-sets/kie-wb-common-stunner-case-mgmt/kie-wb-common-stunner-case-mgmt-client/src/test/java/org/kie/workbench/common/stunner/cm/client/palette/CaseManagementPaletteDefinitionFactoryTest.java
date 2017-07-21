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
package org.kie.workbench.common.stunner.cm.client.palette;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementPaletteDefinitionFactoryTest {

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private DefinitionSetPaletteBuilder paletteBuilder;

    @Mock
    private BS3PaletteWidget palette;

    private CaseManagementPaletteDefinitionFactory factory;

    @Before
    public void setup() {
        factory = new CaseManagementPaletteDefinitionFactory(shapeManager,
                                                             paletteBuilder,
                                                             palette);
    }

    @Test
    public void assertDefinitionSetType() {
        assertEquals(CaseManagementDefinitionSet.class,
                     factory.getDefinitionSetType());
    }

    @Test
    public void checkBuilderConfiguration() {
        factory.configureBuilder();

        verify(paletteBuilder).excludeDefinition(CaseManagementDiagram.class.getName());
        verify(paletteBuilder).excludeDefinition(Lane.class.getName());
        verify(paletteBuilder).excludeDefinition(NoneTask.class.getName());
        verify(paletteBuilder).excludeDefinition(StartNoneEvent.class.getName());
        verify(paletteBuilder).excludeDefinition(EndNoneEvent.class.getName());
        verify(paletteBuilder).excludeDefinition(EndTerminateEvent.class.getName());
        verify(paletteBuilder).excludeDefinition(ParallelGateway.class.getName());
        verify(paletteBuilder).excludeDefinition(ExclusiveDatabasedGateway.class.getName());
        verify(paletteBuilder).excludeDefinition(SequenceFlow.class.getName());

        verify(paletteBuilder).excludeCategory(Categories.EVENTS);
        verify(paletteBuilder).excludeCategory(Categories.CONNECTING_OBJECTS);
    }

    @Test
    public void checkCategoryTitles() {
        assertEquals(CaseManagementPaletteDefinitionFactory.STAGES,
                     factory.getCategoryTitle(CaseManagementPaletteDefinitionFactory.STAGES));
        assertEquals(CaseManagementPaletteDefinitionFactory.ACTIVITIES,
                     factory.getCategoryTitle(CaseManagementPaletteDefinitionFactory.ACTIVITIES));
    }

    @Test
    public void checkCategoryDescriptions() {
        assertEquals(CaseManagementPaletteDefinitionFactory.STAGES,
                     factory.getCategoryDescription(CaseManagementPaletteDefinitionFactory.STAGES));
        assertEquals(CaseManagementPaletteDefinitionFactory.ACTIVITIES,
                     factory.getCategoryDescription(CaseManagementPaletteDefinitionFactory.ACTIVITIES));
    }

    @Test
    public void checkCategoryGlyphDefinitions() {
        assertEquals(AdHocSubprocess.class,
                     factory.getCategoryTargetDefinitionId(CaseManagementPaletteDefinitionFactory.STAGES));
        assertEquals(BusinessRuleTask.class,
                     factory.getCategoryTargetDefinitionId(CaseManagementPaletteDefinitionFactory.ACTIVITIES));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkMorphGroupTitles() {
        factory.getMorphGroupTitle(CaseManagementPaletteDefinitionFactory.STAGES,
                                   new Object());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void checkMorphGroupDescriptions() {
        factory.getMorphGroupDescription(CaseManagementPaletteDefinitionFactory.STAGES,
                                         new Object());
    }
}
