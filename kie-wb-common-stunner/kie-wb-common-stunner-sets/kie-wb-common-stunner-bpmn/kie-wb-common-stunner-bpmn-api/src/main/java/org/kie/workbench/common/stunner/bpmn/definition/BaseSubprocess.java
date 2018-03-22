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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = ReusableSubprocess.class, targets = {BaseTask.class})
public abstract class BaseSubprocess implements BPMNViewDefinition {

    @Category
    public static final transient String category = BPMNCategories.ACTIVITIES;

    @PropertySet
    @FormField
    @Valid
    protected BPMNGeneralSet general;

    @PropertySet
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    protected FontSet fontSet;

    @PropertySet
    protected SimulationSet simulationSet;

    @PropertySet
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    protected final Set<String> labels = new HashSet<>();

    protected BaseSubprocess() {
        initLabels();
    }

    public BaseSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                          final @MapsTo("fontSet") FontSet fontSet,
                          final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                          final @MapsTo("simulationSet") SimulationSet simulationSet) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.simulationSet = simulationSet;
        this.initLabels();
    }

    protected void initLabels() {
        labels.add("all");
        labels.add("sequence_start");
        labels.add("sequence_end");
        labels.add("messageflow_start");
        labels.add("messageflow_end");
        labels.add("to_task_event");
        labels.add("from_task_event");
        labels.add("fromtoall");
        labels.add("ActivitiesMorph");
        labels.add("cm_stage");
    }

    public String getCategory() {
        return category;
    }

    public BPMNGeneralSet getGeneral() {
        return general;
    }

    public void setGeneral(final BPMNGeneralSet general) {
        this.general = general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public SimulationSet getSimulationSet() {
        return simulationSet;
    }

    public void setSimulationSet(final SimulationSet simulationSet) {
        this.simulationSet = simulationSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    public Set<String> getLabels() {
        return labels;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(general),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(simulationSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseSubprocess) {
            BaseSubprocess other = (BaseSubprocess) o;
            return Objects.equals(general,
                                  other.general) &&
                    Objects.equals(backgroundSet,
                                   other.backgroundSet) &&
                    Objects.equals(fontSet,
                                   other.fontSet) &&
                    Objects.equals(simulationSet,
                                   other.simulationSet) &&
                    Objects.equals(dimensionsSet,
                                   other.dimensionsSet) &&
                    Objects.equals(labels,
                                   other.labels);
        }
        return false;
    }
}
