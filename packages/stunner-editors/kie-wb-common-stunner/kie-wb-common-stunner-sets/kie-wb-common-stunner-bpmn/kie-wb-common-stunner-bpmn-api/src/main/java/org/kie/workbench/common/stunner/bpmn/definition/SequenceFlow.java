/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class)
// *** Connection rules for sequence flows ****
@CanConnect(startRole = "sequence_start", endRole = "sequence_end")
@CanConnect(startRole = "choreography_sequence_start", endRole = "choreography_sequence_end")
@CanConnect(startRole = "Exclusive_Eventbased_Gateway", endRole = "FromEventbasedGateway")
@CanConnect(startRole = "EventbasedGateway", endRole = "FromEventbasedGateway")
// **** Cardinality rules for connectors ****
// No incoming sequence flows for start events.
@EdgeOccurrences(role = "Startevents_all", type = EdgeOccurrences.EdgeType.INCOMING, max = 0)
// No outgoing sequence flows for end events.
@EdgeOccurrences(role = "Endevents_all", type = EdgeOccurrences.EdgeType.OUTGOING, max = 0)
// A single outgoing sequence flows for event types that can be docked (boundary) such as Intermediate Timer Event
@EdgeOccurrences(role = "IntermediateEventOnActivityBoundary", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
@EdgeOccurrences(role = "IntermediateEventCatching", type = EdgeOccurrences.EdgeType.INCOMING, max = 1)
@EdgeOccurrences(role = "IntermediateEventThrowing", type = EdgeOccurrences.EdgeType.INCOMING, max = 1)
@EdgeOccurrences(role = "IntermediateEventThrowing", type = EdgeOccurrences.EdgeType.OUTGOING, min = 1)
@EdgeOccurrences(role = "IntermediateEventThrowing", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
// Sequence flows cannot exceed bounds when any of the nodes are in an subprocess context.
@RuleExtension(handler = ConnectorParentsMatchHandler.class,
        typeArguments = {EmbeddedSubprocess.class, EventSubprocess.class,
                MultipleInstanceSubprocess.class, AdHocSubprocess.class},
        arguments = {"Sequence flow connectors cannot exceed the parent's subprocess bounds. " +
                "Both source and target nodes must be in the same subprocess."})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class SequenceFlow extends BaseConnector {

    @Property
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected SequenceFlowExecutionSet executionSet;

    public SequenceFlow() {
        this(new BPMNGeneralSet(),
             new SequenceFlowExecutionSet(),
             new BackgroundSet(COLOR,
                               BORDER_COLOR,
                               BORDER_SIZE),
             new FontSet());
    }

    public SequenceFlow(final @MapsTo("general") BPMNGeneralSet general,
                        final @MapsTo("executionSet") SequenceFlowExecutionSet executionSet,
                        final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                        final @MapsTo("fontSet") FontSet fontSet) {
        super(general,
              backgroundSet,
              fontSet);
        this.executionSet = executionSet;
    }

    public SequenceFlowExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final SequenceFlowExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(executionSet));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SequenceFlow) {
            SequenceFlow other = (SequenceFlow) o;
            return super.equals(other) &&
                    Objects.equals(executionSet,
                                   other.executionSet);
        }
        return false;
    }
}
