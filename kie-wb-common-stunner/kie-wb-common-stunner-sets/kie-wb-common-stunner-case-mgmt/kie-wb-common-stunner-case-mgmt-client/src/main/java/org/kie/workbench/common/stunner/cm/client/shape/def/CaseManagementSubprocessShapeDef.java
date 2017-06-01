/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public final class CaseManagementSubprocessShapeDef
        extends AbstractShapeDef<BaseSubprocess>
        implements StageShapeDef<BaseSubprocess> {

    private static final PictureGlyphDef<BaseSubprocess, BPMNPictures> SUBPROCESS_GLYPH_DEF =
            new PictureGlyphDef<BaseSubprocess, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final BaseSubprocess element) {
                    return element.getDescription();
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.GLYPH_OOME_HACK;
                }
            };

    @Override
    public double getAlpha(final BaseSubprocess element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final BaseSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final BaseSubprocess element) {
        return 1;
    }

    @Override
    public String getBorderColor(final BaseSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final BaseSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final BaseSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final BaseSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final BaseSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final BaseSubprocess element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final BaseSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final BaseSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final BaseSubprocess element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final BaseSubprocess element) {
        return 0;
    }

    @Override
    public double getWidth(final BaseSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final BaseSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getVOffset(final BaseSubprocess element) {
        return 20.0;
    }

    @Override
    public GlyphDef<BaseSubprocess> getGlyphDef() {
        return SUBPROCESS_GLYPH_DEF;
    }
}
