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
import java.util.Set;

import javax.validation.Valid;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = EndNoneEvent.class)
public abstract class BaseEndEvent implements BPMNDefinition,
                                              DataIOModel {

    @Category
    public static final transient String category = Categories.EVENTS;

    @PropertySet
    @FormField
    @Valid
    protected BPMNGeneralSet general;

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected DataIOSet dataIOSet;

    @PropertySet
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    protected FontSet fontSet;

    @PropertySet
    protected CircleDimensionSet dimensionsSet;

    @Labels
    protected final Set<String> labels = new HashSet<String>() {{
        add("all");
        add("sequence_end");
        add("to_task_event");
        add("from_task_event");
        add("fromtoall");
        add("choreography_sequence_end");
        add("Endevents_all");
        add("EndEventsMorph");
        add("cm_nop");
    }};

    static abstract class BaseEndEventBuilder<T extends BaseEndEvent> implements Builder<T> {

        public static final String BG_COLOR = "#ff6347";
        public static final Double BORDER_SIZE = 1.5d;
        public static final String BORDER_COLOR = "#000000";
        public static final Double RADIUS = 14d;
    }

    public BaseEndEvent() {
    }

    public BaseEndEvent(final BPMNGeneralSet general,
                        final DataIOSet dataIOSet,
                        final BackgroundSet backgroundSet,
                        final FontSet fontSet,
                        final CircleDimensionSet dimensionsSet) {
        this.general = general;
        this.dataIOSet = dataIOSet;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
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

    public DataIOSet getDataIOSet() {
        return dataIOSet;
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

    public void setDataIOSet(final DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
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

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(general.hashCode(),
                                         dataIOSet.hashCode(),
                                         backgroundSet.hashCode(),
                                         fontSet.hashCode(),
                                         dimensionsSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseEndEvent) {
            BaseEndEvent other = (BaseEndEvent) o;
            return general.equals(other.general) &&
                    dataIOSet.equals(other.dataIOSet) &&
                    backgroundSet.equals(other.backgroundSet) &&
                    fontSet.equals(other.fontSet) &&
                    dimensionsSet.equals(other.dimensionsSet);
        }
        return false;
    }
}
