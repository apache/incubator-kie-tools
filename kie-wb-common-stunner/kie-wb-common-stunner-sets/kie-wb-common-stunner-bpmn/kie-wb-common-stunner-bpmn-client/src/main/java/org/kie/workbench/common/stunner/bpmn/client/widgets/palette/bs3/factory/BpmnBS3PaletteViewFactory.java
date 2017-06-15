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

package org.kie.workbench.common.stunner.bpmn.client.widgets.palette.bs3.factory;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.BindableBS3PaletteGlyphViewFactory;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconRenderer;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.IconResource;
import org.kie.workbench.common.stunner.client.widgets.palette.factory.icons.svg.SVGIconRenderer;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;

@ApplicationScoped
public class BpmnBS3PaletteViewFactory extends BindableBS3PaletteGlyphViewFactory {

    @SuppressWarnings("unchecked")
    private final static Map<String, IconResource> CATEGORY_RERNDERERS_SETTINGS = new HashMap<String, IconResource>() {{
        put(Categories.ACTIVITIES,
            new IconResource(BPMNImageResources.INSTANCE.categoryActivity()));
        put(Categories.CONTAINERS,
            new IconResource(BPMNImageResources.INSTANCE.categoryContainer()));
        put(Categories.GATEWAYS,
            new IconResource(BPMNImageResources.INSTANCE.categoryGateway()));
        put(Categories.EVENTS,
            new IconResource(BPMNImageResources.INSTANCE.circle()));
        put(Categories.CONNECTING_OBJECTS,
            new IconResource(BPMNImageResources.INSTANCE.categorySequence()));
    }};

    @SuppressWarnings("unchecked")
    private final static Map<String, IconResource> DEFINITION_RERNDERERS_SETTINGS = new HashMap<String, IconResource>() {{
        put(NoneTask.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.taskUser()));
        put(UserTask.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.taskUser()));
        put(ScriptTask.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.taskScript()));
        put(BusinessRuleTask.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.taskBusinessRule()));
        put(StartNoneEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventStart()));
        put(StartSignalEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventStartSignal()));
        put(StartTimerEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventStartTimer()));
        put(ExclusiveDatabasedGateway.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.gatewayExclusive()));
        put(EndNoneEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventEndNone()));
        put(EndTerminateEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventEndTerminate()));
        put(IntermediateTimerEvent.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.eventIntermediateTimer()));
        put(Lane.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.lane()));
        put(ParallelGateway.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.gatewayParallelEvent()));
        put(SequenceFlow.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.sequenceFlow()));
        put(ReusableSubprocess.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.subProcessReusable()));
        put(EmbeddedSubprocess.class.getName(),
            new IconResource(BPMNImageResources.INSTANCE.subProcessEmbedded()));
    }};

    protected BpmnBS3PaletteViewFactory() {
        this(null);
    }

    @Inject
    public BpmnBS3PaletteViewFactory(final ShapeManager shapeManager) {
        super(shapeManager);
    }

    @Override
    protected Class<?> getDefinitionSetType() {
        return BPMNDefinitionSet.class;
    }

    @Override
    protected Class<? extends IconRenderer> getPaletteIconRendererType() {
        return SVGIconRenderer.class;
    }

    @Override
    protected Map<String, IconResource> getCategoryIconResources() {
        return CATEGORY_RERNDERERS_SETTINGS;
    }

    @Override
    protected Map<String, IconResource> getDefinitionIconResources() {
        return DEFINITION_RERNDERERS_SETTINGS;
    }
}
