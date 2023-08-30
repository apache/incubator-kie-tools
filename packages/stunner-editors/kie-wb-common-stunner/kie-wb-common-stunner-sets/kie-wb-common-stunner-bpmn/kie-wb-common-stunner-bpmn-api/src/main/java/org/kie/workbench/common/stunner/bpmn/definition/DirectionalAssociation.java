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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.kie.workbench.common.stunner.core.rule.annotation.EdgeOccurrences;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;
import org.kie.workbench.common.stunner.core.rule.ext.impl.ConnectorParentsMatchHandler;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class)
// *** Connection rules for associations ****
@CanConnect(startRole = "IntermediateCompensationEvent", endRole = "cm_activity")
@CanConnect(startRole = "IntermediateCompensationEvent", endRole = "cm_stage")
@CanConnect(startRole = "IntermediateCompensationEvent", endRole = "cm_stage")

// **** Cardinality rules for associations ****
@EdgeOccurrences(role = "IntermediateCompensationEvent", type = EdgeOccurrences.EdgeType.OUTGOING, max = 1, min = 1)
@EdgeOccurrences(role = "cm_activity", type = EdgeOccurrences.EdgeType.INCOMING, max = 1)
@EdgeOccurrences(role = "cm_stage", type = EdgeOccurrences.EdgeType.INCOMING, max = 1)

// Associations cannot exceed bounds when any of the nodes are in an embedded subprocess context.
@RuleExtension(handler = ConnectorParentsMatchHandler.class,
        typeArguments = {EmbeddedSubprocess.class, EventSubprocess.class, MultipleInstanceSubprocess.class,
                AdHocSubprocess.class},
        arguments = {"Association flow connectors cannot exceed the sub-process' bounds. " +
                "Both source and target nodes must be in same parent process."})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class DirectionalAssociation extends Association {

    public DirectionalAssociation() {
        this(new BPMNGeneralSet(),
             new BackgroundSet(COLOR,
                               BORDER_COLOR,
                               BORDER_SIZE),
             new FontSet());
    }

    public DirectionalAssociation(final @MapsTo("general") BPMNGeneralSet general,
                                  final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                  final @MapsTo("fontSet") FontSet fontSet) {
        super(general,
              backgroundSet,
              fontSet);
    }
}
