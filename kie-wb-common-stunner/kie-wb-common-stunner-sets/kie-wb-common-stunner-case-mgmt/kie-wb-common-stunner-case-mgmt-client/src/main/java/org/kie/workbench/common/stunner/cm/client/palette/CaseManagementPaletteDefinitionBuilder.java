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

package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;

import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.isType;

@Dependent
@CaseManagementEditor
public class CaseManagementPaletteDefinitionBuilder
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    public static final String STAGES = "Stages";
    public static final String TASKS = "Tasks";
    public static final String SUBPROCESSES = "Subprocesses";
    public static final String SUBCASES = "Subcases";

    private static final Map<String, String> CAT_TITLES = new Maps.Builder<String, String>()
            .put(STAGES,
                 STAGES)
            .put(TASKS,
                 TASKS)
            .put(SUBPROCESSES,
                 SUBPROCESSES)
            .put(SUBCASES,
                 SUBCASES)
            .build();

    private static final Map<String, String> DEFINITION_CATEGORY_MAPPINGS = new Maps.Builder<String, String>()
            .put(AdHocSubprocess.class.getName(),
                 STAGES)
            .put(UserTask.class.getName(),
                 TASKS)
            .put(ProcessReusableSubprocess.class.getName(),
                 SUBPROCESSES)
            .put(CaseReusableSubprocess.class.getName(),
                 SUBCASES)
            .build();

    private final AbstractPaletteDefinitionBuilder paletteDefinitionBuilder;
    private final DefinitionManager definitionManager;

    // CDI proxy.
    protected CaseManagementPaletteDefinitionBuilder() {
        this(null, null);
    }

    @Inject
    public CaseManagementPaletteDefinitionBuilder(final CollapsedPaletteDefinitionBuilder paletteDefinitionBuilder,
                                                  final DefinitionManager definitionManager) {
        this.paletteDefinitionBuilder = paletteDefinitionBuilder;
        this.definitionManager = definitionManager;
    }

    @PostConstruct
    public void init() {
        paletteDefinitionBuilder
                .itemFilter(isDefinitionAllowed())
                .categoryFilter(isCategoryAllowed())
                .categoryProvider(this::getCategoryFor);
    }

    private String getCategoryFor(final Object definition) {
        final String fqcn = definition.getClass().getName();
        final String categoryId = DEFINITION_CATEGORY_MAPPINGS.get(fqcn);
        return null != categoryId ?
                categoryId :
                definitionManager.adapters().forDefinition().getCategory(definition);
    }

    private Predicate<String> isCategoryAllowed() {
        return CAT_TITLES::containsKey;
    }

    private Predicate<String> isDefinitionAllowed() {
        return isType(CaseManagementDiagram.class)
                .or(isType(NoneTask.class))
                .or(isType(ServiceTask.class))
                .or(isType(Lane.class))
                .or(isType(StartNoneEvent.class))
                .or(isType(EndNoneEvent.class))
                .or(isType(ParallelGateway.class))
                .or(isType(ExclusiveGateway.class))
                .or(isType(SequenceFlow.class))
                .negate();
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinition) {
        paletteDefinitionBuilder.build(canvasHandler,
                                       paletteDefinition);
    }

    AbstractPaletteDefinitionBuilder getPaletteDefinitionBuilder() {
        return paletteDefinitionBuilder;
    }
}
