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

package org.kie.workbench.common.stunner.basicset.definition;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.type.SliderFieldType;
import org.kie.workbench.common.stunner.basicset.definition.property.Height;
import org.kie.workbench.common.stunner.basicset.definition.property.Name;
import org.kie.workbench.common.stunner.basicset.definition.property.Width;
import org.kie.workbench.common.stunner.basicset.definition.property.background.BackgroundAndBorderSet;
import org.kie.workbench.common.stunner.basicset.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = Rectangle.RectangleBuilder.class)
@CanContain(roles = {"all"})
@CanDock(roles = {"all"})
@FormDefinition(startElement = "backgroundSet")
public class Rectangle implements BasicSetDefinition {

    @Category
    public static final transient String category = Categories.BASIC;

    @Title
    public static final transient String title = "Rectangle";

    @Description
    public static final transient String description = "A rectangle";

    @Property
    @FormField(afterElement = "fontSet")
    @Valid
    private Name name;

    @PropertySet
    @FormField
    @Valid
    private BackgroundAndBorderSet backgroundSet;

    @PropertySet
    @FormField(afterElement = "backgroundSet")
    @Valid
    private FontSet fontSet;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "name",
            settings = {
                    @FieldParam(name = "min", value = "100.0"),
                    @FieldParam(name = "max", value = "300.0"),
                    @FieldParam(name = "step", value = "10.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    private Width width;

    @Property
    @FormField(
            type = SliderFieldType.class,
            afterElement = "width",
            settings = {
                    @FieldParam(name = "min", value = "40.0"),
                    @FieldParam(name = "max", value = "100.0"),
                    @FieldParam(name = "step", value = "5.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    private Height height;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add("all");
        add("rectangle");
    }};

    @NonPortable
    public static class RectangleBuilder implements Builder<Rectangle> {

        public static final String COLOR = "#dfeff8";
        public static final String BORDER_COLOR = "#000000";
        public static final Double WIDTH = 150d;
        public static final Double HEIGHT = 100d;
        public static final Double BORDER_SIZE = 1d;

        @Override
        public Rectangle build() {
            return new Rectangle(new Name("Rectangle"),
                                 new BackgroundAndBorderSet(COLOR,
                                                            BORDER_COLOR,
                                                            BORDER_SIZE),
                                 new FontSet(),
                                 new Width(WIDTH),
                                 new Height(HEIGHT));
        }
    }

    public Rectangle() {
    }

    public Rectangle(final @MapsTo("name") Name name,
                     final @MapsTo("backgroundSet") BackgroundAndBorderSet backgroundSet,
                     final @MapsTo("fontSet") FontSet fontSet,
                     final @MapsTo("width") Width width,
                     final @MapsTo("height") Height height) {
        this.name = name;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.width = width;
        this.height = height;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    public BackgroundAndBorderSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundAndBorderSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
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
}
