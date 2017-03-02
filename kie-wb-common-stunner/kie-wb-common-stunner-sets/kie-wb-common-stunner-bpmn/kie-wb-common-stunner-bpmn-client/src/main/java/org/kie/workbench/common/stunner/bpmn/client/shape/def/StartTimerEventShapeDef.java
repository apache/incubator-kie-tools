/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public final class StartTimerEventShapeDef
        extends AbstractShapeDef<StartTimerEvent>
        implements SVGMutableShapeDef<StartTimerEvent, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final StartTimerEvent element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final StartTimerEvent element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final StartTimerEvent element) {
        return 1;
    }

    @Override
    public String getBorderColor(final StartTimerEvent element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final StartTimerEvent element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final StartTimerEvent element) {
        return 1;
    }

    @Override
    public String getFontFamily(final StartTimerEvent element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final StartTimerEvent element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final StartTimerEvent element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final StartTimerEvent element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final StartTimerEvent element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final StartTimerEvent element) {
        return 0;
    }

    @Override
    public double getWidth(final StartTimerEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public double getHeight(final StartTimerEvent element) {
        return element.getDimensionsSet().getRadius().getValue() * 2;
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final StartTimerEvent element) {
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final StartTimerEvent startTimerEvent) {
        return factory.eventStart(getWidth(startTimerEvent),
                                  getHeight(startTimerEvent),
                                  false);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public GlyphDef<StartTimerEvent> getGlyphDef() {
        return GLYPH_DEF;
    }

    private static final PictureGlyphDef<StartTimerEvent, BPMNPictures> GLYPH_DEF = new PictureGlyphDef<StartTimerEvent, BPMNPictures>() {
        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.EVENT_START;
        }

        @Override
        public String getGlyphDescription(final StartTimerEvent element) {
            return element.getDescription();
        }
    };
}