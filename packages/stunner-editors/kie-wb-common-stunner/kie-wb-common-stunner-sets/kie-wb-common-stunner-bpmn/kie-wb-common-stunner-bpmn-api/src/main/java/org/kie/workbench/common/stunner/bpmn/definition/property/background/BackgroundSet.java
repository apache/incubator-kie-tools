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

package org.kie.workbench.common.stunner.bpmn.definition.property.background;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldType;

@Portable
@Bindable
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "bgColor"
)
public class BackgroundSet implements BPMNPropertySet {

    @Property
    @FormField(
            type = ColorPickerFieldType.class
    )
    @Valid
    private BgColor bgColor;

    @Property
    @FormField(
            type = ColorPickerFieldType.class,
            afterElement = "bgColor"
    )
    @Valid
    private BorderColor borderColor;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "borderColor",
            settings = {
                    @FieldParam(name = "min", value = "0.0"),
                    @FieldParam(name = "max", value = "5.0"),
                    @FieldParam(name = "step", value = "0.5")
            }
    )
    @Valid
    private BorderSize borderSize;

    public BackgroundSet() {
        this(new BgColor(),
             new BorderColor(),
             new BorderSize());
    }

    public BackgroundSet(final @MapsTo("bgColor") BgColor bgColor,
                         final @MapsTo("borderColor") BorderColor borderColor,
                         final @MapsTo("borderSize") BorderSize borderSize) {
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        this.borderSize = borderSize;
    }

    public BackgroundSet(final String bgColor,
                         final String borderColor,
                         final Double borderSize) {
        this.bgColor = new BgColor(bgColor);
        this.borderColor = new BorderColor(borderColor);
        this.borderSize = new BorderSize(borderSize);
    }

    public BgColor getBgColor() {
        return bgColor;
    }

    public BorderColor getBorderColor() {
        return borderColor;
    }

    public BorderSize getBorderSize() {
        return borderSize;
    }

    public void setBgColor(final BgColor bgColor) {
        this.bgColor = bgColor;
    }

    public void setBorderColor(final BorderColor borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderSize(final BorderSize borderSize) {
        this.borderSize = borderSize;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(bgColor),
                                         Objects.hashCode(borderColor),
                                         Objects.hashCode(borderSize));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BackgroundSet) {
            BackgroundSet other = (BackgroundSet) o;
            return Objects.equals(bgColor,
                                  other.bgColor) &&
                    Objects.equals(borderColor,
                                   other.borderColor) &&
                    Objects.equals(borderSize,
                                   other.borderSize);
        }
        return false;
    }
}
