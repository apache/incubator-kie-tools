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
package org.kie.workbench.common.dmn.api.property.background;

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
        startElement = "bgColour"
)
public class BackgroundSet implements DMNPropertySet {

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

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "borderColour",
            settings = {
                    @FieldParam(name = "min", value = "0.0"),
                    @FieldParam(name = "max", value = "5.0"),
                    @FieldParam(name = "step", value = "0.5")
            }
    )
    @Valid
    private BorderSize borderSize;

    public BackgroundSet() {
        this(new BgColour(),
             new BorderColour(),
             new BorderSize());
    }

    public BackgroundSet(final BgColour bgColour,
                         final BorderColour borderColour,
                         final BorderSize borderSize) {
        this.bgColour = bgColour;
        this.borderColour = borderColour;
        this.borderSize = borderSize;
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
        if (!(o instanceof BackgroundSet)) {
            return false;
        }

        final BackgroundSet that = (BackgroundSet) o;

        if (!bgColour.equals(that.bgColour)) {
            return false;
        }
        if (!borderColour.equals(that.borderColour)) {
            return false;
        }
        return borderSize.equals(that.borderSize);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(bgColour != null ? bgColour.hashCode() : 0,
                                         borderColour != null ? borderColour.hashCode() : 0,
                                         borderSize != null ? borderSize.hashCode() : 0);
    }
}
