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


package org.kie.workbench.common.stunner.bpmn.definition.property.font;

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldType;

@Portable
@Bindable
@FormDefinition(
        startElement = "fontFamily"
)
public class FontSet implements BPMNPropertySet {

    @Property
    @FormField
    private FontFamily fontFamily;

    @Property
    @FormField(
            type = ColorPickerFieldType.class,
            afterElement = "fontFamily"
    )
    private FontColor fontColor;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "fontColor",
            settings = {
                    @FieldParam(name = "min", value = "8.0"),
                    @FieldParam(name = "max", value = "24.0"),
                    @FieldParam(name = "step", value = "1.0")
            }
    )
    private FontSize fontSize;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "fontSize",
            settings = {
                    @FieldParam(name = "min", value = "0.0"),
                    @FieldParam(name = "max", value = "15.0"),
                    @FieldParam(name = "step", value = "1.0")
            }
    )
    private FontBorderSize fontBorderSize;

    @Property
    @FormField(
            type = ColorPickerFieldType.class,
            afterElement = "fontBorderSize"
    )
    private FontBorderColor fontBorderColor;

    public FontSet() {
        this(new FontFamily(),
             new FontColor(),
             new FontSize(),
             new FontBorderSize(),
             new FontBorderColor());
    }

    public FontSet(final @MapsTo("fontFamily") FontFamily fontFamily,
                   final @MapsTo("fontColor") FontColor fontColor,
                   final @MapsTo("fontSize") FontSize fontSize,
                   final @MapsTo("fontBorderSize") FontBorderSize fontBorderSize,
                   final @MapsTo("fontBorderColor") FontBorderColor fontBorderColor) {
        this.fontFamily = fontFamily;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.fontBorderSize = fontBorderSize;
        this.fontBorderColor = fontBorderColor;
    }

    public FontSet(final String fontFamily,
                   final String fontColor,
                   final Double fontSize,
                   final Double fontBorderSize,
                   final String fontBorderColor) {
        this.fontFamily = new FontFamily(fontFamily);
        this.fontColor = new FontColor(fontColor);
        this.fontSize = new FontSize(fontSize);
        this.fontBorderSize = new FontBorderSize(fontBorderSize);
        this.fontBorderColor = new FontBorderColor(fontBorderColor);
    }

    public FontFamily getFontFamily() {
        return fontFamily;
    }

    public FontColor getFontColor() {
        return fontColor;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public FontBorderSize getFontBorderSize() {
        return fontBorderSize;
    }

    public FontBorderColor getFontBorderColor() {
        return fontBorderColor;
    }

    public void setFontFamily(final FontFamily fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontColor(final FontColor fontColor) {
        this.fontColor = fontColor;
    }

    public void setFontSize(final FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void setFontBorderSize(final FontBorderSize fontBorderSize) {
        this.fontBorderSize = fontBorderSize;
    }

    public void setFontBorderColor(FontBorderColor fontBorderColor) {
        this.fontBorderColor = fontBorderColor;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(fontFamily),
                                         Objects.hashCode(fontColor),
                                         Objects.hashCode(fontSize),
                                         Objects.hashCode(fontBorderSize),
                                         Objects.hashCode(fontBorderColor));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FontSet) {
            FontSet other = (FontSet) o;
            return Objects.equals(fontFamily,
                                  other.fontFamily) &&
                    Objects.equals(fontColor,
                                   other.fontColor) &&
                    Objects.equals(fontSize,
                                   other.fontSize) &&
                    Objects.equals(fontBorderSize,
                                   other.fontBorderSize) &&
                    Objects.equals(fontBorderColor,
                                   other.fontBorderColor);
        }
        return false;
    }
}
