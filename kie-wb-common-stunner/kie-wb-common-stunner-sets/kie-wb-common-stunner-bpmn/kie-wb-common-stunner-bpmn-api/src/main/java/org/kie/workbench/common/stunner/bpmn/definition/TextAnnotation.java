/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "general",
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class TextAnnotation extends BaseArtifacts {

    @PropertySet
    @FormField
    @Valid
    protected BPMNGeneralSet general;

    @Labels
    private final static Set<String> labels = new Sets.Builder<String>()
            .add("text_annotation")
            .add("lane_child")
            .add("all")
            .build();

    public TextAnnotation() {
        this(new BPMNGeneralSet("Text Annotation"),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet());
    }

    public TextAnnotation(final @MapsTo("general") BPMNGeneralSet general,
                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                          final @MapsTo("fontSet") FontSet fontSet,
                          final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        super(backgroundSet, fontSet, dimensionsSet);
        this.general = general;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public BPMNGeneralSet getGeneral() {
        return general;
    }

    public void setGeneral(final BPMNGeneralSet general) {
        this.general = general;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(general.hashCode(),
                                         backgroundSet.hashCode(),
                                         fontSet.hashCode(),
                                         dimensionsSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextAnnotation) {
            TextAnnotation other = (TextAnnotation) o;
            return general.equals(other.general) &&
                    backgroundSet.equals(other.backgroundSet) &&
                    fontSet.equals(other.fontSet) &&
                    dimensionsSet.equals(other.dimensionsSet);
        }
        return false;
    }
}