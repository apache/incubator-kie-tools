/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.backend.service.htmleditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.editor.commons.service.htmleditor.HtmlEditorService;

public class HtmlEditorServiceImpl implements HtmlEditorService {

    private VFSService vfsServices;

    private DeleteService deleteService;

    private RenameService renameService;

    private CopyService copyService;

    private SaveAndRenameServiceImpl<String, DefaultMetadata> saveAndRenameService;

    @Inject
    public HtmlEditorServiceImpl(final VFSService vfsServices,
                                 final DeleteService deleteService,
                                 final RenameService renameService,
                                 final CopyService copyService,
                                 final SaveAndRenameServiceImpl<String, DefaultMetadata> saveAndRenameService) {
        this.vfsServices = vfsServices;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.copyService = copyService;
        this.saveAndRenameService = saveAndRenameService;
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        deleteService.delete(path, comment);
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public Path save(final Path path,
                     final String content,
                     final DefaultMetadata _metadata,
                     final String _comment) {
        return vfsServices.write(path, content);
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final String comment) {
        return copyService.copy(path, newName, comment);
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final Path targetDirectory,
                     final String comment) {
        return copyService.copy(path, newName, targetDirectory, comment);
    }

    @Override
    public String load(final Path path) {
        return vfsServices.readAllString(path);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final DefaultMetadata metadata,
                              final String content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }
}
