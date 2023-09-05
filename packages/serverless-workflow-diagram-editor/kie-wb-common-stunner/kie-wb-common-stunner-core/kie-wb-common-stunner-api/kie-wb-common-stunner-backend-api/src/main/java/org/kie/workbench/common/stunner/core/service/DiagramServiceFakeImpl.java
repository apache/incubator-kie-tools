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

package org.kie.workbench.common.stunner.core.service;

import jakarta.enterprise.context.Dependent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

@Dependent
//TODO do wee need to implement this?
public class DiagramServiceFakeImpl implements DiagramService {
    @Override
    public Diagram<Graph, Metadata> getDiagramByPath(Path path) {
        throw new UnsupportedOperationException("getDiagramByPath");
    }

    @Override
    public boolean accepts(Path path) {
        throw new UnsupportedOperationException("accepts");
    }

    @Override
    public Path create(Path path, String name, String defSetId) {
        throw new UnsupportedOperationException("create");
    }

    @Override
    public Metadata saveOrUpdate(Diagram<Graph, Metadata> diagram) {
        throw new UnsupportedOperationException("saveOrUpdate");
    }

    @Override
    public Path saveOrUpdateSvg(Path diagramPath, String rawDiagramSvg) {
        throw new UnsupportedOperationException("saveOrUpdateSvg");
    }

    @Override
    public boolean delete(Diagram<Graph, Metadata> diagram) {
        throw new UnsupportedOperationException("delete");
    }

    @Override
    public String getRawContent(Diagram<Graph, Metadata> diagram) {
        throw new UnsupportedOperationException("getRawContent");
    }
}
