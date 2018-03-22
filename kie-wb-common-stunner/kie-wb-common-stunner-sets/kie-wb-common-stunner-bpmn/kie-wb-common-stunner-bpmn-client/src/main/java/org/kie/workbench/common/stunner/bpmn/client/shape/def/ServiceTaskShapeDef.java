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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNSVGViewFactory;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.ServiceTaskShapeViewHandler;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.CompositeShapeViewHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.SizeHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class ServiceTaskShapeDef extends BaseDimensionedShapeDef
        implements BPMNSvgShapeDef<ServiceTask> {

    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;
    private final Function<String, Glyph> iconDataGlyphGenerator;

    public ServiceTaskShapeDef(final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        this(workItemDefinitionRegistry,
             data -> ImageDataUriGlyph.create(() -> data));
    }

    ServiceTaskShapeDef(final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                        final Function<String, Glyph> iconDataGlyphGenerator) {
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.iconDataGlyphGenerator = iconDataGlyphGenerator;
    }

    @Override
    public SizeHandler<ServiceTask, SVGShapeView> newSizeHandler() {
        return newSizeHandlerBuilder()
                .width(task -> task.getDimensionsSet().getWidth().getValue())
                .height(task -> task.getDimensionsSet().getHeight().getValue())
                .minWidth(task -> 25d)
                .maxWidth(task -> 400d)
                .minHeight(task -> 25d)
                .maxHeight(task -> 400d)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiConsumer<ServiceTask, SVGShapeView> viewHandler() {
        return new CompositeShapeViewHandler<ServiceTask, SVGShapeView>()
                .register(newViewAttributesHandler())
                .register(new ServiceTaskShapeViewHandler(workItemDefinitionRegistry))::handle;
    }

    @Override
    public SVGShapeView<?> newViewInstance(final BPMNSVGViewFactory factory,
                                           final ServiceTask workItem) {

        return newViewInstance(Optional.ofNullable(workItem.getDimensionsSet().getWidth()),
                               Optional.ofNullable(workItem.getDimensionsSet().getHeight()),
                               factory.serviceTask());
    }

    public Glyph getGlyph(final Class<? extends ServiceTask> type,
                          final String defId) {
        final String name = defId.substring(defId.lastIndexOf(".") + 1, defId.length());
        final String itemIconData = ServiceTask.class.getSimpleName().equals(name) ? null :
                workItemDefinitionRegistry
                        .get()
                        .get(name)
                        .getIconData();
        final String iconData = null != itemIconData ? itemIconData : WorkItemDefinitionClientRegistry.DEFAULT_ICON_DATA;
        return iconDataGlyphGenerator.apply(iconData);
    }
}