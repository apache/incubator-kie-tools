/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.BaseDimensionedShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public final class CaseManagementSvgUserTaskShapeDef extends BaseDimensionedShapeDef
        implements CaseManagementSvgShapeDef<BaseTask> {

    public static final SVGShapeViewResources<BaseTask, CaseManagementSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseTask, CaseManagementSVGViewFactory>()
                    .put(UserTask.class, CaseManagementSVGViewFactory::task);

    public static final Map<Class<? extends BaseTask>, Glyph> GLYPHS =
            new HashMap<Class<? extends BaseTask>, Glyph>() {{
                put(UserTask.class, CaseManagementSVGGlyphFactory.TASK_GLYPH);
            }};

    private static HasTitle.Position getSubprocessTextPosition(final BaseTask bean) {
        return HasTitle.Position.CENTER;
    }

    @Override
    public FontHandler<BaseTask, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .position(CaseManagementSvgUserTaskShapeDef::getSubprocessTextPosition)
                .build();
    }

    @Override
    public SizeHandler<BaseTask, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 50d)
                .minHeight(task -> 50d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final CaseManagementSVGViewFactory factory, final BaseTask obj) {
        return newViewInstance(Optional.ofNullable(obj.getDimensionsSet().getWidth()),
                               Optional.ofNullable(obj.getDimensionsSet().getHeight()),
                               VIEW_RESOURCES.getResource(factory, obj));
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseTask> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}