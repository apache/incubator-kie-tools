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
package org.kie.workbench.common.dmn.api.property.styling;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldType;

@Portable
@Bindable
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "bgColour"
)
public class StylingSet implements DMNPropertySet {

    @Property
    @FormField(
            type = ColorPickerFieldType.class
    )
    @Valid
    private BgColour bgColour;

    @Property
    @FormField(
            type = ColorPickerFieldType.class,
            afterElement = "bgColour"
    )
    @Valid
    private BorderColour borderColour;

    private BorderSize borderSize;

    @Property
    @FormField
    @Valid
    private FontFamily fontFamily;

    @Property
    @FormField(
            type = ColorPickerFieldType.class,
            afterElement = "fontFamily"
    )
    @Valid
    private FontColour fontColour;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "fontColour",
            settings = {
                    @FieldParam(name = "min", value = "8.0"),
                    @FieldParam(name = "max", value = "24.0"),
                    @FieldParam(name = "step", value = "1.0")
            }
    )
    @Valid
    private FontSize fontSize;

    public StylingSet() {
        this(new BgColour(),
             new BorderColour(),
             new BorderSize(),
             new FontFamily(),
             new FontColour(),
             new FontSize());
    }

    public StylingSet(final BgColour bgColour,
                      final BorderColour borderColour,
                      final BorderSize borderSize,
                      final FontFamily fontFamily,
                      final FontColour fontColour,
                      final FontSize fontSize) {
        this.bgColour = bgColour;
        this.borderColour = borderColour;
        this.borderSize = borderSize;
        this.fontFamily = fontFamily;
        this.fontColour = fontColour;
        this.fontSize = fontSize;
    }

    public FontFamily getFontFamily() {
        return fontFamily;
    }

    public FontColour getFontColour() {
        return fontColour;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontFamily(final FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontColour(final FontColour fontColour) {
        this.fontColour = fontColour;
    }

    public void setFontSize(final FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public BgColour getBgColour() {
        return bgColour;
    }

    public BorderColour getBorderColour() {
        return borderColour;
    }

    public BorderSize getBorderSize() {
        return borderSize;
    }

    public void setBgColour(final BgColour bgColour) {
        this.bgColour = bgColour;
    }

    public void setBorderColour(final BorderColour borderColour) {
        this.borderColour = borderColour;
    }

    public void setBorderSize(final BorderSize borderSize) {
        this.borderSize = borderSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StylingSet)) {
            return false;
        }

        final StylingSet that = (StylingSet) o;

        return Objects.equals(bgColour, that.bgColour) &&
                Objects.equals(borderColour, that.borderColour) &&
                Objects.equals(borderSize, that.borderSize) &&
                Objects.equals(fontFamily, that.fontFamily) &&
                Objects.equals(fontColour, that.fontColour) &&
                Objects.equals(fontSize, that.fontSize);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(bgColour != null ? bgColour.hashCode() : 0,
                                         borderColour != null ? borderColour.hashCode() : 0,
                                         borderSize != null ? borderSize.hashCode() : 0,
                                         fontFamily != null ? fontFamily.hashCode() : 0,
                                         fontColour != null ? fontColour.hashCode() : 0,
                                         fontSize != null ? fontSize.hashCode() : 0);
    }
}
