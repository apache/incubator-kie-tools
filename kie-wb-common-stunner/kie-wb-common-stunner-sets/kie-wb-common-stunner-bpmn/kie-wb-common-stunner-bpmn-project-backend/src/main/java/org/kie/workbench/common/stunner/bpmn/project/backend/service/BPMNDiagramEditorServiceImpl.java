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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.project.service.BPMNDiagramEditorService;
import org.kie.workbench.common.stunner.bpmn.project.service.MigrationResult;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
@Service
public class BPMNDiagramEditorServiceImpl implements BPMNDiagramEditorService {

    private IOService ioService;

    private CommentedOptionFactory optionFactory;

    public BPMNDiagramEditorServiceImpl() {
        //Empty constructor for Weld proxying
    }

    @Inject
    public BPMNDiagramEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                        final CommentedOptionFactory optionFactory) {
        this.ioService = ioService;
        this.optionFactory = optionFactory;
    }

    @Override
    public MigrationResult migrateDiagram(final Path path,
                                          final String newName,
                                          final String newExtension,
                                          final String commitMessage) {
        checkNotNull("path",
                     path);
        checkNotNull("newName",
                     newName);
        checkNotNull("newExtension",
                     newExtension);

        final org.uberfire.java.nio.file.Path _path = Paths.convert(path);
        final org.uberfire.java.nio.file.Path _target = _path.resolveSibling(newName + newExtension);

        if (ioService.exists(_target)) {
            return new MigrationResult(Paths.convert(_target),
                                       ServiceError.MIGRATION_ERROR_PROCESS_ALREADY_EXIST);
        }
        try {
            ioService.startBatch(_target.getFileSystem());
            ioService.move(_path,
                           _target,
                           optionFactory.makeCommentedOption(commitMessage));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ioService.endBatch();
        }
        return new MigrationResult(Paths.convert(_target));
    }
}
