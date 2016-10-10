/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.api;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractDiagramManager<D extends Diagram> implements DiagramManager<D> {

    protected final DiagramRegistry<D> registry;

    protected AbstractDiagramManager() {
        this.registry = null;
    }

    public AbstractDiagramManager( final DiagramRegistry<D> registry ) {
        this.registry = registry;
    }

    @Override
    public void update( final D diagram ) {
        registry.update( diagram );
    }

    @Override
    public void register( final D item ) {
        registry.register( item );
    }

    @Override
    public boolean contains( final D item ) {
        return registry.contains( item );
    }

    @Override
    public boolean remove( final D item ) {
        return registry.remove( item );
    }

    @Override
    public D getDiagramByUUID( final String uuid ) {
        return registry.getDiagramByUUID( uuid );
    }

    @Override
    public Collection<D> getItems() {
        return Collections.unmodifiableCollection( registry.getItems() );
    }

    @Override
    public void clear() {
        registry.clear();
    }
}
