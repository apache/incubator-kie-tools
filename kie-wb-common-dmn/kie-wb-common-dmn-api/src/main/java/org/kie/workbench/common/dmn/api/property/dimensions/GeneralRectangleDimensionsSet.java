/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
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
public class GeneralRectangleDimensionsSet implements RectangleDimensionsSet<GeneralWidth, GeneralHeight> {

    private static final String MIN_WIDTH = "50.0";

    private static final String MAX_WIDTH = "200.0";

    private static final String MIN_HEIGHT = "50.0";

    private static final String MAX_HEIGHT = "200.0";

    private static final String STEP = "25.0";

    @Property
    @FormField(
            type = SliderFieldType.class,
            settings = {
                    @FieldParam(name = "min", value = MIN_WIDTH),
                    @FieldParam(name = "max", value = MAX_WIDTH),
                    @FieldParam(name = "step", value = STEP),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    protected GeneralWidth width;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "width",
            settings = {
                    @FieldParam(name = "min", value = MIN_HEIGHT),
                    @FieldParam(name = "max", value = MAX_HEIGHT),
                    @FieldParam(name = "step", value = STEP),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    protected GeneralHeight height;

    public GeneralRectangleDimensionsSet() {
        this(new GeneralWidth(),
             new GeneralHeight());
    }

    public GeneralRectangleDimensionsSet(final GeneralWidth width,
                                         final GeneralHeight height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public GeneralWidth getWidth() {
        return width;
    }

    @Override
    public void setWidth(final GeneralWidth width) {
        this.width = width;
    }

    @Override
    public GeneralHeight getHeight() {
        return height;
    }

    @Override
    public void setHeight(final GeneralHeight height) {
        this.height = height;
    }

    public double getMinimumWidth() {
        return Double.valueOf(MIN_WIDTH);
    }

    public double getMaximumWidth() {
        return Double.valueOf(MAX_WIDTH);
    }

    public double getMinimumHeight() {
        return Double.valueOf(MIN_HEIGHT);
    }

    public double getMaximumHeight() {
        return Double.valueOf(MAX_HEIGHT);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GeneralRectangleDimensionsSet)) {
            return false;
        }

        final GeneralRectangleDimensionsSet that = (GeneralRectangleDimensionsSet) o;

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