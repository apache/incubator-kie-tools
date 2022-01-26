/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.service;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 8/27/21
 */
@Dependent
public class DiagramServiceImpl implements DiagramService {

    @Override
    public Diagram<Graph, Metadata> getDiagramByPath(Path path) {
        throw new Error(getClass().getCanonicalName()+".getDiagramByPath");
    }

    @Override
    public boolean accepts(Path path) {
        throw new Error(getClass().getCanonicalName()+".accepts");
    }

    @Override
    public Path create(Path path, String name, String defSetId) {
        throw new Error(getClass().getCanonicalName()+".create");
    }

    @Override
    public Metadata saveOrUpdate(Diagram<Graph, Metadata> diagram) {
        throw new Error(getClass().getCanonicalName()+".saveOrUpdate");
    }

    @Override
    public Path saveOrUpdateSvg(Path diagramPath, String rawDiagramSvg) {
        throw new Error(getClass().getCanonicalName()+".saveOrUpdateSvg");
    }

    @Override
    public boolean delete(Diagram<Graph, Metadata> diagram) {
        throw new Error(getClass().getCanonicalName()+".delete");
    }

    @Override
    public String getRawContent(Diagram<Graph, Metadata> diagram) {
        throw new Error(getClass().getCanonicalName()+".getRawContent");
    }
}
