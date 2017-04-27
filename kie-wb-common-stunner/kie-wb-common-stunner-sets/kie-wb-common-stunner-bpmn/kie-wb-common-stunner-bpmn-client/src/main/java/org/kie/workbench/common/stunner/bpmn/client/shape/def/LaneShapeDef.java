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
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.shape.def.BPMNPictures;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class LaneShapeDef
        extends AbstractShapeDef<Lane>
        implements SVGMutableShapeDef<Lane, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final Lane element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final Lane element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final Lane element) {
        return 0.7d;
    }

    @Override
    public String getBorderColor(final Lane element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final Lane element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final Lane element) {
        return 1;
    }

    @Override
    public String getFontFamily(final Lane element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final Lane element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final Lane element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final Lane element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final Lane element) {
        return HasTitle.Position.LEFT;
    }

    @Override
    public double getFontRotation(final Lane element) {
        return 270;
    }

    @Override
    public double getWidth(final Lane element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final Lane element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final Lane element) {
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final Lane lane) {
        return factory.lane(getWidth(lane),
                            getHeight(lane),
                            true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public GlyphDef<Lane> getGlyphDef() {
        return GLYPH_DEF;
    }

    private static final PictureGlyphDef<Lane, BPMNPictures> GLYPH_DEF = new PictureGlyphDef<Lane, BPMNPictures>() {
        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.LANE;
        }

        @Override
        public String getGlyphDescription(final Lane element) {
            return element.getDescription();
        }
    };
}