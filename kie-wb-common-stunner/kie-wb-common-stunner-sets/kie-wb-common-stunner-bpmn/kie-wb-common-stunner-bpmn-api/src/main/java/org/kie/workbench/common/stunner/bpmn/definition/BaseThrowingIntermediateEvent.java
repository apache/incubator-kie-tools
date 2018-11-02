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
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = IntermediateSignalEventThrowing.class)
public abstract class BaseThrowingIntermediateEvent
        implements BPMNViewDefinition,
                   DataIOModel {

    @Category
    public static final transient String category = BPMNCategories.INTERMEDIATE_EVENTS;

    @Labels
    protected final Set<String> labels = new HashSet<>();

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
    protected CircleDimensionSet dimensionsSet;

    protected void initLabels() {
        labels.add("all");
        labels.add("lane_child");
        labels.add("sequence_start");
        labels.add("sequence_end");
        labels.add("to_task_event");
        labels.add("from_task_event");
        labels.add("fromtoall");
        labels.add("FromEventbasedGateway");
        labels.add("IntermediateEventsMorph");
        labels.add("cmnop");
        labels.add("IntermediateEventThrowing");
    }

    public BaseThrowingIntermediateEvent() {
        initLabels();
    }

    public BaseThrowingIntermediateEvent(final BPMNGeneralSet general,
                                         final BackgroundSet backgroundSet,
                                         final FontSet fontSet,
                                         final CircleDimensionSet dimensionsSet) {
        this();
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
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

    public BPMNGeneralSet getGeneral() {
        return general;
    }

    public void setGeneral(BPMNGeneralSet general) {
        this.general = general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public CircleDimensionSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(CircleDimensionSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    public Set<String> getLabels() {
        return labels;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         Objects.hashCode(general),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseThrowingIntermediateEvent) {
            BaseThrowingIntermediateEvent other = (BaseThrowingIntermediateEvent) o;
            return Objects.equals(general, other.general) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
