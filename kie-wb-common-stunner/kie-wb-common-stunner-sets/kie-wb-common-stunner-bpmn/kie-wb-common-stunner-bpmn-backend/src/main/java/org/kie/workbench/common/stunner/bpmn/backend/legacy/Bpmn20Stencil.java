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
package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.emf.ecore.EClass;

/**
 * @author Antoine Toulme
 * the mapping to stencil ids to BPMN 2.0 metamodel classes
 */
public enum Bpmn20Stencil {
    Task(Bpmn2Package.eINSTANCE.getTask()),
    BPMNDiagram(Bpmn2Package.eINSTANCE.getDefinitions()),
    Pool(Bpmn2Package.eINSTANCE.getProcess()),
    Lane(Bpmn2Package.eINSTANCE.getLane()),
    SequenceFlow(Bpmn2Package.eINSTANCE.getSequenceFlow()),
    Task_None(Bpmn2Package.eINSTANCE.getTask()),
    Task_Custom(Bpmn2Package.eINSTANCE.getTask()),
    Task_Script(Bpmn2Package.eINSTANCE.getScriptTask()),
    Task_User(Bpmn2Package.eINSTANCE.getUserTask()),
    Task_Business_rule(Bpmn2Package.eINSTANCE.getBusinessRuleTask()),
    Task_Manual(Bpmn2Package.eINSTANCE.getManualTask()),
    Task_Service(Bpmn2Package.eINSTANCE.getServiceTask()),
    Task_Send(Bpmn2Package.eINSTANCE.getSendTask()),
    Task_Receive(Bpmn2Package.eINSTANCE.getReceiveTask()),
    Exclusive_Databased_Gateway(Bpmn2Package.eINSTANCE.getExclusiveGateway()),
    ParallelGateway(Bpmn2Package.eINSTANCE.getParallelGateway()),
    EventbasedGateway(Bpmn2Package.eINSTANCE.getEventBasedGateway()),
    ComplexGateway(Bpmn2Package.eINSTANCE.getComplexGateway()),
    InclusiveGateway(Bpmn2Package.eINSTANCE.getInclusiveGateway()),
    StartNoneEvent(Bpmn2Package.eINSTANCE.getStartEvent()),
    StartMessageEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                      Bpmn2Package.eINSTANCE.getMessageEventDefinition()),
    StartEscalationEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                         Bpmn2Package.eINSTANCE.getEscalationEventDefinition()),
    StartCompensationEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                           Bpmn2Package.eINSTANCE.getCompensateEventDefinition()),
    StartSignalEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                     Bpmn2Package.eINSTANCE.getSignalEventDefinition()),
    StartMultipleEvent(Bpmn2Package.eINSTANCE.getStartEvent()),
    StartParallelMultipleEvent(Bpmn2Package.eINSTANCE.getStartEvent()),
    StartTimerEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                    Bpmn2Package.eINSTANCE.getTimerEventDefinition()),
    StartErrorEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                    Bpmn2Package.eINSTANCE.getErrorEventDefinition()),
    StartConditionalEvent(Bpmn2Package.eINSTANCE.getStartEvent(),
                          Bpmn2Package.eINSTANCE.getConditionalEventDefinition()),
    TextAnnotation(Bpmn2Package.eINSTANCE.getTextAnnotation()),
    Group(Bpmn2Package.eINSTANCE.getGroup()),
    DataObject(Bpmn2Package.eINSTANCE.getDataObject()),
    DataStore(Bpmn2Package.eINSTANCE.getDataStore()),
    Message(Bpmn2Package.eINSTANCE.getMessage()),
    EndNoneEvent(Bpmn2Package.eINSTANCE.getEndEvent()),
    EndMessageEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                    Bpmn2Package.eINSTANCE.getMessageEventDefinition()),
    EndEscalationEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                       Bpmn2Package.eINSTANCE.getEscalationEventDefinition()),
    EndCancelEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                   Bpmn2Package.eINSTANCE.getCancelEventDefinition()),
    EndErrorEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                  Bpmn2Package.eINSTANCE.getErrorEventDefinition()),
    EndSignalEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                   Bpmn2Package.eINSTANCE.getSignalEventDefinition()),
    EndTerminateEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                      Bpmn2Package.eINSTANCE.getTerminateEventDefinition()),
    EndMultipleEvent(Bpmn2Package.eINSTANCE.getEndEvent()),
    EndCompensationEvent(Bpmn2Package.eINSTANCE.getEndEvent(),
                         Bpmn2Package.eINSTANCE.getCompensateEventDefinition()),
    IntermediateMessageEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                     Bpmn2Package.eINSTANCE.getMessageEventDefinition()),
    IntermediateSignalEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                    Bpmn2Package.eINSTANCE.getSignalEventDefinition()),
    IntermediateErrorEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                   Bpmn2Package.eINSTANCE.getErrorEventDefinition()),
    IntermediateTimerEvent(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                           Bpmn2Package.eINSTANCE.getTimerEventDefinition()),
    IntermediateEscalationEvent(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                Bpmn2Package.eINSTANCE.getEscalationEventDefinition()),
    IntermediateConditionalEvent(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                 Bpmn2Package.eINSTANCE.getConditionalEventDefinition()),
    IntermediateLinkEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                  Bpmn2Package.eINSTANCE.getLinkEventDefinition()),
    IntermediateErrorEvent(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                           Bpmn2Package.eINSTANCE.getErrorEventDefinition()),
    IntermediateCancelEvent(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                            Bpmn2Package.eINSTANCE.getCancelEventDefinition()),
    IntermediateCompensationEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent(),
                                          Bpmn2Package.eINSTANCE.getCompensateEventDefinition()),
    IntermediateMultipleEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent()),
    IntermediateParallelMultipleEventCatching(Bpmn2Package.eINSTANCE.getIntermediateCatchEvent()),
    IntermediateEvent(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent()),
    IntermediateMessageEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent(),
                                     Bpmn2Package.eINSTANCE.getMessageEventDefinition()),
    IntermediateEscalationEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent(),
                                        Bpmn2Package.eINSTANCE.getEscalationEventDefinition()),
    IntermediateLinkEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent(),
                                  Bpmn2Package.eINSTANCE.getLinkEventDefinition()),
    IntermediateCompensationEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent(),
                                          Bpmn2Package.eINSTANCE.getCompensateEventDefinition()),
    IntermediateSignalEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent(),
                                    Bpmn2Package.eINSTANCE.getSignalEventDefinition()),
    IntermediateMultipleEventThrowing(Bpmn2Package.eINSTANCE.getIntermediateThrowEvent()),
    Association_Undirected(Bpmn2Package.eINSTANCE.getAssociation(),
                           AssociationDirection.NONE),
    Association_Unidirectional(Bpmn2Package.eINSTANCE.getAssociation(),
                               AssociationDirection.ONE),
    Association_Bidirectional(Bpmn2Package.eINSTANCE.getAssociation(),
                              AssociationDirection.BOTH),
    Subprocess(Bpmn2Package.eINSTANCE.getSubProcess()),
    AdHocSubprocess(Bpmn2Package.eINSTANCE.getAdHocSubProcess()),
    MultipleInstanceSubprocess(Bpmn2Package.eINSTANCE.getSubProcess()),
    ReusableSubprocess(Bpmn2Package.eINSTANCE.getCallActivity()),
    EventSubprocess(Bpmn2Package.eINSTANCE.getEventSubprocess()),
    Relationship(Bpmn2Package.eINSTANCE.getRelationship()),
    Import(Bpmn2Package.eINSTANCE.getImport());

    public String id;
    public EClass className;
    public EClass eventType;
    public AssociationDirection associationDirection;

    private Bpmn20Stencil(EClass className) {
        this.className = className;
    }

    private Bpmn20Stencil(EClass className,
                          AssociationDirection assocDir) {
        this.className = className;
        this.associationDirection = assocDir;
    }

    private Bpmn20Stencil(EClass className,
                          EClass eventType) {
        this.className = className;
        this.eventType = eventType;
    }

    public static BaseElement createElement(String stencilId,
                                            String taskType,
                                            boolean customElement) {
        if (customElement) {
            stencilId = "Task";
            taskType = "Custom";
        }
        Bpmn20Stencil stencil = Bpmn20Stencil.valueOf(taskType == null ? stencilId : stencilId + "_" + taskType.replaceAll(" ",
                                                                                                                           "_"));
        if (stencil == null) {
            throw new IllegalArgumentException("unregistered stencil id: " + stencilId);
        }
        BaseElement elt = (BaseElement) Bpmn2Factory.eINSTANCE.create(stencil.className);
        if (stencil.eventType != null) {
            if (elt instanceof CatchEvent) {
                ((CatchEvent) elt).getEventDefinitions().add((EventDefinition) Bpmn2Factory.eINSTANCE.create(stencil.eventType));
            } else if (elt instanceof ThrowEvent) {
                ((ThrowEvent) elt).getEventDefinitions().add((EventDefinition) Bpmn2Factory.eINSTANCE.create(stencil.eventType));
            } else {
                throw new IllegalArgumentException("Cannot set eventType on " + elt);
            }
        }
        if (stencil.associationDirection != null) {
            ((Association) elt).setAssociationDirection(stencil.associationDirection);
        }
        return elt;
    }
}
