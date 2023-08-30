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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.DocumentationTextHandler;
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

public class BasePropertyReader implements PropertyReader {

    protected final BaseElement element;
    protected final BPMNShape shape;
    protected final BPMNDiagram diagram;
    protected final double resolutionFactor;

    public BasePropertyReader(final BaseElement element,
                              final BPMNDiagram diagram,
                              final BPMNShape shape,
                              final double resolutionFactor) {
        this.element = element;
        this.diagram = diagram;
        this.shape = shape;
        this.resolutionFactor = resolutionFactor;
    }

    @Override
    public String getDocumentation() {
        return element.getDocumentation().stream()
                .findFirst()
                .map(BasePropertyReader::getDocumentationText)
                .orElse("");
    }

    private static String getDocumentationText(org.eclipse.bpmn2.Documentation documentation) {
        return DocumentationTextHandler.of(documentation).getText();
    }

    @Override
    public String getMetaDataAttributes() {
        return CustomElement.metaDataAttributes.of(element).get();
    }

    @Override
    public String getDescription() {
        return CustomElement.description.of(element).get();
    }

    @Override
    public FontSet getFontSet() {
        final FontFamily fontFamily = new FontFamily();
        final FontColor fontColor = new FontColor(optionalAttribute("fontcolor")
                                                          .orElse(colorsDefaultFont()));
        final FontSize fontSize = new FontSize(optionalAttribute("fontsize")
                                                       .map(Double::parseDouble).orElse(null));
        final FontBorderSize fontBorderSize = new FontBorderSize();
        final FontBorderColor fontBorderColor = new FontBorderColor();
        return new FontSet(fontFamily, fontColor, fontSize, fontBorderSize, fontBorderColor);
    }

    @Override
    public BackgroundSet getBackgroundSet() {
        return new BackgroundSet(
                new BgColor(optionalAttribute("bgcolor")
                                    .orElse(colorsDefaultBg())),
                new BorderColor(optionalAttribute("bordercolor")
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
        if (element.getAnyAttribute().isEmpty()) {
            return Optional.empty();
        }
        final List<String> attributes = Arrays.asList(attributeIds);
        return element.getAnyAttribute().stream()
                .filter(e -> attributes.contains(e.getEStructuralFeature().getName()))
                .map(e -> e.getValue().toString())
                .findFirst();
    }

    @Override
    public Bounds getBounds() {
        if (shape == null) {
            return Bounds.create();
        }
        return computeBounds(shape.getBounds());
    }

    protected Bounds computeBounds(final org.eclipse.dd.dc.Bounds bounds) {
        final double x = bounds.getX() * resolutionFactor;
        final double y = bounds.getY() * resolutionFactor;
        final double width = bounds.getWidth() * resolutionFactor;
        final double height = bounds.getHeight() * resolutionFactor;
        return Bounds.create(x, y, x + width, y + height);
    }

    @Override
    public CircleDimensionSet getCircleDimensionSet() {
        if (shape == null) {
            return new CircleDimensionSet();
        }
        return new CircleDimensionSet(new Radius(
                shape.getBounds().getWidth() * resolutionFactor / 2d));
    }

    @Override
    public RectangleDimensionsSet getRectangleDimensionsSet() {
        if (shape == null) {
            return new RectangleDimensionsSet();
        }
        org.eclipse.dd.dc.Bounds bounds = shape.getBounds();
        return new RectangleDimensionsSet(bounds.getWidth() * resolutionFactor,
                                          bounds.getHeight() * resolutionFactor);
    }

    @Override
    public boolean isExpanded() {
        return shape.isIsExpanded();
    }

    @Override
    public BaseElement getElement() {
        return element;
    }

    @Override
    public BPMNShape getShape() {
        return shape;
    }
}
