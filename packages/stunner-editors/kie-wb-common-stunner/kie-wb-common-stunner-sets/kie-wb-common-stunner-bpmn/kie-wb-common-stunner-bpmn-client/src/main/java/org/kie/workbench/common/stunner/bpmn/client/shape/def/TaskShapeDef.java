/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNGlyphFactory;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.TaskViewHandler;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
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
            Stream.of(new AbstractMap.SimpleEntry<>(NoneTask.class, BPMNGlyphFactory.TASK),
                      new AbstractMap.SimpleEntry<>(GenericServiceTask.class, BPMNGlyphFactory.TASK_GENERIC_SERVICE),
                      new AbstractMap.SimpleEntry<>(UserTask.class, BPMNGlyphFactory.TASK_USER),
                      new AbstractMap.SimpleEntry<>(ScriptTask.class, BPMNGlyphFactory.TASK_SCRIPT),
                      new AbstractMap.SimpleEntry<>(BusinessRuleTask.class, BPMNGlyphFactory.TASK_BUSINESS_RULE))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static final Map<Enum, Double> DEFAULT_TASK_MARGINS_WITH_ICON =
            Stream.of(new AbstractMap.SimpleEntry<>(HorizontalAlignment.LEFT, ICON_WIDTH))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static final Map<Class<? extends BaseTask>, Map<Enum, Double>> TASK_MARGIN_SUPPLIERS = buildTaskMarginSuppliers();

    private static Map<Class<? extends BaseTask>, Map<Enum, Double>> buildTaskMarginSuppliers() {
        final Map<Class<? extends BaseTask>, Map<Enum, Double>> taskMarginSuppliers = new HashMap<>();
        taskMarginSuppliers.put(NoneTask.class, null);
        taskMarginSuppliers.put(UserTask.class, DEFAULT_TASK_MARGINS_WITH_ICON);
        taskMarginSuppliers.put(ScriptTask.class, DEFAULT_TASK_MARGINS_WITH_ICON);
        taskMarginSuppliers.put(BusinessRuleTask.class, DEFAULT_TASK_MARGINS_WITH_ICON);
        taskMarginSuppliers.put(CustomTask.class, DEFAULT_TASK_MARGINS_WITH_ICON);
        taskMarginSuppliers.put(GenericServiceTask.class, DEFAULT_TASK_MARGINS_WITH_ICON);

        return taskMarginSuppliers;
    }

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
                .margins(bean -> TASK_MARGIN_SUPPLIERS.getOrDefault(bean.getClass(), null))
                .build();
    }
}