/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service;

import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.service.BPMNDiagramService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;

@Service
public class BPMNDiagramServiceImpl implements BPMNDiagramService {

    private IOService ioService;

    @Inject
    public BPMNDiagramServiceImpl(final @Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public BPMNDiagramServiceImpl() {
    }

    @Override
    public ProjectType getProjectType(Path projectRootPath) {
        try (DirectoryStream<org.uberfire.java.nio.file.Path> paths =
                     ioService.newDirectoryStream(Paths.convert(projectRootPath), f -> f.getFileName().toString().startsWith("."))) {
            return ProjectType.fromFileName(StreamSupport.stream(paths.spliterator(), false)
                                                    .map(Paths::convert)
                                                    .map(Path::getFileName)
                                                    .findFirst()
            ).orElse(null);
        }
    }
}