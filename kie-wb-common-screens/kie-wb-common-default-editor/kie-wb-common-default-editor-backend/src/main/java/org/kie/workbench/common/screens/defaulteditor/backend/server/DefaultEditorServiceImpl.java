/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.defaulteditor.backend.server;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.services.backend.service.KieService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;

@Service
@ApplicationScoped
public class DefaultEditorServiceImpl
        extends KieService<DefaultEditorContent>
        implements DefaultEditorService {

    @Inject
    CommentedOptionFactory commentedOptionFactory;

    @Inject
    SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @Inject
    RenameService renameService;

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public DefaultEditorContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    public Path save(final Path resource,
                     final String content,
                     final Metadata metadata,
                     final String comment) {
        try {
            ioService.write(Paths.convert(resource),
                            content,
                            metadataService.setUpAttributes(resource,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    protected DefaultEditorContent constructContent(Path path, Overview overview) {
        return new DefaultEditorContent(overview);
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final String content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }
}
