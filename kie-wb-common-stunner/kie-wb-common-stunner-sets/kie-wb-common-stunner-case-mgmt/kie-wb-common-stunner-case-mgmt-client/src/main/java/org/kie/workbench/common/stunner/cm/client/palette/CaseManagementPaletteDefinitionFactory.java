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

import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementAdhocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementNoneTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementReusableSubprocess;
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
            CaseManagementNoneTask.class);
        put(Categories.SUBPROCESSES,
            CaseManagementAdhocSubprocess.class);
        put(Categories.SUBPROCESSES,
            CaseManagementReusableSubprocess.class);
    }};

    private static final Map<String, String> MORPH_GROUP_TITLES = new HashMap<String, String>(2) {{
        put(CaseManagementBaseSubprocess.class.getName(),
            STAGES);
        put(CaseManagementBaseTask.class.getName(),
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
        // Exclude CaseManagementDiagram from palette model.
        excludeDefinition(CaseManagementDiagram.class);
        // Exclude the none task from palette, it will be available by dragging from the main Activities category.
        excludeDefinition(CaseManagementNoneTask.class);
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
        return CaseManagementDefinitionSet.class;
    }
}
