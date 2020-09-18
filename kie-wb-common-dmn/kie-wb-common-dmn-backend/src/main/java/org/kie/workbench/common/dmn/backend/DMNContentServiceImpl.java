/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.dmn.api.DMNContentResource;
import org.kie.workbench.common.dmn.api.DMNContentService;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.marshalling.DMNPathsHelper;
import org.kie.workbench.common.dmn.backend.common.DMNIOHelper;
import org.kie.workbench.common.dmn.backend.editors.common.PMMLIncludedDocumentFactory;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.options.CommentedOption;

@Service
@ApplicationScoped
public class DMNContentServiceImpl extends KieService<String> implements DMNContentService {

    private final CommentedOptionFactory commentedOptionFactory;

    private final DMNIOHelper dmnIOHelper;

    private final DMNPathsHelper pathsHelper;

    private final PMMLIncludedDocumentFactory pmmlIncludedDocumentFactory;

    @Inject
    public DMNContentServiceImpl(final CommentedOptionFactory commentedOptionFactory,
                                 final DMNIOHelper dmnIOHelper,
                                 final DMNPathsHelper pathsHelper,
                                 final PMMLIncludedDocumentFactory pmmlIncludedDocumentFactory) {
        this.commentedOptionFactory = commentedOptionFactory;
        this.dmnIOHelper = dmnIOHelper;
        this.pathsHelper = pathsHelper;
        this.pmmlIncludedDocumentFactory = pmmlIncludedDocumentFactory;
    }

    @Override
    public String getContent(final Path path) {
        return getSource(path);
    }

    @Override
    public DMNContentResource getProjectContent(final Path path,
                                                final String defSetId) {

        final String content = getSource(path);
        final String title = path.getFileName();
        final ProjectMetadata metadata = buildMetadataInstance(path, defSetId, title);

        return new DMNContentResource(content, metadata);
    }

    @Override
    public void saveContent(final Path path,
                            final String content,
                            final Metadata metadata,
                            final String comment) {

        try {
            ioService.write(convertPath(path),
                            content,
                            getAttrs(path, metadata),
                            getCommentedOption(comment));
        } catch (final Exception e) {
            logger.error("Error while saving diagram.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Path> getModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getModelsPaths(workspaceProject);
    }

    @Override
    public List<Path> getDMNModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getDMNModelsPaths(workspaceProject);
    }

    @Override
    public List<Path> getPMMLModelsPaths(final WorkspaceProject workspaceProject) {
        return pathsHelper.getPMMLModelsPaths(workspaceProject);
    }

    @Override
    public PMMLDocumentMetadata loadPMMLDocumentMetadata(final Path path) {
        return pmmlIncludedDocumentFactory.getDocumentByPath(path);
    }

    @Override
    protected String constructContent(final Path path,
                                      final Overview _overview) {
        return getSource(path);
    }

    @Override
    public String getSource(final Path path) {
        return loadPath(path).map(dmnIOHelper::isAsString).orElse("");
    }

    private CommentedOption getCommentedOption(final String comment) {
        return commentedOptionFactory.makeCommentedOption(comment);
    }

    private Map<String, Object> getAttrs(final Path path,
                                         final Metadata metadata) {
        return Optional
                .ofNullable(metadata)
                .map(m -> metadataService.setUpAttributes(path, m))
                .orElse(new HashMap<>());
    }

    private ProjectMetadata buildMetadataInstance(final Path path,
                                                  final String defSetId,
                                                  final String title) {
        final Package modulePackage = moduleService.resolvePackage(path);
        final KieModule kieModule = moduleService.resolveModule(path);
        final Overview overview = overviewLoader.loadOverview(path);
        return new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(defSetId)
                .forModuleName(kieModule.getModuleName())
                .forProjectPackage(modulePackage)
                .forOverview(overview)
                .forTitle(title)
                .forPath(path)
                .build();
    }

    private Optional<InputStream> loadPath(final Path path) {
        try {
            return Optional.ofNullable(ioService.newInputStream(convertPath(path)));
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

    org.uberfire.java.nio.file.Path convertPath(final Path path) {
        return Paths.convert(path);
    }
}
