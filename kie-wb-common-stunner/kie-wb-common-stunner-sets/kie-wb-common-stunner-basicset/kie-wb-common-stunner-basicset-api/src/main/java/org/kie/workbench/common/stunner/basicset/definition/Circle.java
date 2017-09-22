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
import org.kie.workbench.common.stunner.basicset.definition.property.Name;
import org.kie.workbench.common.stunner.basicset.definition.property.Radius;
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

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = Circle.CircleBuilder.class)
@CanContain(roles = {"all"})
@FormDefinition(startElement = "backgroundSet")
public class Circle implements BasicSetDefinition {

    @Category
    public static final transient String category = Categories.BASIC;

    @Title
    public static final transient String title = "Circle";

    @Description
    public static final transient String description = "A circle";

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
                    @FieldParam(name = "min", value = "25.0"),
                    @FieldParam(name = "max", value = "50.0"),
                    @FieldParam(name = "step", value = "1.0"),
                    @FieldParam(name = "precision", value = "0.0")
            }
    )
    @Valid
    private Radius radius;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add("all");
        add("circle");
    }};

    @NonPortable
    public static class CircleBuilder implements Builder<Circle> {

        public static final String COLOR = "#ffff66";
        public static final String BORDER_COLOR = "#000000";
        public static final Double RADIUS = 50d;
        public static final Double BORDER_SIZE = 1d;

        @Override
        public Circle build() {
            return new Circle(new Name("Circle"),
                              new BackgroundAndBorderSet(COLOR,
                                                         BORDER_COLOR,
                                                         BORDER_SIZE),
                              new FontSet(),
                              new Radius(RADIUS));
        }
    }

    public Circle() {
    }

    public Circle(final @MapsTo("name") Name name,
                  final @MapsTo("backgroundSet") BackgroundAndBorderSet backgroundSet,
                  final @MapsTo("fontSet") FontSet fontSet,
                  final @MapsTo("radius") Radius radius) {
        this.name = name;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.radius = radius;
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

    public void setName(Name name) {
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

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(final Radius radius) {
        this.radius = radius;
    }
}
