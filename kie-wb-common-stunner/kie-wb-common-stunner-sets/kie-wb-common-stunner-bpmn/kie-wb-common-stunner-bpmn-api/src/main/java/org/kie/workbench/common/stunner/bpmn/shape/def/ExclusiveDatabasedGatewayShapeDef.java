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

package org.kie.workbench.common.stunner.bpmn.shape.def;

import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.definition.shape.AbstractShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.shapes.def.PolygonShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureGlyphDef;

public final class ExclusiveDatabasedGatewayShapeDef
        extends AbstractShapeDef<ExclusiveDatabasedGateway>
        implements
        PolygonShapeDef<ExclusiveDatabasedGateway> {

    @Override
    public double getRadius(final ExclusiveDatabasedGateway element) {
        return element.getDimensionsSet().getRadius().getValue();
    }

    @Override
    public String getBackgroundColor(final ExclusiveDatabasedGateway element) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public double getBackgroundAlpha(final ExclusiveDatabasedGateway element) {
        return 1;
    }

    @Override
    public String getBorderColor(final ExclusiveDatabasedGateway element) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize(final ExclusiveDatabasedGateway element) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public double getBorderAlpha(final ExclusiveDatabasedGateway element) {
        return 1;
    }

    @Override
    public String getFontFamily(final ExclusiveDatabasedGateway element) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor(final ExclusiveDatabasedGateway element) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize(final ExclusiveDatabasedGateway element) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public double getFontBorderSize(final ExclusiveDatabasedGateway element) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public HasTitle.Position getFontPosition(final ExclusiveDatabasedGateway element) {
        return HasTitle.Position.BOTTOM;
    }

    @Override
    public double getFontRotation(final ExclusiveDatabasedGateway element) {
        return 0;
    }

    private static final PictureGlyphDef<ExclusiveDatabasedGateway, BPMNPictures> PICTURE_GLYPH_DEF =
            new PictureGlyphDef<ExclusiveDatabasedGateway, BPMNPictures>() {

                @Override
                public String getGlyphDescription(final ExclusiveDatabasedGateway element) {
                    return element.getDescription();
                }

                @Override
                public BPMNPictures getSource(final Class<?> type) {
                    return BPMNPictures.CANCEL;
                }
            };

    @Override
    public GlyphDef<ExclusiveDatabasedGateway> getGlyphDef() {
        return PICTURE_GLYPH_DEF;
    }
}
