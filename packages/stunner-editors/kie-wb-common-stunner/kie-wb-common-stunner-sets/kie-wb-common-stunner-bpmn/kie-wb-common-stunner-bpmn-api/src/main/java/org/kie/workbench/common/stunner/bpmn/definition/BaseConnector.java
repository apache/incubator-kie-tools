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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;

public abstract class BaseConnector implements BPMNViewDefinition {

    @Category
    public static final transient String category = BPMNCategories.CONNECTING_OBJECTS;

    @Property
    @FormField
    @Valid
    protected BPMNGeneralSet general;

    @Property
    @Valid
    protected BackgroundSet backgroundSet;

    @Property
    protected FontSet fontSet;

    public static final transient String COLOR = "#000000";
    public static final transient String BORDER_COLOR = "#000000";
    public static final Double BORDER_SIZE = 1d;

    @Labels
    protected final Set<String> labels = Stream.of("all",
                                                   "lane_child",
                                                   "ConnectingObjectsMorph",
                                                   "cm_nop",
                                                   "connector")
            .collect(Collectors.toSet());

    protected BaseConnector() {
    }

    protected BaseConnector(final @MapsTo("general") BPMNGeneralSet general,
                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                            final @MapsTo("fontSet") FontSet fontSet
    ) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
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

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         Objects.hashCode(general),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(labels));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseConnector) {
            BaseConnector other = (BaseConnector) o;
            return Objects.equals(general, other.general) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
