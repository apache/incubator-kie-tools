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
package org.kie.workbench.common.dmn.api.property.dimensions;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(
        startElement = "width"
)
public class RectangleDimensionsSet implements DMNPropertySet {

    @Property
    @FormField(
            type = SliderFieldType.class,
            settings = {
                    @FieldParam(name = "min", value = "50.0"),
                    @FieldParam(name = "max", value = "200.0"),
                    @FieldParam(name = "step", value = "25.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    protected Width width;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "width",
            settings = {
                    @FieldParam(name = "min", value = "50.0"),
                    @FieldParam(name = "max", value = "200.0"),
                    @FieldParam(name = "step", value = "25.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    protected Height height;

    public RectangleDimensionsSet() {
        this(new Width(),
             new Height());
    }

    public RectangleDimensionsSet(final @MapsTo("width") Width width,
                                  final @MapsTo("height") Height height) {
        this.width = width;
        this.height = height;
    }

    public Width getWidth() {
        return width;
    }

    public void setWidth(final Width width) {
        this.width = width;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(final Height height) {
        this.height = height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RectangleDimensionsSet)) {
            return false;
        }

        final RectangleDimensionsSet that = (RectangleDimensionsSet) o;

        if (width != null ? !width.equals(that.width) : that.width != null) {
            return false;
        }
        return height != null ? height.equals(that.height) : that.height == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(width != null ? width.hashCode() : 0,
                                         height != null ? height.hashCode() : 0);
    }
}