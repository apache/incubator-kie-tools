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

package org.kie.workbench.common.stunner.bpmn.client.components.palette.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseEndEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.BaseStartEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.BindableDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPaletteBuilder;
import org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService;

@Dependent
public class BPMNPaletteDefinitionFactory extends BindableDefSetPaletteDefinitionFactory<DefinitionSetPalette, BS3PaletteWidget<DefinitionSetPalette>> {

    private final AbstractTranslationService translationService;

    private static final Map<String, Class<?>> CAT_DEF_IDS = new HashMap<String, Class<?>>(1) {{
        put(Categories.ACTIVITIES,
            NoneTask.class);
        put(Categories.ACTIVITIES,
            UserTask.class);
        put(Categories.ACTIVITIES,
            ScriptTask.class);
        put(Categories.ACTIVITIES,
            BusinessRuleTask.class);
        put(Categories.ACTIVITIES,
            EmbeddedSubprocess.class);
        put(Categories.ACTIVITIES,
            ReusableSubprocess.class);
        put(Categories.ACTIVITIES,
            AdHocSubprocess.class);
        put(Categories.CONNECTING_OBJECTS,
            SequenceFlow.class);
        put(Categories.EVENTS,
            StartNoneEvent.class);
        put(Categories.EVENTS,
            StartSignalEvent.class);
        put(Categories.EVENTS,
            StartTimerEvent.class);
        put(Categories.EVENTS,
            StartErrorEvent.class);
        put(Categories.EVENTS,
            EndNoneEvent.class);
        put(Categories.EVENTS,
            EndTerminateEvent.class);
        put(Categories.EVENTS,
            EndErrorEvent.class);
        put(Categories.EVENTS,
            IntermediateTimerEvent.class);
        put(Categories.EVENTS,
            IntermediateSignalEventCatching.class);
        put(Categories.EVENTS,
            IntermediateSignalEventThrowing.class);
        put(Categories.EVENTS,
            IntermediateErrorEventCatching.class);
        put(Categories.GATEWAYS,
            ParallelGateway.class);
        put(Categories.GATEWAYS,
            ExclusiveGateway.class);
        put(Categories.GATEWAYS,
            InclusiveGateway.class);
        put(Categories.CONTAINERS,
            Lane.class);
    }};
    private final Map<String, String> CAT_TITLES = new HashMap<String, String>(6);
    private static final Map<String, String> MORPH_GROUP_TITLES = new HashMap<String, String>(6);

    @Inject
    public BPMNPaletteDefinitionFactory(final ShapeManager shapeManager,
                                        final DefinitionSetPaletteBuilder paletteBuilder,
                                        final AbstractTranslationService translationService,
                                        final ManagedInstance<BS3PaletteWidget<DefinitionSetPalette>> palette) {
        super(shapeManager,
              paletteBuilder,
              palette);
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        CAT_TITLES.put(Categories.ACTIVITIES,
                       translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.category.activities"));
        CAT_TITLES.put(Categories.CONNECTING_OBJECTS,
                       translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.category.connectingObjects"));
        CAT_TITLES.put(Categories.EVENTS,
                       translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.category.events"));
        CAT_TITLES.put(Categories.GATEWAYS,
                       translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.category.gateways"));
        CAT_TITLES.put(Categories.CONTAINERS,
                       translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.category.containers"));
        MORPH_GROUP_TITLES.put(BaseTask.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.tasks"));
        MORPH_GROUP_TITLES.put(BaseStartEvent.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.start"));
        MORPH_GROUP_TITLES.put(BaseEndEvent.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.end"));
        MORPH_GROUP_TITLES.put(BaseCatchingIntermediateEvent.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.catchingIntermediate"));
        MORPH_GROUP_TITLES.put(BaseThrowingIntermediateEvent.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.throwingIntermediate"));
        MORPH_GROUP_TITLES.put(BaseSubprocess.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.subprocess"));
        MORPH_GROUP_TITLES.put(BaseGateway.class.getName(),
                               translationService.getKeyValue("org.kie.workbench.common.stunner.bpmn.definition.morph.base.gateways"));
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
