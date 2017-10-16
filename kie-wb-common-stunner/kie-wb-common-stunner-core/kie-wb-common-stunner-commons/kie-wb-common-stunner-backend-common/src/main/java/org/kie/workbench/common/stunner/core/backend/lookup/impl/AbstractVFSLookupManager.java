/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.backend.lookup.impl;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.LookupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

public abstract class AbstractVFSLookupManager<I, T, R extends LookupManager.LookupRequest> extends AbstractLookupManager<I, T, R> {

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractVFSLookupManager.class.getName());

    private final IOService ioService;

    public AbstractVFSLookupManager(IOService ioService) {
        this.ioService = ioService;
    }

    protected abstract boolean acceptsPath(final org.uberfire.backend.vfs.Path path);

    protected abstract I getItemByPath(final org.uberfire.backend.vfs.Path path);

    public List<I> getItemsByPath(final org.uberfire.java.nio.file.Path root) {
        try {
            final List<I> result = new LinkedList<I>();
            if (ioService.exists(root)) {
                walkFileTree(checkNotNull("root",
                                          root),
                             new SimpleFileVisitor<Path>() {
                                 @Override
                                 public FileVisitResult visitFile(final org.uberfire.java.nio.file.Path _file,
                                                                  final BasicFileAttributes attrs) throws IOException {
                                     checkNotNull("file",
                                                  _file);
                                     checkNotNull("attrs",
                                                  attrs);
                                     final org.uberfire.backend.vfs.Path file = org.uberfire.backend.server.util.Paths.convert(_file);
                                     if (acceptsPath(file)) {
                                         I item = null;
                                         try {
                                             // portable diagram representation.
                                             item = getItemByPath(file);
                                         } catch (final Exception e) {
                                             LOG.error("Cannot load diagram for path [" + file + "]",
                                                       e);
                                         }
                                         if (null != item) {
                                             result.add(item);
                                         }
                                     }
                                     return FileVisitResult.CONTINUE;
                                 }
                             });
            }
            return result;
        } catch (Exception e) {
            LOG.error("Error while loading from VFS the item with path [" + root + "].",
                      e);
        }
        return null;
    }

    protected IOService getIoService() {
        return ioService;
    }
}
