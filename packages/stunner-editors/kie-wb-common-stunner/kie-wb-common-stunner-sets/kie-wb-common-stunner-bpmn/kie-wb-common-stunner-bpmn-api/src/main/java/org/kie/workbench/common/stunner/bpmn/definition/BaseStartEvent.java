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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = StartNoneEvent.class)
public abstract class BaseStartEvent implements BPMNViewDefinition,
                                                DataIOModel {

    @Category
    public static final transient String category = BPMNCategories.START_EVENTS;

    @Labels
    protected final Set<String> labels = new HashSet<String>();

    @Property
    @FormField
    @Valid
    protected BPMNGeneralSet general;

    @Property
    @Valid
    protected BackgroundSet backgroundSet;

    @Property
    protected FontSet fontSet;

    @Property
    private CircleDimensionSet dimensionsSet;

    @Property
    private SimulationAttributeSet simulationSet;

    @Property
    @FormField(
            afterElement = "simulationSet"
    )
    @Valid
    protected AdvancedData advancedData;

    public BaseStartEvent() {
        initLabels();
    }

    public BaseStartEvent(final BPMNGeneralSet general,
                          final BackgroundSet backgroundSet,
                          final FontSet fontSet,
                          final CircleDimensionSet dimensionsSet,
                          final SimulationAttributeSet simulationSet,
                          final AdvancedData advancedData) {
        this();
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.simulationSet = simulationSet;
        this.advancedData = advancedData;
    }

    protected void initLabels() {
        labels.add("all");
        labels.add("lane_child");
        labels.add("Startevents_all");
        labels.add("Startevents_outgoing_all");
        labels.add("sequence_start");
        labels.add("choreography_sequence_start");
        labels.add("to_task_event");
        labels.add("from_task_event");
        labels.add("fromtoall");
        labels.add("StartEventsMorph");
        labels.add("cm_nop");
    }

    @Override
    public boolean hasInputVars() {
        return false;
    }

    @Override
    public boolean isSingleInputVar() {
        return false;
    }

    @Override
    public boolean hasOutputVars() {
        return false;
    }

    @Override
    public boolean isSingleOutputVar() {
        return false;
    }

    public String getCategory() {
        return category;
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

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setGeneral(final BPMNGeneralSet general) {
        this.general = general;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public CircleDimensionSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final CircleDimensionSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    public SimulationAttributeSet getSimulationSet() {
        return simulationSet;
    }

    public void setSimulationSet(SimulationAttributeSet simulationSet) {
        this.simulationSet = simulationSet;
    }

    public AdvancedData getAdvancedData() {
        return advancedData;
    }

    public void setAdvancedData(AdvancedData advancedData) {
        this.advancedData = advancedData;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         Objects.hashCode(general),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(simulationSet),
                                         Objects.hashCode(advancedData),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseStartEvent) {
            BaseStartEvent other = (BaseStartEvent) o;
            return Objects.equals(general, other.general) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(simulationSet, other.simulationSet) &&
                    Objects.equals(advancedData, other.advancedData) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
