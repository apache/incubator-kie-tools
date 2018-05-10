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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;

public abstract class BasePropertyReader {

    protected final BaseElement element;
    protected final BPMNShape shape;
    protected final BPMNPlane plane;

    public BasePropertyReader(BaseElement element, BPMNPlane plane, BPMNShape shape) {
        this.element = element;
        this.plane = plane;
        this.shape = shape;
    }

    public String getDocumentation() {
        return element.getDocumentation().stream()
                .findFirst()
                .map(org.eclipse.bpmn2.Documentation::getText)
                .orElse("");
    }

    public String getDescription() {
        return CustomElement.description.of(element).get();
    }

    public FontSet getFontSet() {
        return new FontSet(
                new FontFamily(),
                new FontColor(optionalAttribute("fontcolor")//, "color")
                                      .orElse(colorsDefaultFont())),
                new FontSize(optionalAttribute("fontsize")
                                     .map(Double::parseDouble).orElse(null)),
                new FontBorderSize(),
                new FontBorderColor());
    }

    public BackgroundSet getBackgroundSet() {
        return new BackgroundSet(
                new BgColor(optionalAttribute("bgcolor")//, "background-color")
                                    .orElse(colorsDefaultBg())),
                new BorderColor(optionalAttribute(/*"border-color", */"bordercolor")
                                        .orElse(colorsDefaultBr())),
                new BorderSize()
        );
    }

    protected String colorsDefaultBg() {
        return null;
    }

    protected String colorsDefaultBr() {
        return null;
    }

    protected String colorsDefaultFont() {
        return null;
    }

    protected Optional<String> optionalAttribute(String... attributeIds) {
        List<String> attributes = Arrays.asList(attributeIds);
        return element.getAnyAttribute().stream()
                .filter(e -> attributes.contains(e.getEStructuralFeature().getName()))
                .map(e -> e.getValue().toString())
                .findFirst();
    }

    public Bounds getBounds() {
        if (shape == null) {
            return BoundsImpl.build();
        }
        org.eclipse.dd.dc.Bounds bounds = shape.getBounds();
        return BoundsImpl.build(
                bounds.getX(),
                bounds.getY(),
                bounds.getX() + bounds.getWidth(),
                bounds.getY() + bounds.getHeight());
    }

    public CircleDimensionSet getCircleDimensionSet() {
        if (shape == null) {
            return new CircleDimensionSet();
        }
        return new CircleDimensionSet(new Radius(
                shape.getBounds().getWidth() / 2d));
    }

    public RectangleDimensionsSet getRectangleDimensionsSet() {
        if (shape == null) {
            return new RectangleDimensionsSet();
        }
        org.eclipse.dd.dc.Bounds bounds = shape.getBounds();
        return new RectangleDimensionsSet((double) bounds.getWidth(), (double) bounds.getHeight());
    }
}
