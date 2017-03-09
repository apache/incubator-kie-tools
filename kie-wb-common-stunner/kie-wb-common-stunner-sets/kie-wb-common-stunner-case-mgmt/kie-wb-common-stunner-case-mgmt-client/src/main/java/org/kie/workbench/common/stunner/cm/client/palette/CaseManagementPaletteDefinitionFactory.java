/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;

@Dependent
public class CaseManagementPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory {

    private static final String STAGES = "Stages";
    private static final String ACTIVITIES = "Activities";

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>(2) {{
        put(Categories.SUBPROCESSES,
            STAGES);
        put(Categories.ACTIVITIES,
            ACTIVITIES);
    }};

    private static final Map<String, Class<?>> CAT_DEF_IDS = new HashMap<String, Class<?>>(1) {{
        put(Categories.ACTIVITIES,
            UserTask.class);
        put(Categories.ACTIVITIES,
            ScriptTask.class);
        put(Categories.ACTIVITIES,
            BusinessRuleTask.class);
        put(Categories.SUBPROCESSES,
            AdHocSubprocess.class);
        put(Categories.SUBPROCESSES,
            ReusableSubprocess.class);
    }};

    private static final Map<String, String> MORPH_GROUP_TITLES = new HashMap<String, String>(2) {{
        put(BaseSubprocess.class.getName(),
            STAGES);
        put(BaseTask.class.getName(),
            ACTIVITIES);
    }};

    @Inject
    public CaseManagementPaletteDefinitionFactory(final ShapeManager shapeManager,
                                                  final DefinitionSetPaletteBuilder paletteBuilder) {
        super(shapeManager,
              paletteBuilder);
    }

    @Override
    protected void configureBuilder() {
        super.configureBuilder();
        excludeDefinition(BPMNDiagram.class);
        excludeDefinition(Lane.class);
        excludeDefinition(NoneTask.class);
        excludeDefinition(StartNoneEvent.class);
        excludeDefinition(EndNoneEvent.class);
        excludeDefinition(EndTerminateEvent.class);
        excludeDefinition(ParallelGateway.class);
        excludeDefinition(ExclusiveDatabasedGateway.class);
        excludeDefinition(SequenceFlow.class);

        excludeCategory(Categories.CONNECTING_OBJECTS);
    }

    @Override
    protected String getCategoryTitle(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected Class<?> getCategoryTargetDefinitionId(final String id) {
        return CAT_DEF_IDS.get(id);
    }

    @Override
    protected String getCategoryDescription(final String id) {
        return CAT_TITLES.get(id);
    }

    @Override
    protected String getMorphGroupTitle(final String morphBaseId,
                                        final Object definition) {
        return MORPH_GROUP_TITLES.get(morphBaseId);
    }

    @Override
    protected String getMorphGroupDescription(final String morphBaseId,
                                              final Object definition) {
        return MORPH_GROUP_TITLES.get(morphBaseId);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return CaseManagementDefinitionSet.class;
    }
}
