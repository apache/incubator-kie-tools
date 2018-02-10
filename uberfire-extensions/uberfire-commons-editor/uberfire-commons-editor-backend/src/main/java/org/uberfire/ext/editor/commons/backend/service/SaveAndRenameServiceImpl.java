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

package org.uberfire.ext.editor.commons.backend.service;

import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

public class SaveAndRenameServiceImpl<T, M> implements SupportsSaveAndRename<T, M> {

    private SupportsSaveAndRename<T, M> updateService;

    public void init(final SupportsSaveAndRename<T, M> updateService) {
        this.updateService = updateService;
    }

    @Override
    public Path rename(final Path path,
                       final String newFileName,
                       final String comment) {
        return updateService.rename(path, newFileName, comment);
    }

    @Override
    public Path save(final Path path,
                     final T content,
                     final M metadata,
                     final String comment) {
        return updateService.save(path, content, metadata, comment);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final M metadata,
                              final T content,
                              final String comment) {

        final Path savedPath = save(path, content, metadata, comment);
        final Path renamedPath = rename(savedPath, newFileName, comment);

        return renamedPath;
    }
}
