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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;

@Dependent
public class CaseManagementPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory<DefinitionSetPalette, BS3PaletteWidget<DefinitionSetPalette>> {

    public static final String STAGES = "Stages";
    public static final String ACTIVITIES = "Activities";

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>(2) {{
        put(STAGES,
            STAGES);
        put(ACTIVITIES,
            ACTIVITIES);
    }};

    private static final Map<String, Class<?>> CAT_DEF_IDS = new HashMap<String, Class<?>>(5) {{
        put(STAGES,
            AdHocSubprocess.class);
        put(ACTIVITIES,
            BusinessRuleTask.class);
    }};

    @Inject
    public CaseManagementPaletteDefinitionFactory(final ShapeManager shapeManager,
                                                  final @CaseManagementEditor DefinitionSetPaletteBuilder paletteBuilder,
                                                  final BS3PaletteWidget<DefinitionSetPalette> palette) {
        super(shapeManager,
              paletteBuilder,
              palette);
    }

    @Override
    protected void configureBuilder() {
        super.configureBuilder();
        excludeDefinition(CaseManagementDiagram.class);
        excludeDefinition(Lane.class);
        excludeDefinition(NoneTask.class);
        excludeDefinition(StartNoneEvent.class);
        excludeDefinition(EndNoneEvent.class);
        excludeDefinition(EndTerminateEvent.class);
        excludeDefinition(ParallelGateway.class);
        excludeDefinition(ExclusiveDatabasedGateway.class);
        excludeDefinition(SequenceFlow.class);

        excludeCategory(Categories.EVENTS);
        excludeCategory(Categories.CONNECTING_OBJECTS);
    }

    @Override
    protected String getCategoryTitle(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected String getCategoryDescription(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected Class<?> getCategoryTargetDefinitionId(final String id) {
        return CAT_DEF_IDS.get(id);
    }

    @Override
    protected String getMorphGroupTitle(final String morphBaseId,
                                        final Object definition) {
        throw new UnsupportedOperationException("CaseManagement does not use Morph Groups for Palette construction");
    }

    @Override
    protected String getMorphGroupDescription(final String morphBaseId,
                                              final Object definition) {
        throw new UnsupportedOperationException("CaseManagement does not use Morph Groups for Palette construction");
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }
}
