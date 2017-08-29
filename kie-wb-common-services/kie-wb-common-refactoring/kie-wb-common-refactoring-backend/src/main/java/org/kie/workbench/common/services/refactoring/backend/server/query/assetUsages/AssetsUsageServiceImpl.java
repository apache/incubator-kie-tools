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

package org.kie.workbench.common.services.refactoring.backend.server.query.assetUsages;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.kie.workbench.common.services.refactoring.service.impact.RefactorOperationBuilder;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.base.SegmentedPath;

@Service
@ApplicationScoped
public class AssetsUsageServiceImpl implements AssetsUsageService {

    private KieProjectService projectService;

    private RefactoringQueryService refactoringQueryService;

    @Inject
    public AssetsUsageServiceImpl(KieProjectService projectService,
                                  RefactoringQueryService refactoringQueryService) {
        this.projectService = projectService;
        this.refactoringQueryService = refactoringQueryService;
    }

    @Override
    public List<Path> getAssetUsages(String resourceFQN,
                                     ResourceType resourceType,
                                     Path assetPath) {

        return getQueryList(assetPath,
                            QueryOperationRequest
                                    .references(resourceFQN,
                                                resourceType));
    }

    @Override
    public List<Path> getAssetPartUsages(String resourceFQN,
                                         String resourcePart,
                                         PartType partType,
                                         Path assetPath) {

        return getQueryList(assetPath,
                            QueryOperationRequest.referencesPart(resourceFQN,
                                                                 resourcePart,
                                                                 partType));
    }

    protected List<Path> getQueryList(Path assetPath,
                                      RefactorOperationBuilder<QueryOperationRequest>.RequiresProject builder) {
        KieProject project = projectService.resolveProject(assetPath);

        String branch = "master";

        org.uberfire.java.nio.file.Path nioPath = Paths.convert(assetPath);

        if (nioPath instanceof SegmentedPath) {
            branch = ((SegmentedPath) nioPath).getSegmentId();
        }

        QueryOperationRequest request = builder.inProjectRootPathURI(project.getRootPath().toURI()).onBranch(branch);

        return refactoringQueryService.queryToList(request).stream().map(row -> (Path) row.getValue()).collect(Collectors.toList());
    }
}
