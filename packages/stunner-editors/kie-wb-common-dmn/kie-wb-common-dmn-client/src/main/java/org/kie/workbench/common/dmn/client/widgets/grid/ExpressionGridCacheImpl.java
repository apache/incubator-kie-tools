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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@Dependent
public class ExpressionGridCacheImpl extends AbstractCanvasControl<AbstractCanvas> implements ExpressionGridCache {

    private Map<String, Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cache = new HashMap<>();

    public ExpressionGridCacheImpl() {
        //Errai seems to need a zero parameter constructor to be explicitly declared
    }

    @Override
    protected void doInit() {
        cache = new HashMap<>();
    }

    @Override
    protected void doDestroy() {
        cache.clear();
    }

    @Override
    public Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> getExpressionGrid(final String nodeUUID) {
        return cache.getOrDefault(nodeUUID, Optional.empty());
    }

    @Override
    public void putExpressionGrid(final String nodeUUID,
                                  final Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>> gridWidget) {
        if (gridWidget.isPresent()) {
            if (gridWidget.get().isCacheable()) {
                cache.put(nodeUUID, gridWidget);
            }
        }
    }

    @Override
    public void removeExpressionGrid(final String nodeUUID) {
        cache.remove(nodeUUID);
    }

    //Package-protected for Unit Tests
    Map<String, Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> getContent() {
        return cache;
    }
}
