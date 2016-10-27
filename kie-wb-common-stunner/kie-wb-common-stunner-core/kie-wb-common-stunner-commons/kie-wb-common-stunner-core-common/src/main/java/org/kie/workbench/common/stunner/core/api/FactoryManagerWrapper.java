/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.api;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;

public abstract class FactoryManagerWrapper implements FactoryManager {

    protected abstract FactoryManager getWrapped();

    @Override
    public <T> T newDefinition( final String id ) {
        return getWrapped().newDefinition( id );
    }

    @Override
    public <T> T newDefinition( final Class<T> type ) {
        return getWrapped().newDefinition( type );
    }

    @Override
    public Element newElement( final String uuid,
                               final String id ) {
        return getWrapped().newElement( uuid, id );
    }

    @Override
    public Element newElement( final String uuid,
                               final Class<?> type ) {
        return getWrapped().newElement( uuid, type );
    }

    @Override
    public <D extends Diagram> D newDiagram( final String name,
                                             final String id ) {
        return getWrapped().newDiagram( name, id );
    }

    @Override
    public <D extends Diagram> D newDiagram( final String name,
                                             final Class<?> type ) {
        return getWrapped().newDiagram( name, type );
    }

    @Override
    public FactoryRegistry registry() {
        return getWrapped().registry();
    }
}
