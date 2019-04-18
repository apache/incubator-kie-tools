/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.screens.library.api.index.Constants;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.KPropertyImpl;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.IndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.annotations.VisibleAsset;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class LibraryIndexer extends AbstractFileIndexer {

    private static final Logger logger = LoggerFactory.getLogger(LibraryIndexer.class);

    private static final String LIBRARY_CLASSIFIER = "library";

    private Set<ResourceTypeDefinition> visibleResourceTypes;

    // For proxying
    public LibraryIndexer() {
        visibleResourceTypes = new HashSet<>();
    }

    @Inject
    public LibraryIndexer(@VisibleAsset final Instance<ResourceTypeDefinition> visibleResourceTypeDefinitions) {
        this.visibleResourceTypes = StreamSupport
                .stream(visibleResourceTypeDefinitions.spliterator(),
                        false)
                .collect(Collectors.toSet());
    }

    void setIOService(final IOService ioService) {
        this.ioService = ioService;
    }

    void setModuleService(final KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public boolean supportsPath(final Path path) {
        return !(this.isFolder(path) || this.isHidden(path)) && this.isSupportedByAnyResourceType(path);
    }

    private boolean isSupportedByAnyResourceType(Path path) {
        org.uberfire.backend.vfs.Path convertedPath = convertPath(path);
        return this.getVisibleResourceTypes()
                .stream()
                .anyMatch(resourceTypeDefinition ->
                                  resourceTypeDefinition.accept(convertedPath));
    }

    @Override
    public String getIndexerId() {
        return Constants.INDEXER_ID;
    }

    @Override
    protected IndexBuilder fillIndexBuilder(final Path path) throws Exception {
        // responsible for basic index info: file name, project root path
        final String fileName = Paths.convert(path).getFileName();
        final String repoRoot = getRootBackendPath(path);

        final IndexBuilder builder = () -> {
            final Set<KProperty<?>> indexElements = new HashSet<>();

            indexElements.add(new KPropertyImpl<>(LibraryFileNameIndexTerm.TERM,
                                                  fileName));
            indexElements.add(new KPropertyImpl<>(FieldFactory.FILE_NAME_FIELD_SORTED,
                                                  fileName.toLowerCase(),
                                                  false,
                                                  true));
            indexElements.add(new KPropertyImpl<>(LibraryRepositoryRootIndexTerm.TERM,
                                                  repoRoot));

            return indexElements;
        };
        return builder;
    }

    private String getRootBackendPath(final Path path) {
        Path root = path;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        return Paths.convert(root).toURI();
    }

    @Override
    public KObject toKObject(final Path path) {
        KObject index = null;

        try {
            // create a builder with the default information
            IndexBuilder builder = fillIndexBuilder(path);

            Set<KProperty<?>> indexElements = null;
            if (builder != null) {
                // build index document
                indexElements = builder.build();
            } else {
                indexElements = Collections.emptySet();
            }

            index = KObjectUtil.toKObject(path,
                                          LIBRARY_CLASSIFIER,
                                          indexElements);
        } catch (Exception e) {
            // Unexpected parsing or processing error
            logger.error("Unable to index '" + path.toUri().toString() + "'.",
                         e.getMessage(),
                         e);
        }

        return index;
    }

    @Override
    public KObjectKey toKObjectKey(final Path path) {
        return KObjectUtil.toKObjectKey(path,
                                        LIBRARY_CLASSIFIER);
    }

    protected org.uberfire.backend.vfs.Path convertPath(Path path) {
        return Paths.convert(path);
    }

    protected KieModule getModule(final Path path) {
        return moduleService.resolveModule(Paths.convert(path),
                                           false);
    }

    protected Package getPackage(final Path path) {
        return moduleService.resolvePackage(Paths.convert(path));
    }

    protected boolean isFolder(final Path path) {
        return Files.isDirectory(path);
    }

    protected boolean isHidden(Path path) {
        return path.getFileName().startsWith(".");
    }

    protected Set<ResourceTypeDefinition> getVisibleResourceTypes() {
        return this.visibleResourceTypes;
    }
}

