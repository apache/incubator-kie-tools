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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.kie.workbench.common.dmn.api.marshalling.DMNImportTypesHelper;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.kie.workbench.common.dmn.backend.editors.included.query.AllModelsValueFileExtensionIndexTerm;
import org.kie.workbench.common.dmn.backend.editors.included.query.PMMLValueFileExtensionIndexTerm;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueFileExtensionIndexTerm;
import org.kie.workbench.common.dmn.backend.editors.types.query.DMNValueRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.uberfire.apache.commons.io.FilenameUtils;
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

    private final DMNImportTypesHelper importTypesHelper;

    private final IOService ioService;

    public DMNPathsHelperImpl() {
        this(null, null, null);
    }

    @Inject
    public DMNPathsHelperImpl(final RefactoringQueryServiceImpl refactoringQueryService,
                              final DMNImportTypesHelper importTypesHelper,
                              final @Named("ioStrategy") IOService ioService) {
        this.refactoringQueryService = refactoringQueryService;
        this.importTypesHelper = importTypesHelper;
        this.ioService = ioService;
    }

    @Override
    public List<Path> getModelsPaths(final WorkspaceProject workspaceProject) {
        if (workspaceProject != null) {
            return getPathsByWorkspaceProject(modelsQueryTerms(workspaceProject.getRootPath().toURI()));
        } else {
            return getStandalonePaths(allModelsFilter());
        }
    }

    @Override
    public List<Path> getDMNModelsPaths(final WorkspaceProject workspaceProject) {
        if (workspaceProject != null) {
            return getPathsByWorkspaceProject(dmnQueryTerms(workspaceProject.getRootPath().toURI()));
        } else {
            return getStandalonePaths(dmnModelFilter());
        }
    }

    @Override
    public List<Path> getPMMLModelsPaths(final WorkspaceProject workspaceProject) {
        if (workspaceProject != null) {
            return getPathsByWorkspaceProject(pmmlQueryTerms(workspaceProject.getRootPath().toURI()));
        } else {
            return getStandalonePaths(pmmlDocumentFilter());
        }
    }

    @Override
    public String getRelativeURI(final Path dmnModelPath,
                                 final Path includedModelPath) {
        //This is true on standalone new diagrams.. move to interface with different impls for standalone and -project
        if (Objects.isNull(dmnModelPath)) {
            return includedModelPath.getFileName();
        }

        final org.uberfire.java.nio.file.Path nioDMN = convertPath(normalizePath(dmnModelPath));
        final org.uberfire.java.nio.file.Path nioIncluded = convertPath(normalizePath(includedModelPath));
        // This can return Path like "../file" for files in the same folder so it needs to be normalised.
        // See https://issues.jboss.org/browse/AF-2045
        final org.uberfire.java.nio.file.Path nioRelative = nioDMN.relativize(nioIncluded);

        // Path.normalise() has a bug for Paths prefixed "../" so strip it manually. toString() works better than toURI().toString()!
        // See https://issues.jboss.org/browse/AF-2046
        final String strRelative = FilenameUtils.separatorsToUnix(nioRelative.toString());
        if (strRelative.startsWith("../")) {
            return strRelative.substring(3);
        } else {
            return strRelative;
        }
    }

    //---------------------------
    // Business Central specific
    //---------------------------

    private List<Path> getPathsByWorkspaceProject(final Set<ValueIndexTerm> queryTerms) {
        final RefactoringPageRequest request = buildRequest(queryTerms);
        return refactoringQueryService
                .query(request)
                .getPageRowList()
                .stream()
                .map(row -> (Path) row.getValue())
                .collect(Collectors.toList());
    }

    private RefactoringPageRequest buildRequest(final Set<ValueIndexTerm> queryTerms) {
        return new RefactoringPageRequest(NAME, queryTerms, 0, 1000, TRUE);
    }

    private Set<ValueIndexTerm> modelsQueryTerms(final String rootPath) {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new DMNValueRepositoryRootIndexTerm(rootPath));
        queryTerms.add(new AllModelsValueFileExtensionIndexTerm());
        return queryTerms;
    }

    private Set<ValueIndexTerm> dmnQueryTerms(final String rootPath) {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new DMNValueRepositoryRootIndexTerm(rootPath));
        queryTerms.add(new DMNValueFileExtensionIndexTerm());
        return queryTerms;
    }

    private Set<ValueIndexTerm> pmmlQueryTerms(final String rootPath) {
        final Set<ValueIndexTerm> queryTerms = new HashSet<>();
        queryTerms.add(new DMNValueRepositoryRootIndexTerm(rootPath));
        queryTerms.add(new PMMLValueFileExtensionIndexTerm());
        return queryTerms;
    }

    //---------------------------
    // Standalone specific
    //---------------------------

    private List<Path> getStandalonePaths(final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter) {
        return StreamSupport
                .stream(getStandaloneModelPaths(filter).spliterator(), false)
                .map(this::convertPath)
                .collect(Collectors.toList());
    }

    DirectoryStream<org.uberfire.java.nio.file.Path> getStandaloneModelPaths(final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> filter) {
        final org.uberfire.java.nio.file.Path root = getStandaloneRootPath();
        return ioService.newDirectoryStream(root, filter);
    }

    DirectoryStream.Filter<org.uberfire.java.nio.file.Path> allModelsFilter() {
        return path -> importTypesHelper.isDMN(convertPath(path)) || importTypesHelper.isPMML(convertPath(path));
    }

    DirectoryStream.Filter<org.uberfire.java.nio.file.Path> dmnModelFilter() {
        return path -> importTypesHelper.isDMN(convertPath(path));
    }

    DirectoryStream.Filter<org.uberfire.java.nio.file.Path> pmmlDocumentFilter() {
        return path -> importTypesHelper.isPMML(convertPath(path));
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

    Path normalizePath(final Path path) {
        return Paths.normalizePath(path);
    }
}
