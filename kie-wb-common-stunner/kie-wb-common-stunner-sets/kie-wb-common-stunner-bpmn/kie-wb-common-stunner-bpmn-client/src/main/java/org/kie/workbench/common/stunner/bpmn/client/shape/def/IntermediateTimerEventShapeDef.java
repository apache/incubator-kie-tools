/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.BPMNPictures;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class IntermediateTimerEventShapeDef extends AbstractShapeDef<IntermediateTimerEvent>
        implements SVGMutableShapeDef<IntermediateTimerEvent, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final IntermediateTimerEvent element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final IntermediateTimerEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final IntermediateTimerEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final IntermediateTimerEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final IntermediateTimerEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final IntermediateTimerEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final IntermediateTimerEvent element) {
        return 0;
    }

    @Override
    public double getWidth(final IntermediateTimerEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public double getHeight(final IntermediateTimerEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final IntermediateTimerEvent element) {
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final IntermediateTimerEvent intermediateTimerEvent) {
        return factory.eventIntermediate(getWidth(intermediateTimerEvent),
                                         getHeight(intermediateTimerEvent),
                                         false);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public GlyphDef<IntermediateTimerEvent> getGlyphDef() {
        return GLYPH_DEF;
    }

    private static final PictureGlyphDef<IntermediateTimerEvent, BPMNPictures> GLYPH_DEF = new PictureGlyphDef<IntermediateTimerEvent, BPMNPictures>() {
        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.EVENT_INTERMEDIATE_TIMER;
        }

        @Override
        public String getGlyphDescription(final IntermediateTimerEvent element) {
            return element.getDescription();
        }
    };
}
