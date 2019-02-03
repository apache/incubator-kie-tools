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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import java.util.Map;
import java.util.Optional;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.BaseDimensionedShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public final class CaseManagementSvgSubprocessShapeDef extends BaseDimensionedShapeDef
        implements CaseManagementSvgShapeDef<ReusableSubprocess> {

    public static final SVGShapeViewResources<ReusableSubprocess, CaseManagementSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<ReusableSubprocess, CaseManagementSVGViewFactory>()
                    .put(ProcessReusableSubprocess.class, CaseManagementSVGViewFactory::subprocess)
                    .put(CaseReusableSubprocess.class, CaseManagementSVGViewFactory::subcase);

    public static final Map<Class<? extends ReusableSubprocess>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends ReusableSubprocess>, Glyph>()
                    .put(ProcessReusableSubprocess.class, CaseManagementSVGGlyphFactory.SUBPROCESS_GLYPH)
                    .put(CaseReusableSubprocess.class, CaseManagementSVGGlyphFactory.SUBCASE_GLYPH).build();

    private static HasTitle.Position getSubprocessTextPosition(final BaseSubprocess bean) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public FontHandler<ReusableSubprocess, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(CaseManagementSvgSubprocessShapeDef::getSubprocessTextPosition)
                .build();
    }

    @Override
    public SizeHandler<ReusableSubprocess, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(e -> e.getDimensionsSet().getWidth().getValue())
                .height(e -> e.getDimensionsSet().getHeight().getValue())
                .minWidth(e -> 50d)
                .minHeight(e -> 50d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final CaseManagementSVGViewFactory factory,
                                           final ReusableSubprocess bean) {
        return newViewInstance(Optional.ofNullable(bean.getDimensionsSet().getWidth()),
                               Optional.ofNullable(bean.getDimensionsSet().getHeight()),
                               VIEW_RESOURCES.getResource(factory, bean));
    }

    @Override
    public Glyph getGlyph(final Class<? extends ReusableSubprocess> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}