/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;

import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;

public abstract class BaseIntermediateEvent
        implements BPMNViewDefinition,
                   DataIOModel {

    @Labels
    protected final Set<String> labels = new HashSet<>();

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
    protected CircleDimensionSet dimensionsSet;

    @Property
    @FormField(afterElement = "executionSet")
    @Valid
    protected DataIOSet dataIOSet;

    @Property
    @FormField(
            afterElement = "dataIOSet"
    )
    @Valid
    protected AdvancedData advancedData;

    public BaseIntermediateEvent() {
        initLabels();
    }

    public BaseIntermediateEvent(final BPMNGeneralSet general,
                                 final BackgroundSet backgroundSet,
                                 final FontSet fontSet,
                                 final CircleDimensionSet dimensionsSet,
                                 final DataIOSet dataIOSet,
                                 final AdvancedData advancedData) {
        this();
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.advancedData = advancedData;
        this.dataIOSet = dataIOSet;
    }

    protected abstract void initLabels();

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

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    public AdvancedData getAdvancedData() {
        return advancedData;
    }

    public void setAdvancedData(AdvancedData advancedData) {
        this.advancedData = advancedData;
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
                                         Objects.hashCode(advancedData),
                                         Objects.hashCode(dataIOSet),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BaseIntermediateEvent) {
            BaseIntermediateEvent other = (BaseIntermediateEvent) o;
            return Objects.equals(general, other.general) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(advancedData, other.advancedData) &&
                    Objects.equals(dataIOSet, other.dataIOSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
