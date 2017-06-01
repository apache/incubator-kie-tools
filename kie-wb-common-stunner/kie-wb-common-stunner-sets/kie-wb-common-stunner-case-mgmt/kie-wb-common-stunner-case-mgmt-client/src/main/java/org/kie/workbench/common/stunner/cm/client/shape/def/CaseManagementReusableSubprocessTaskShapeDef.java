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

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.shape.BPMNPictures;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.def.HasChildShapeDefs;
import org.kie.workbench.common.stunner.shapes.def.RectangleShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureShapeDef;

public final class CaseManagementReusableSubprocessTaskShapeDef
        extends AbstractShapeDef<ReusableSubprocess>
        implements RectangleShapeDef<ReusableSubprocess>,
                   HasChildShapeDefs<ReusableSubprocess> {

    private static final PictureGlyphDef<ReusableSubprocess, BPMNPictures> TASK_GLYPH_DEF = new PictureGlyphDef<ReusableSubprocess, BPMNPictures>() {

        @Override
        public String getGlyphDescription(final ReusableSubprocess element) {
            return element.getDescription();
        }

        @Override
        public BPMNPictures getSource(final Class<?> type) {
            return BPMNPictures.TASK_BUSINESS_RULE;
        }
    };

    @Override
    public double getAlpha(final ReusableSubprocess element) {
        return 1d;
    }

    @Override
    public String getBackgroundColor(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final ReusableSubprocess element) {
        return 1;
    }

    @Override
    public String getBorderColor(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final ReusableSubprocess element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final ReusableSubprocess element) {
        return 1;
    }

    @Override
    public String getFontFamily(final ReusableSubprocess element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final ReusableSubprocess element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public String getFontBorderColor(final ReusableSubprocess element) {
        return element.getFontSet().getFontBorderColor().getValue();
    }

    @Override
    public double getFontSize(final ReusableSubprocess element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final ReusableSubprocess element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final ReusableSubprocess element) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public double getFontRotation(final ReusableSubprocess element) {
        return 0;
    }

    @Override
    public GlyphDef<ReusableSubprocess> getGlyphDef() {
        return TASK_GLYPH_DEF;
    }

    @Override
    public Map<ShapeDef<ReusableSubprocess>, HasChildren.Layout> getChildShapeDefs() {
        return new HashMap<ShapeDef<ReusableSubprocess>, HasChildren.Layout>() {{
            put(new ReusableSubprocessTaskTypeProxy(),
                HasChildren.Layout.TOP);
        }};
    }

    @Override
    public double getWidth(final ReusableSubprocess element) {
        return element.getDimensionsSet().getWidth().getValue();
    }

    @Override
    public double getHeight(final ReusableSubprocess element) {
        return element.getDimensionsSet().getHeight().getValue();
    }

    @Override
    public double getCornerRadius(final ReusableSubprocess element) {
        return 5;
    }

    public final class ReusableSubprocessTaskTypeProxy extends AbstractShapeDef<ReusableSubprocess> implements PictureShapeDef<ReusableSubprocess, BPMNPictures> {

        @Override
        public BPMNPictures getPictureSource(final ReusableSubprocess element) {
            return BPMNPictures.TASK_BUSINESS_RULE;
        }

        @Override
        public double getWidth(final ReusableSubprocess element) {
            return 15d;
        }

        @Override
        public double getHeight(final ReusableSubprocess element) {
            return 15d;
        }
    }
}
