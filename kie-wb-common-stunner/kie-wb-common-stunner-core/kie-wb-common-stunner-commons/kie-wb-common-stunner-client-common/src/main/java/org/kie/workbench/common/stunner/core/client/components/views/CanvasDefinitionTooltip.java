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

package org.kie.workbench.common.stunner.core.client.components.views;

import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Transform;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

/**
 * A tooltip that shows the specified Definition title in the canvas.
 */
@Dependent
public class CanvasDefinitionTooltip implements CanvasTooltip<CanvasDefinitionTooltip.DefinitionIdContent> {

    public static class DefinitionIdContent {

        private final String id;

        public DefinitionIdContent(final String id) {
            this.id = id;
        }
    }

    private final CanvasTooltip<String> textTooltip;
    private final Function<String, String> titleProvider;
    private String prefix;

    @Inject
    public CanvasDefinitionTooltip(final DefinitionManager definitionManager,
                                   final DefinitionsCacheRegistry registry,
                                   final CanvasTooltip<String> textTooltip) {
        this(textTooltip,
             defId -> getDefinitionTitle(definitionManager,
                                         registry,
                                         defId));
    }

    CanvasDefinitionTooltip(final CanvasTooltip<String> textTooltip,
                            final Function<String, String> titleProvider) {
        this.textTooltip = textTooltip;
        this.titleProvider = titleProvider;
        this.prefix = "";
    }

    public CanvasDefinitionTooltip setPrefix(final String value) {
        this.prefix = value;
        return this;
    }

    public CanvasDefinitionTooltip configure(final AbstractCanvasHandler canvasHandler) {
        setTransform(canvasHandler.getCanvas().getLayer().getTransform());
        setCanvasLocation(new Point2D(canvasHandler.getAbstractCanvas().getView().getAbsoluteX(),
                                      canvasHandler.getAbstractCanvas().getView().getAbsoluteY()));
        return this;
    }

    @Override
    public void setCanvasLocation(final Point2D location) {
        this.textTooltip.setCanvasLocation(location);
    }

    @Override
    public void setTransform(Transform transform) {
        this.textTooltip.setTransform(transform);
    }

    @Override
    public void show(final DefinitionIdContent content,
                     final Point2D location) {
        this.show(content.id,
                  location);
    }

    public void show(final String definitionId,
                     final Point2D location) {
        final String title = titleProvider.apply(definitionId);
        textTooltip.show(prefix + title,
                         location);
    }

    @Override
    public void hide() {
        textTooltip.hide();
    }

    public void destroy() {
        textTooltip.destroy();
    }

    private static String getDefinitionTitle(final DefinitionManager definitionManager,
                                             final DefinitionsCacheRegistry registry,
                                             final String id) {
        return definitionManager
                .adapters()
                .forDefinition()
                .getTitle(registry.getDefinitionById(id));
    }
}
