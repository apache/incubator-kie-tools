/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = BPMNDiagram.BPMNDiagramBuilder.class)
@CanContain(roles = {"cm_stage", "cm_nop"})
@FormDefinition(
        startElement = "diagramSet",
        policy = FieldPolicy.ONLY_MARKED
)

public class BPMNDiagram extends org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram {

    public BPMNDiagram() {
        super();
    }

    public BPMNDiagram(final @MapsTo("diagramSet") DiagramSet diagramSet,
                       final @MapsTo("processData") ProcessData processData,
                       final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                       final @MapsTo("fontSet") FontSet fontSet,
                       final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        super(diagramSet,
              processData,
              backgroundSet,
              fontSet,
              dimensionsSet);
    }

    @NonPortable
    public static class BPMNDiagramBuilder implements Builder<BPMNDiagram> {

        public static final transient String COLOR = "#FFFFFF";
        public static final transient String BORDER_COLOR = "#000000";
        public static final Double BORDER_SIZE = 1d;
        public static final Double WIDTH = 950d;
        public static final Double HEIGHT = 950d;

        @Override
        public BPMNDiagram build() {
            return new BPMNDiagram(new DiagramSet(""),
                                   new ProcessData(),
                                   new BackgroundSet(COLOR,
                                                     BORDER_COLOR,
                                                     BORDER_SIZE),
                                   new FontSet(),
                                   new RectangleDimensionsSet(WIDTH,
                                                              HEIGHT));
        }
    }
}
