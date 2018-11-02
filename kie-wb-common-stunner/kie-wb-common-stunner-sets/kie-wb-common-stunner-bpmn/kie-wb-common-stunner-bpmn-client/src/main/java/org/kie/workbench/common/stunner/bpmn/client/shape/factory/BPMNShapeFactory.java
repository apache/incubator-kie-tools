/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.factory;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.AssociationConnectorDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.BPMNDiagramShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.CatchingIntermediateEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.EndEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.GatewayShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.LaneShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.ServiceTaskShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.StartEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SubprocessShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.TaskShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.ThrowingIntermediateEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.BPMNShapeViewHandlers;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

@Dependent
public class BPMNShapeFactory
        implements ShapeFactory<BPMNDefinition, Shape> {

    private final BasicShapesFactory basicShapesFactory;
    private final SVGShapeFactory svgShapeFactory;
    private final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory;
    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;
    private final StunnerPreferencesRegistries preferencesRegistries;
    private final DefinitionUtils definitionUtils;

    // CDI proxy.
    protected BPMNShapeFactory() {
        this.basicShapesFactory = null;
        this.svgShapeFactory = null;
        this.delegateShapeFactory = null;
        this.workItemDefinitionRegistry = null;
        this.preferencesRegistries = null;
        this.definitionUtils = null;
    }

    @Inject
    public BPMNShapeFactory(final BasicShapesFactory basicShapesFactory,
                            final SVGShapeFactory svgShapeFactory,
                            final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory,
                            final ManagedInstance<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                            final DefinitionUtils definitionUtils,
                            final StunnerPreferencesRegistries preferencesRegistries) {
        this.basicShapesFactory = basicShapesFactory;
        this.svgShapeFactory = svgShapeFactory;
        this.delegateShapeFactory = delegateShapeFactory;
        this.workItemDefinitionRegistry = workItemDefinitionRegistry::get;
        this.definitionUtils = definitionUtils;
        this.preferencesRegistries = preferencesRegistries;
    }

    BPMNShapeFactory(final BasicShapesFactory basicShapesFactory,
                     final SVGShapeFactory svgShapeFactory,
                     final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory,
                     final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                     final DefinitionUtils definitionUtils,
                     final StunnerPreferencesRegistries preferencesRegistries) {
        this.basicShapesFactory = basicShapesFactory;
        this.svgShapeFactory = svgShapeFactory;
        this.delegateShapeFactory = delegateShapeFactory;
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.definitionUtils = definitionUtils;
        this.preferencesRegistries = preferencesRegistries;
    }

    @PostConstruct
    @SuppressWarnings("all")
    public void registerDelegates() {
        delegateShapeFactory
                .delegate(BPMNDiagramImpl.class,
                          new BPMNDiagramShapeDef(),
                          () -> svgShapeFactory)
                .delegate(NoneTask.class,
                          new TaskShapeDef(),
                          () -> svgShapeFactory)
                .delegate(UserTask.class,
                          new TaskShapeDef(),
                          () -> svgShapeFactory)
                .delegate(ScriptTask.class,
                          new TaskShapeDef(),
                          () -> svgShapeFactory)
                .delegate(BusinessRuleTask.class,
                          new TaskShapeDef(),
                          () -> svgShapeFactory)
                .delegate(ServiceTask.class,
                          new ServiceTaskShapeDef(workItemDefinitionRegistry::get),
                          () -> svgShapeFactory)
                .delegate(StartNoneEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartSignalEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartTimerEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartMessageEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartErrorEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartConditionalEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartEscalationEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(StartCompensationEvent.class,
                          new StartEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(ParallelGateway.class,
                          new GatewayShapeDef(),
                          () -> svgShapeFactory)
                .delegate(ExclusiveGateway.class,
                          new GatewayShapeDef(),
                          () -> svgShapeFactory)
                .delegate(InclusiveGateway.class,
                          new GatewayShapeDef(),
                          () -> svgShapeFactory)
                .delegate(Lane.class,
                          new LaneShapeDef(),
                          () -> svgShapeFactory)
                .delegate(ReusableSubprocess.class,
                          new SubprocessShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EmbeddedSubprocess.class,
                          new SubprocessShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EventSubprocess.class,
                          new SubprocessShapeDef(),
                          () -> svgShapeFactory)
                .delegate(AdHocSubprocess.class,
                          new SubprocessShapeDef(),
                          () -> svgShapeFactory)
                .delegate(MultipleInstanceSubprocess.class,
                          new SubprocessShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndNoneEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndSignalEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndMessageEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndTerminateEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndErrorEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndEscalationEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(EndCompensationEvent.class,
                          new EndEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateTimerEvent.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateConditionalEvent.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateSignalEventCatching.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateErrorEventCatching.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateMessageEventCatching.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateEscalationEvent.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateCompensationEvent.class,
                          new CatchingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateSignalEventThrowing.class,
                          new ThrowingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateMessageEventThrowing.class,
                          new ThrowingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateEscalationEventThrowing.class,
                          new ThrowingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(IntermediateCompensationEventThrowing.class,
                          new ThrowingIntermediateEventShapeDef(),
                          () -> svgShapeFactory)
                .delegate(SequenceFlow.class,
                          new SequenceFlowConnectorDef(() -> getFontHandler()),
                          () -> basicShapesFactory)
                .delegate(Association.class,
                          new AssociationConnectorDef(),
                          () -> basicShapesFactory);
    }

    private <W extends BPMNViewDefinition, V extends ShapeView> FontHandler.Builder<W, V> getFontHandler() {
        final String definitionSetId = definitionUtils.getDefinitionSetId(BPMNDefinitionSet.class);
        final StunnerTextPreferences preferences = preferencesRegistries.get(definitionSetId, StunnerTextPreferences.class);
        return new BPMNShapeViewHandlers.FontHandlerBuilder<W, V>()
                .alpha(c -> preferences.getTextAlpha())
                .fontFamily(c -> preferences.getTextFontFamily())
                .fontSize(c -> preferences.getTextFontSize())
                .fontColor(c -> preferences.getTextFillColor())
                .strokeColor(c -> preferences.getTextStrokeColor())
                .strokeSize(c -> preferences.getTextStrokeWidth());
    }

    @Override
    @SuppressWarnings("all")
    public Shape newShape(final BPMNDefinition definition) {
        return delegateShapeFactory.newShape(definition);
    }

    @Override
    @SuppressWarnings("all")
    public Glyph getGlyph(final String definitionId) {
        return delegateShapeFactory.getGlyph(definitionId);
    }
}
