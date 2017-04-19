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

package org.kie.workbench.common.stunner.bpmn.client.components.palette.factory;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;

// TODO: i18n.
@Dependent
public class BPMNPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory {

    private static final Map<String, String> CAT_TITLES = new HashMap<String, String>(6) {{
        put(Categories.ACTIVITIES,
            "Activities");
        put(Categories.SUBPROCESSES,
            "Subprocesses");
        put(Categories.CONNECTING_OBJECTS,
            "Connecting objects");
        put(Categories.EVENTS,
            "Events");
        put(Categories.GATEWAYS,
            "Gateways");
        put(Categories.LANES,
            "Lanes");
    }};

    private static final Map<String, Class<?>> CAT_DEF_IDS = new HashMap<String, Class<?>>(1) {{
        put(Categories.ACTIVITIES,
            NoneTask.class);
        put(Categories.SUBPROCESSES,
            ReusableSubprocess.class);
        put(Categories.CONNECTING_OBJECTS,
            SequenceFlow.class);
        put(Categories.EVENTS,
            StartNoneEvent.class);
        put(Categories.EVENTS,
            StartSignalEvent.class);
        put(Categories.EVENTS,
            StartTimerEvent.class);
        put(Categories.EVENTS,
            IntermediateTimerEvent.class);
        put(Categories.GATEWAYS,
            ParallelGateway.class);
        put(Categories.LANES,
            Lane.class);
    }};

    private static final Map<String, String> MORPH_GROUP_TITLES = new HashMap<String, String>(5) {{
        put(BaseTask.class.getName(),
            "Tasks");
        put(BaseStartEvent.class.getName(),
            "Start Events");
        put(BaseEndEvent.class.getName(),
            "End Events");
        put(BaseSubprocess.class.getName(),
            "Subprocesses");
        put(BaseGateway.class.getName(),
            "Gateways");
    }};

    @Inject
    public BPMNPaletteDefinitionFactory(final ShapeManager shapeManager,
                                        final DefinitionSetPaletteBuilder paletteBuilder) {
        super(shapeManager,
              paletteBuilder);
    }

    @Override
    protected void configureBuilder() {
        super.configureBuilder();
        // Exclude BPMN Diagram from palette model.
        excludeDefinition(BPMNDiagramImpl.class);
        // Exclude the none task from palette, it will be available by dragging from the main Activities category.
        excludeDefinition(NoneTask.class);
        // TODO: Exclude connectors category from being present on the palette model - Dropping connectors from palette produces an error right now, must fix it on lienzo side.
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
        return BPMNDefinitionSet.class;
    }
}
