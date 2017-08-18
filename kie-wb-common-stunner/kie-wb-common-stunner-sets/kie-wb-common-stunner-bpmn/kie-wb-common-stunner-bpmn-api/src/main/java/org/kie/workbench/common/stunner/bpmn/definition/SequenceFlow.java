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

package org.kie.workbench.common.stunner.bpmn.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.SequenceFlowExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class, builder = SequenceFlow.SequenceFlowBuilder.class)
// *** Connection rules for sequence flows ****
@CanConnect(startRole = "sequence_start", endRole = "sequence_end")
@CanConnect(startRole = "choreography_sequence_start", endRole = "choreography_sequence_end")
@CanConnect(startRole = "Exclusive_Eventbased_Gateway", endRole = "FromEventbasedGateway")
@CanConnect(startRole = "EventbasedGateway", endRole = "FromEventbasedGateway")
// **** Cardinality rules for connectors ****
// No incoming sequence flows for start events.
@EdgeOccurrences(role = "Startevents_all", type = EdgeOccurrences.EdgeType.INCOMING, max = 0)
// Only single outgoing sequence flow for start events.
@EdgeOccurrences(role = "Startevents_outgoing_all", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
// No outgoing sequence flows for end events.
@EdgeOccurrences(role = "Endevents_all", type = EdgeOccurrences.EdgeType.OUTGOING, max = 0)
// A single outgoing sequence flows for message flow_start roles, such as Tasks or Subprocess.
@EdgeOccurrences(role = "messageflow_start", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
// A single outgoing sequence flows for event types that can be docked (boundary) such as Intermediate Timer Event
@EdgeOccurrences(role = "IntermediateEventOnActivityBoundary", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1)
// Sequence flows cannot exceed bounds when any of the nodes are in an embedded subprocess context.
@RuleExtension(handler = ConnectorParentsMatchHandler.class,
        typeArguments = {EmbeddedSubprocess.class},
        arguments = {"Sequence flow connectors cannot exceed the embbedded subprocess' bounds. " +
                "Both source and target nodes must be in same parent process."})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class SequenceFlow extends BaseConnector {

    @Title
    public static final transient String title = "Sequence Flow";

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected SequenceFlowExecutionSet executionSet;

    @NonPortable
    public static class SequenceFlowBuilder extends BaseConnectorBuilder<SequenceFlow> {

        @Override
        public SequenceFlow build() {
            return new SequenceFlow(new BPMNGeneralSet("Sequence"),
                                    new SequenceFlowExecutionSet(),
                                    new BackgroundSet(COLOR,
                                                      BORDER_COLOR,
                                                      BORDER_SIZE),
                                    new FontSet());
        }
    }

    public SequenceFlow() {
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

    @Override
    public String getTitle() {
        return title;
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
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SequenceFlow) {
            SequenceFlow other = (SequenceFlow) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}
