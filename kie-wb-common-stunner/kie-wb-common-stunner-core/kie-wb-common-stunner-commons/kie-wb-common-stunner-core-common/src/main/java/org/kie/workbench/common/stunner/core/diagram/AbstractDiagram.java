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

package org.kie.workbench.common.stunner.core.diagram;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.workbench.common.stunner.core.graph.Graph;

public abstract class AbstractDiagram<G extends Graph, S extends Settings> implements Diagram<G, S> {

    private final String uuid;
    private final G graph;
    private final S settings;

    public AbstractDiagram( @MapsTo( "uuid" ) String uuid,
                            @MapsTo( "graph" ) G graph,
                            @MapsTo( "settings" ) S settings ) {
        this.uuid = uuid;
        this.graph = graph;
        this.settings = settings;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public G getGraph() {
        return graph;
    }

    @Override
    public S getSettings() {
        return settings;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Diagram ) ) {
            return false;
        }
        Diagram that = ( Diagram ) o;
        return uuid.equals( that.getUUID() );

    }

}
