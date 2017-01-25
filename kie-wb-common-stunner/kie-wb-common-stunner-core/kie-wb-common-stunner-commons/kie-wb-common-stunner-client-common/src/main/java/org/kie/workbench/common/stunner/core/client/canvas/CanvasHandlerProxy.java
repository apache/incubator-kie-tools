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

package org.kie.workbench.common.stunner.core.client.canvas;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.graph.GraphRulesManager;
import org.kie.workbench.common.stunner.core.rule.model.ModelRulesManager;
import org.uberfire.mvp.Command;

/**
 * It wraps a given <code>AbstractCanvasHandler</code> instance and replicates its state into this instance' canvas.
 * By default it provides a <code>null</code> instance for the graph execution context and does not load rules,
 * so no runtime evaluations neither model updates are applied by this handler.
 * Eg: It's useful for preview goals - it can proxy a given handler and replicate its state on a differnt canvas instance.
 * @param <D> The diagram type.
 * @param <C> The handled canvas type.
 */
public abstract class CanvasHandlerProxy<D extends Diagram, C extends AbstractCanvas> extends BaseCanvasHandler<D, C> {

    public CanvasHandlerProxy(final DefinitionManager definitionManager,
                              final GraphUtils graphUtils,
                              final ShapeManager shapeManager) {
        super(definitionManager,
              graphUtils,
              shapeManager);
    }

    public abstract AbstractCanvasHandler getWrapped();

    @Override
    public GraphRulesManager getGraphRulesManager() {
        return getWrapped().getGraphRulesManager();
    }

    @Override
    public ModelRulesManager getModelRulesManager() {
        return getWrapped().getModelRulesManager();
    }

    @Override
    public Index<?, ?> getGraphIndex() {
        return getWrapped().getGraphIndex();
    }

    @Override
    public GraphCommandExecutionContext getGraphExecutionContext() {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void buildGraphIndex(final Command loadCallback) {
        // No index to build as it's using the index from the wrapped instance..
        loadCallback.execute();
    }

    protected void loadRules(final Command loadCallback) {
        // No rules for viewer.
        loadCallback.execute();
    }

    @Override
    protected void destroyGraphIndex(final Command callback) {
        // No index to destroy as it's using the index from the wrapped instance.
        callback.execute();
    }
}
