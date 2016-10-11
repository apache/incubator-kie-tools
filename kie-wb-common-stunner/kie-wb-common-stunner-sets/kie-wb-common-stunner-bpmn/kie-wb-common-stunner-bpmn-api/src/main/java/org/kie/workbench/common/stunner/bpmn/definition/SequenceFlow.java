/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
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

import javax.validation.Valid;
import java.util.Set;

@Portable
@Bindable
@Definition( graphFactory = EdgeFactory.class, builder = SequenceFlow.SequenceFlowBuilder.class )
// Connection rules.
@CanConnect( startRole = "sequence_start", endRole = "sequence_end" )
@CanConnect( startRole = "choreography_sequence_start", endRole = "choreography_sequence_end" )
@CanConnect( startRole = "Exclusive_Eventbased_Gateway", endRole = "FromEventbasedGateway" )
@CanConnect( startRole = "EventbasedGateway", endRole = "FromEventbasedGateway" )
// Edge cardinality rules.
@EdgeOccurrences( role = "Startevents_all", type = EdgeOccurrences.EdgeType.INCOMING, max = 0 )
@EdgeOccurrences( role = "Endevents_all", type = EdgeOccurrences.EdgeType.OUTGOING, max = 0 )
public class SequenceFlow extends BaseConnector {

    @Title
    public static final transient String title = "Sequence Flow";

    @PropertySet
    @FieldDef( label = "Implementation/Execution", position = 1 )
    @Valid
    protected SequenceFlowExecutionSet executionSet;

    @NonPortable
    public static class SequenceFlowBuilder extends BaseConnectorBuilder<SequenceFlow> {
        @Override
        public SequenceFlow build() {
            return new SequenceFlow( new BPMNGeneralSet( "Sequence" ),
                    new SequenceFlowExecutionSet(),
                    new BackgroundSet( COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet() );
        }
    }

    public SequenceFlow() {
    }

    public SequenceFlow( @MapsTo( "general" ) BPMNGeneralSet general,
                         @MapsTo( "executionSet" ) SequenceFlowExecutionSet executionSet,
                         @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                         @MapsTo( "fontSet" ) FontSet fontSet ) {
        super( general, backgroundSet, fontSet );
        this.executionSet = executionSet;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public BPMNGeneralSet getGeneral() {
        return general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setGeneral( BPMNGeneralSet general ) {
        this.general = general;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public SequenceFlowExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet( SequenceFlowExecutionSet executionSet ) {
        this.executionSet = executionSet;
    }
}
