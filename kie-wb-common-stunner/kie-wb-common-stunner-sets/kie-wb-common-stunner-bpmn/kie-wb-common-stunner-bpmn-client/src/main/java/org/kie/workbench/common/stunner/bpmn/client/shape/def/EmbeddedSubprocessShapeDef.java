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
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGMutableShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class EmbeddedSubprocessShapeDef
        extends AbstractShapeDef<EmbeddedSubprocess>
        implements SVGMutableShapeDef<EmbeddedSubprocess, BPMNSVGViewFactory> {

    @Override
    public double getAlpha(final EmbeddedSubprocess element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final EmbeddedSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final EmbeddedSubprocess element) {
        return 0.7d;
    }

    @Override
    public String getBorderColor(final EmbeddedSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final EmbeddedSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final EmbeddedSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final EmbeddedSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final EmbeddedSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final EmbeddedSubprocess element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final EmbeddedSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final EmbeddedSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final EmbeddedSubprocess element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final EmbeddedSubprocess element) {
        return 0;
    }

    @Override
    public double getWidth(final EmbeddedSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final EmbeddedSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public boolean isSVGViewVisible(final String viewName,
                                    final EmbeddedSubprocess element) {
        return false;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final EmbeddedSubprocess lane) {
        return factory.subprocessEmbedded(getWidth(lane),
                                          getHeight(lane),
                                          true);
    }

    @Override
    public Class<BPMNSVGViewFactory> getViewFactoryType() {
        return BPMNSVGViewFactory.class;
    }

    @Override
    public GlyphDef<EmbeddedSubprocess> getGlyphDef() {
        return GLYPH_DEF;
    }

    private static final PictureGlyphDef<EmbeddedSubprocess, BPMNPictures> GLYPH_DEF = new PictureGlyphDef<EmbeddedSubprocess, BPMNPictures>() {
        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.SUB_PROCESS_EMBEDDED;
        }

        @Override
        public String getGlyphDescription(final EmbeddedSubprocess element) {
            return element.getDescription();
        }
    };
}