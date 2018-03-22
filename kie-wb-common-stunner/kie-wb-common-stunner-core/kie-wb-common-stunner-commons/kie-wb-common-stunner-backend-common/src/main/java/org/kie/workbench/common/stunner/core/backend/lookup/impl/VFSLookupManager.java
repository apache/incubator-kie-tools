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

package org.kie.workbench.common.stunner.core.backend.lookup.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.VFSLookupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.java.nio.file.Files.walkFileTree;

public class VFSLookupManager<I>
        extends AbstractLookupManager<I, I, VFSLookupRequest> {

    private static final Logger LOG =
            LoggerFactory.getLogger(VFSLookupManager.class.getName());

    private final IOService ioService;
    private Predicate<org.uberfire.backend.vfs.Path> pathAcceptor;
    private Function<org.uberfire.backend.vfs.Path, I> itemSupplier;

    public VFSLookupManager(final IOService ioService) {
        this.ioService = ioService;
        this.pathAcceptor = null;
        this.itemSupplier = null;
    }

    public VFSLookupManager(final IOService ioService,
                            final Predicate<org.uberfire.backend.vfs.Path> pathAcceptor,
                            final Function<org.uberfire.backend.vfs.Path, I> itemSupplier) {
        this.ioService = ioService;
        this.pathAcceptor = pathAcceptor;
        this.itemSupplier = itemSupplier;
    }

    public VFSLookupManager<I> setPathAcceptor(final Predicate<org.uberfire.backend.vfs.Path> pathAcceptor) {
        this.pathAcceptor = pathAcceptor;
        return this;
    }

    public VFSLookupManager<I> setItemSupplier(final Function<org.uberfire.backend.vfs.Path, I> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    protected List<I> getItems(final VFSLookupRequest request) {
        Path root = parseCriteriaPath(request);
        return getItemsByPath(root);
    }

    public List<I> getItemsByPath(final Path root) {
        final List<I> result = new LinkedList<I>();
        try {
            if (ioService.exists(root)) {
                walkFileTree(checkNotNull("root",
                                          root),
                             new SimpleFileVisitor<Path>() {
                                 @Override
                                 public FileVisitResult visitFile(final Path _file,
                                                                  final BasicFileAttributes attrs) throws IOException {
                                     checkNotNull("file",
                                                  _file);
                                     checkNotNull("attrs",
                                                  attrs);
                                     final org.uberfire.backend.vfs.Path file = Paths.convert(_file);
                                     if (pathAcceptor.test(file)) {
                                         I item = null;
                                         try {
                                             // portable diagram representation.
                                             item = itemSupplier.apply(file);
                                         } catch (final Exception e) {
                                             LOG.error("Cannot load item from path [" + file + "]",
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
        } catch (Exception e) {
            LOG.error("Error while loading from VFS the item with path [" + root + "].",
                      e);
        }
        return result;
    }

    @Override
    protected boolean matches(final String criteria,
                              final I item) {
        return true;
    }

    @Override
    protected I buildResult(final I item) {
        return item;
    }

    private Path parseCriteriaPath(final VFSLookupRequest request) {
        org.uberfire.backend.vfs.Path path = request.getPath();
        return Paths.convert(path);
    }
}
