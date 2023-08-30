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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public final class DiagramImpl extends AbstractDiagram<Graph, Metadata> {

    public DiagramImpl(final @MapsTo("name") String name,
                       final @MapsTo("metadata") Metadata metadata) {
        super(name,
              metadata);
    }

    public DiagramImpl(final String name,
                       final Graph graph,
                       final Metadata metadata) {
        super(name,
              graph,
              metadata);
    }

    @Override
    public int hashCode() {
        int graphHash = (null != getGraph()) ? getGraph().hashCode() : 0;
        int metadataHash = (null != getMetadata()) ? getMetadata().hashCode() : 0;
        int nameHash = (null != getName()) ? getName().hashCode() : 0;
        return HashUtil.combineHashCodes(graphHash,
                                         metadataHash,
                                         nameHash);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DiagramImpl) {
            DiagramImpl other = (DiagramImpl) o;
            return ((null != getGraph()) ? getGraph().equals(other.getGraph()) : null == other.getGraph()) &&
                    ((null != getMetadata()) ? getMetadata().equals(other.getMetadata()) : null == other.getMetadata()) &&
                    ((null != getName()) ? getName().equals(other.getName()) : null == other.getName());
        } else {
            return false;
        }
    }
}
