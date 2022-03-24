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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Collection;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.graph.Element;

public abstract class AbstractActionsToolboxFactory
        implements ActionsToolboxFactory {

    protected abstract ActionsToolboxView<?> newViewInstance();

    @Override
    public Optional<Toolbox<?>> build(final AbstractCanvasHandler canvasHandler,
                                      final Element element) {
        final Collection<ToolboxAction<AbstractCanvasHandler>> actions = getActions(canvasHandler,
                                                                                    element);
        if (!actions.isEmpty()) {
            final ActionsToolbox<?> toolbox =
                    new ActionsToolbox<>(() -> canvasHandler,
                                         element,
                                         newViewInstance());
            actions.forEach(toolbox::add);
            return Optional.of(toolbox.init());
        }
        return Optional.empty();
    }
}
