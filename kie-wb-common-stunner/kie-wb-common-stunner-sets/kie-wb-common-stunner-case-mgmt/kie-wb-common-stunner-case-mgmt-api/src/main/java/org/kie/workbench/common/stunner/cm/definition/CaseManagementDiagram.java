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
package org.kie.workbench.common.stunner.cm.definition;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNBaseInfo;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = CaseManagementDiagram.CaseManagementDiagramBuilder.class)
@CanContain(roles = {"cm_stage", "cm_nop"})
@FormDefinition(
        startElement = "diagramSet",
        policy = FieldPolicy.ONLY_MARKED
)
// This is a clone of org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram with different containment rules.
// Unfortunately extending the foregoing and providing a new @CanContain annotation leads to problems with identifying
// Factories for Definitions; as CM's BindableDefinitionAdapterProxy is then generated with support for the super-class.
// This then leads the unmarshalling of model Elements to Definitions to use the wrong Factory and hence fail.
public class CaseManagementDiagram implements BPMNDiagram {

    @Category
    public static final transient String category = BPMNCategories.CONTAINERS;

    @PropertySet
    @FormField
    @Valid
    private DiagramSet diagramSet;

    @PropertySet
    @FormField(
            afterElement = "diagramSet"
    )
    @Valid
    protected ProcessData processData;

    @PropertySet
    private BackgroundSet backgroundSet;

    @PropertySet
    private FontSet fontSet;

    @PropertySet
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add("canContainArtifacts");
        add("diagram");
    }};

    @NonPortable
    public static class CaseManagementDiagramBuilder implements Builder<CaseManagementDiagram> {

        @Override
        public CaseManagementDiagram build() {
            return new CaseManagementDiagram(new DiagramSet(""),
                                             new ProcessData(),
                                             new BackgroundSet(),
                                             new FontSet(),
                                             new RectangleDimensionsSet());
        }
    }

    public CaseManagementDiagram() {
    }

    public CaseManagementDiagram(final @MapsTo("diagramSet") DiagramSet diagramSet,
                                 final @MapsTo("processData") ProcessData processData,
                                 final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                 final @MapsTo("fontSet") FontSet fontSet,
                                 final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        this.diagramSet = diagramSet;
        this.processData = processData;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    public String getCategory() {
        return category;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public DiagramSet getDiagramSet() {
        return diagramSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
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

    public void setDiagramSet(final DiagramSet diagramSet) {
        this.diagramSet = diagramSet;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    @Override
    public BPMNBaseInfo getGeneral() {
        return getDiagramSet();
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(diagramSet.hashCode(),
                                         processData.hashCode(),
                                         backgroundSet.hashCode(),
                                         fontSet.hashCode(),
                                         dimensionsSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseManagementDiagram) {
            CaseManagementDiagram other = (CaseManagementDiagram) o;
            return diagramSet.equals(other.diagramSet) &&
                    processData.equals(other.processData) &&
                    backgroundSet.equals(other.backgroundSet) &&
                    fontSet.equals(other.fontSet) &&
                    dimensionsSet.equals(other.dimensionsSet);
        }
        return false;
    }
}
