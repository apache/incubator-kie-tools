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
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import static org.kie.workbench.common.stunner.basicset.util.FieldDefLabelConstants.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Shape( factory = BasicShapesFactory.class, def = SequenceFlowConnectorDef.class )
public abstract class BaseConnector implements BPMNDefinition {
    @Category
    public static final transient String category = Categories.CONNECTING_OBJECTS;

    @Description
    public static final transient String description = "A Connecting Object";

    @PropertySet
    @FieldDef( label = FIELDDEF_GENERAL_SETTINGS, position = 0 )
    @Valid
    protected BPMNGeneralSet general;

    @PropertySet
    @FieldDef( label = FIELDDEF_BACKGROUND_SETTINGS, position = 2 )
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    //@FieldDef( label = FIELDDEF_FONT_SETTINGS, position = 3 )
    protected FontSet fontSet;

    @NonPortable
    static abstract class BaseConnectorBuilder<T extends BaseConnector> implements Builder<BaseConnector> {

        public static final transient String COLOR = "#000000";
        public static final transient String BORDER_COLOR = "#000000";
        public static final Double BORDER_SIZE = 3d;

    }

    @Labels
    protected final Set<String> labels = new HashSet<String>() {{
        add( "all" );
        add( "ConnectingObjectsMorph" );
    }};

    protected BaseConnector() {
    }

    public BaseConnector( @MapsTo( "general" ) BPMNGeneralSet general,
                          @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                          @MapsTo( "fontSet" ) FontSet fontSet
    ) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
    }

    public String getCategory() {
        return category;
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

    public void setGeneral( BPMNGeneralSet general ) {
        this.general = general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet( FontSet fontSet ) {
        this.fontSet = fontSet;
    }
}
