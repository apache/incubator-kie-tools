/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class TaskShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<BaseTask> {

    public static final SVGShapeViewResources<BaseTask, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseTask, BPMNSVGViewFactory>()
                    .put(NoneTask.class, BPMNSVGViewFactory::noneTask)
                    .put(UserTask.class, BPMNSVGViewFactory::userTask)
                    .put(ScriptTask.class, BPMNSVGViewFactory::scriptTask)
                    .put(BusinessRuleTask.class, BPMNSVGViewFactory::businessRuleTask);

    public static final Map<Class<? extends BaseTask>, SvgDataUriGlyph> GLYPHS =
            new HashMap<Class<? extends BaseTask>, SvgDataUriGlyph>() {{
                put(NoneTask.class, BPMNSVGGlyphFactory.NONE_TASK_GLYPH);
                put(UserTask.class, BPMNSVGGlyphFactory.USER_TASK_GLYPH);
                put(ScriptTask.class, BPMNSVGGlyphFactory.SCRIPT_TASK_GLYPH);
                put(BusinessRuleTask.class, BPMNSVGGlyphFactory.BUSINESS_RULE_TASK_GLYPH);
            }};

    @Override
    public SizeHandler<BaseTask, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 50d)
                .maxWidth(task -> 400d)
                .minHeight(task -> 50d)
                .maxHeight(task -> 400d)
                .build();
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final BaseTask task) {

        return newViewInstance(Optional.ofNullable(task.getDimensionsSet().getWidth()),
                               Optional.ofNullable(task.getDimensionsSet().getHeight()),
                               VIEW_RESOURCES.getResource(factory, task));
    }

    @Override
    public Glyph getGlyph(final Class<? extends BaseTask> type,
                          final String defId) {
        return GLYPHS.get(type);
    }
}