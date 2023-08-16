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


package org.kie.workbench.common.stunner.core.diagram;

import org.kie.workbench.common.stunner.core.graph.Graph;

public abstract class AbstractDiagram<G extends Graph, S extends Metadata> implements Diagram<G, S> {

    private final String name;
    private final S metadata;
    private G graph;

    public AbstractDiagram(final String name,
                           final S metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public AbstractDiagram(final String name,
                           final G graph,
                           final S metadata) {
        this.name = name;
        this.metadata = metadata;
        this.graph = graph;
    }

    public void setGraph(G graph) {
        this.graph = graph;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public G getGraph() {
        return graph;
    }

    @Override
    public S getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Diagram)) {
            return false;
        }
        Diagram that = (Diagram) o;
        return name != null && name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return name == null ? 0 : ~~name.hashCode();
    }
}
