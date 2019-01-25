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
package org.kie.workbench.common.dmn.api.property.font;

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
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.kie.workbench.common.stunner.forms.model.ColorPickerFieldType;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "fontFamily"
)
public class FontSet implements DMNPropertySet {

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

    public FontSet() {
        this(new FontFamily(),
             new FontColour(),
             new FontSize());
    }

    public FontSet(final FontFamily fontFamily,
                   final FontColour fontColour,
                   final FontSize fontSize) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FontSet)) {
            return false;
        }

        final FontSet fontSet = (FontSet) o;

        if (fontFamily != null ? !fontFamily.equals(fontSet.fontFamily) : fontSet.fontFamily != null) {
            return false;
        }
        if (fontColour != null ? !fontColour.equals(fontSet.fontColour) : fontSet.fontColour != null) {
            return false;
        }
        return fontSize != null ? fontSize.equals(fontSet.fontSize) : fontSet.fontSize == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(fontFamily != null ? fontFamily.hashCode() : 0,
                                         fontColour != null ? fontColour.hashCode() : 0,
                                         fontSize != null ? fontSize.hashCode() : 0);
    }
}
