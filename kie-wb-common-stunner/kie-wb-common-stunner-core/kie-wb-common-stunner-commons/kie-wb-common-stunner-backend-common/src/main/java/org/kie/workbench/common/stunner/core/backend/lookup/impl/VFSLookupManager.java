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

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.lookup.AbstractLookupManager;
import org.kie.workbench.common.stunner.core.lookup.VFSLookupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.VFSService;

@Dependent
@Typed(VFSLookupManager.class)
public class VFSLookupManager<T>
        extends AbstractLookupManager<T, T, VFSLookupRequest> {

    private static final Logger LOG =
            LoggerFactory.getLogger(VFSLookupManager.class.getName());

    private final VFSService vfsService;
    private Predicate<org.uberfire.backend.vfs.Path> pathAcceptor;
    private Function<org.uberfire.backend.vfs.Path, T> itemSupplier;

    // CDI proxy.
    protected VFSLookupManager() {
        this.vfsService = null;
        this.pathAcceptor = null;
        this.itemSupplier = null;
    }

    @Inject
    public VFSLookupManager(final VFSService vfsService) {
        this.vfsService = vfsService;
        this.pathAcceptor = null;
        this.itemSupplier = null;
    }

    public VFSLookupManager(final VFSService vfsService,
                            final Predicate<org.uberfire.backend.vfs.Path> pathAcceptor,
                            final Function<org.uberfire.backend.vfs.Path, T> itemSupplier) {
        this.vfsService = vfsService;
        this.pathAcceptor = pathAcceptor;
        this.itemSupplier = itemSupplier;
    }

    public VFSLookupManager<T> setPathAcceptor(final Predicate<org.uberfire.backend.vfs.Path> pathAcceptor) {
        this.pathAcceptor = pathAcceptor;
        return this;
    }

    public VFSLookupManager<T> setItemSupplier(final Function<org.uberfire.backend.vfs.Path, T> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    protected List<T> getItems(final VFSLookupRequest request) {
        return getItemsByPath(request.getPath());
    }

    public List<T> getItemsByPath(final org.uberfire.backend.vfs.Path root) {
        final DirectoryStream<org.uberfire.backend.vfs.Path> files =
                vfsService.newDirectoryStream(root,
                                              path -> pathAcceptor.test(path));
        return StreamSupport.stream(files.spliterator(),
                                    false)
                .map(itemSupplier)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean matches(final String criteria,
                              final T item) {
        return true;
    }

    @Override
    protected T buildResult(final T item) {
        return item;
    }
}
