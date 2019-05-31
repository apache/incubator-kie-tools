/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.TaskViewHandler;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.CompositeShapeViewHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class TaskShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<BaseTask> {

    public static final double ICON_WIDTH = 35d;

    public static final SVGShapeViewResources<BaseTask, BPMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<BaseTask, BPMNSVGViewFactory>()
                    .put(NoneTask.class, BPMNSVGViewFactory::noneTask)
                    .put(GenericServiceTask.class, BPMNSVGViewFactory::genericServiceTask)
                    .put(UserTask.class, BPMNSVGViewFactory::userTask)
                    .put(ScriptTask.class, BPMNSVGViewFactory::scriptTask)
                    .put(BusinessRuleTask.class, BPMNSVGViewFactory::businessRuleTask);

    public static final Map<Class<? extends BaseTask>, Glyph> GLYPHS =
            new Maps.Builder<Class<? extends BaseTask>, Glyph>()
                    .put(NoneTask.class, BPMNGlyphFactory.TASK)
                    .put(GenericServiceTask.class, BPMNGlyphFactory.TASK_GENERIC_SERVICE)
                    .put(UserTask.class, BPMNGlyphFactory.TASK_USER)
                    .put(ScriptTask.class, BPMNGlyphFactory.TASK_SCRIPT)
                    .put(BusinessRuleTask.class, BPMNGlyphFactory.TASK_BUSINESS_RULE)
                    .build();

    private static final Map<Enum, Double> DEFAULT_TASK_MARGINS_WITH_ICON =
            new Maps.Builder<Enum, Double>()
                    .put(HorizontalAlignment.LEFT, ICON_WIDTH)
                    .build();

    private static Map<Class<? extends BaseTask>, Map<Enum, Double>> taskMarginSuppliers =
            new Maps.Builder()
                    .put(NoneTask.class, null)
                    .put(UserTask.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .put(ScriptTask.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .put(BusinessRuleTask.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .put(ServiceTask.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .put(GenericServiceTask.class, DEFAULT_TASK_MARGINS_WITH_ICON)
                    .build();

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
    @SuppressWarnings("unchecked")
    public BiConsumer<BaseTask, SVGShapeView> viewHandler() {
        return new CompositeShapeViewHandler<BaseTask, SVGShapeView>()
                .register(newViewAttributesHandler())
                .register(new TaskViewHandler())::handle;
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

    @Override
    public FontHandler<BaseTask, SVGShapeView> newFontHandler() {
        return newFontHandlerBuilder()
                .margins(bean -> taskMarginSuppliers.getOrDefault(bean.getClass(), null))
                .build();
    }
}