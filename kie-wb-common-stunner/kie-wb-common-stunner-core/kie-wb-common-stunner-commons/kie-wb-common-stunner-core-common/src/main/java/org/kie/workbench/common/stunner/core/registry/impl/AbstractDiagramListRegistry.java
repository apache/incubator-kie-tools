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

package org.kie.workbench.common.stunner.core.registry.impl;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;

import java.util.Collection;
import java.util.List;

public abstract class AbstractDiagramListRegistry<D extends Diagram>
        extends AbstractDynamicRegistryWrapper<D, ListRegistry<D>>
        implements DiagramRegistry<D> {

    public AbstractDiagramListRegistry( final List<D> items ) {
        super( new ListRegistry<>( Diagram::getName, items ) );
    }

    @Override
    public D getDiagramByUUID( final String uuid ) {
        return getWrapped().getItemByKey( uuid );
    }

    @Override
    public void update( final D diagram ) {
        final int index = getWrapped().indexOf( diagram );
        if ( index != -1 ) {
            final boolean isRemoved = remove( diagram );
            if ( isRemoved ) {
                getWrapped().add( index, diagram );
            }

        } else {
            throw new RuntimeException( "Diagram with uuid [" + diagram.getName() + "] cannot be updated as it does not exist." );

        }

    }

    public Collection<D> getItems() {
        return getWrapped().getItems();
    }

    public void clear() {
        getWrapped().clear();
    }

}
