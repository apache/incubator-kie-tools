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
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNDiagramShapeDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

@Portable
@Bindable
@Definition( graphFactory = NodeFactory.class, builder = BPMNDiagram.BPMNDiagramBuilder.class )
@CanContain( roles = { "all" } )
@Shape( factory = BasicShapesFactory.class, def = BPMNDiagramShapeDef.class )
public class BPMNDiagram implements BPMNDefinition {

    @Category
    public static final transient String category = Categories.LANES;

    @Title
    public static final transient String title = "BPMN Diagram";

    @Description
    public static final transient String description = "BPMN Diagram";

    @PropertySet
    @FieldDef( label = FIELDDEF_GENERAL_SETTINGS, position = 0 )
    @Valid
    private BPMNGeneralSet general;

    @PropertySet
    @FieldDef( label = FIELDDEF_PROCESS_SETTINGS, position = 1 )
    @Valid
    private DiagramSet diagramSet;

    @PropertySet
    @FieldDef( label = FIELDDEF_DATA, position = 2 )
    @Valid
    protected ProcessData processData;

    @PropertySet
    private BackgroundSet backgroundSet;

    @PropertySet
    private FontSet fontSet;

    @PropertySet
    //@FieldDef( label = FIELDDEF_SHAPE_DIMENSIONS, position = 5 )
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add( "canContainArtifacts" );
        add( "diagram" );
    }};

    @NonPortable
    public static class BPMNDiagramBuilder implements Builder<BPMNDiagram> {

        public static final transient String COLOR = "#FFFFFF";
        public static final transient String BORDER_COLOR = "#000000";
        public static final Double BORDER_SIZE = 1d;
        public static final Double WIDTH = 950d;
        public static final Double HEIGHT = 950d;

        @Override
        public BPMNDiagram build() {
            return new BPMNDiagram( new BPMNGeneralSet(),
                    new DiagramSet(),
                    new ProcessData(),
                    new BackgroundSet( COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet(),
                    new RectangleDimensionsSet( WIDTH, HEIGHT ) );
        }

    }

    public BPMNDiagram() {
    }

    public BPMNDiagram( @MapsTo( "general" ) BPMNGeneralSet general,
                        @MapsTo( "diagramSet" ) DiagramSet diagramSet,
                        @MapsTo( "processData" ) ProcessData processData,
                        @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                        @MapsTo( "fontSet" ) FontSet fontSet,
                        @MapsTo( "dimensionsSet" ) RectangleDimensionsSet dimensionsSet ) {
        this.general = general;
        this.diagramSet = diagramSet;
        this.processData = processData;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
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

    public DiagramSet getDiagramSet() {
        return diagramSet;
    }

    public BPMNGeneralSet getGeneral() {
        return general;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet( RectangleDimensionsSet dimensionsSet ) {
        this.dimensionsSet = dimensionsSet;
    }

    public ProcessData getProcessData() {
        return processData;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setGeneral( BPMNGeneralSet general ) {
        this.general = general;
    }

    public void setDiagramSet( DiagramSet diagramSet ) {
        this.diagramSet = diagramSet;
    }

    public void setProcessData( ProcessData processData ) {
        this.processData = processData;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet( FontSet fontSet ) {
        this.fontSet = fontSet;
    }

}
