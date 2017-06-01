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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import org.kie.workbench.common.stunner.bpmn.client.shape.BPMNPictures;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public class NullShapeDef<W extends BPMNDefinition> implements MutableShapeDef<W> {

    private final PictureGlyphDef<W, BPMNPictures> NULL_GLYPH_DEF =
            new PictureGlyphDef<W, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final BPMNDefinition element) {
                    return "";
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.GLYPH_OOME_HACK;
                }
            };

    @Override
    public double getAlpha(final W element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final W element) {
        return "";
    }

    @Override
    public double getBackgroundAlpha(final W element) {
        return 1d;
    }

    @Override
    public String getBorderColor(final W element) {
        return "";
    }

    @Override
    public double getBorderSize(final W element) {
        return 1d;
    }

    @Override
    public double getBorderAlpha(final W element) {
        return 1d;
    }

    @Override
    public String getFontFamily(final W element) {
        return "";
    }

    @Override
    public String getFontColor(final W element) {
        return "";
    }

    @Override
    public String getFontBorderColor(final W element) {
        return "";
    }

    @Override
    public double getFontSize(final W element) {
        return 0;
    }

    @Override
    public double getFontBorderSize(final W element) {
        return 0;
    }

    @Override
    public HasTitle.Position getFontPosition(final W element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final W element) {
        return 0;
    }

    @Override
    public GlyphDef<W> getGlyphDef() {
        return NULL_GLYPH_DEF;
    }
}
