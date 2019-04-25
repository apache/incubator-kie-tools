/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueFileExtensionIndexTerm;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;

import static java.lang.Boolean.TRUE;
import static org.kie.workbench.common.dmn.backend.editors.types.query.FindAllDmnAssetsQuery.NAME;

@ApplicationScoped
public class DMNPathsHelperImpl implements DMNPathsHelper {

    static final String STANDALONE_URI = "default://master@system/stunner/diagrams";

    static final String STANDALONE_FILE_NAME = "diagrams";

    private final RefactoringQueryServiceImpl refactoringQueryService;

    private final DMNDefinitionSetResourceType resourceType;

    private final IOService ioService;

    public DMNPathsHelperImpl() {
        this(null, null, null);
    }

    @Inject
    public DMNPathsHelperImpl(final RefactoringQueryServiceImpl refactoringQueryService,
                              final DMNDefinitionSetResourceType resourceType,
                              final @Named("ioStrategy") IOService ioService) {
        this.refactoringQueryService = refactoringQueryService;
        this.resourceType = resourceType;
        this.ioService = ioService;
    }

    @Override
    public List<Path> getDiagramsPaths(final WorkspaceProject workspaceProject) {
        if (workspaceProject != null) {
            return getPathsByWorkspaceProject(workspaceProject);
        } else {
            return getStandalonePaths();
        }
    }

    private List<Path> getStandalonePaths() {
        return StreamSupport
                .stream(getDMNPaths().spliterator(), false)
                .map(this::convertPath)
                .collect(Collectors.toList());
    }

    private List<Path> getPathsByWorkspaceProject(final WorkspaceProject workspaceProject) {
        final RefactoringPageRequest request = buildRequest(workspaceProject.getRootPath().toURI());
        return refactoringQueryService
                .query(request)
                .getPageRowList()
                .stream()
                .map(row -> (Path) row.getValue())
                .collect(Collectors.toList());
    }

    private RefactoringPageRequest buildRequest(final String rootPath) {
        return new RefactoringPageRequest(NAME, queryTerms(rootPath), 0, 1000, TRUE);
    }

    private Set<ValueIndexTerm> queryTerms(final String rootPath) {

        final Set<ValueIndexTerm> queryTerms = new HashSet<>();

        queryTerms.add(new DMNValueRepositoryRootIndexTerm(rootPath));
        queryTerms.add(new DMNValueFileExtensionIndexTerm());

        return queryTerms;
    }

    DirectoryStream<org.uberfire.java.nio.file.Path> getDMNPaths() {
        final org.uberfire.java.nio.file.Path root = getStandaloneRootPath();
        return ioService.newDirectoryStream(root, dmnAssetsFilter());
    }

    DirectoryStream.Filter<org.uberfire.java.nio.file.Path> dmnAssetsFilter() {
        return path -> resourceType.accept(convertPath(path));
    }

    org.uberfire.java.nio.file.Path getStandaloneRootPath() {
        return convertPath(newPath(STANDALONE_FILE_NAME, STANDALONE_URI));
    }

    Path newPath(final String fileName,
                 final String uri) {
        return PathFactory.newPath(fileName, uri);
    }

    org.uberfire.java.nio.file.Path convertPath(final Path path) {
        return Paths.convert(path);
    }

    Path convertPath(final org.uberfire.java.nio.file.Path path) {
        return Paths.convert(path);
    }
}
