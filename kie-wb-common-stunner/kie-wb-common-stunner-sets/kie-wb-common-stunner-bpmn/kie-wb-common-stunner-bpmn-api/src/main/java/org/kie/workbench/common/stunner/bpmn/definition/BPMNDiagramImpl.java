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
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@CanContain(roles = {"all"})
@FormDefinition(
        startElement = "diagramSet",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class BPMNDiagramImpl implements BPMNDiagram {

    @Category
    public static final transient String category = BPMNCategories.CONTAINERS;
    public static final String DIAGRAM_SET = "diagramSet";
    public static final String PROCESS_DATA = "processData";
    public static final String CASE_MANAGEMENT_SET = "caseManagementSet";

    @PropertySet
    @FormField
    @Valid
    private DiagramSet diagramSet;

    @PropertySet
    @FormField(
            afterElement = DIAGRAM_SET
    )
    @Valid
    protected ProcessData processData;

    @PropertySet
    @FormField(
            afterElement = PROCESS_DATA
    )
    protected CaseManagementSet caseManagementSet;

    @PropertySet
    private BackgroundSet backgroundSet;

    @PropertySet
    private FontSet fontSet;

    @PropertySet
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    private final Set<String> labels = new Sets.Builder<String>()
            .add("canContainArtifacts")
            .add("diagram")
            .build();

    public static final Double WIDTH = 950d;
    public static final Double HEIGHT = 950d;

    public BPMNDiagramImpl() {
        this(new DiagramSet(),
             new ProcessData(),
             new CaseManagementSet(),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(WIDTH,
                                        HEIGHT));
    }

    public BPMNDiagramImpl(final @MapsTo(DIAGRAM_SET) DiagramSet diagramSet,
                           final @MapsTo(PROCESS_DATA) ProcessData processData,
                           final @MapsTo(CASE_MANAGEMENT_SET) CaseManagementSet caseManagementSet,
                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                           final @MapsTo("fontSet") FontSet fontSet,
                           final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet) {
        this.diagramSet = diagramSet;
        this.processData = processData;
        this.caseManagementSet = caseManagementSet;
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

    public ProcessData getProcessData() {
        return processData;
    }

    public CaseManagementSet getCaseManagementSet() {
        return caseManagementSet;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDiagramSet(final DiagramSet diagramSet) {
        this.diagramSet = diagramSet;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public void setCaseManagementSet(final CaseManagementSet caseManagementSet) {
        this.caseManagementSet = caseManagementSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    @Override
    public BPMNBaseInfo getGeneral() {
        return null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(diagramSet.hashCode(),
                                         processData.hashCode(),
                                         caseManagementSet.hashCode(),
                                         backgroundSet.hashCode(),
                                         fontSet.hashCode(),
                                         dimensionsSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BPMNDiagramImpl) {
            BPMNDiagramImpl other = (BPMNDiagramImpl) o;
            return diagramSet.equals(other.diagramSet) &&
                    processData.equals(other.processData) &&
                    caseManagementSet.equals(other.caseManagementSet) &&
                    backgroundSet.equals(other.backgroundSet) &&
                    fontSet.equals(other.fontSet) &&
                    dimensionsSet.equals(other.dimensionsSet);
        }
        return false;
    }
}
