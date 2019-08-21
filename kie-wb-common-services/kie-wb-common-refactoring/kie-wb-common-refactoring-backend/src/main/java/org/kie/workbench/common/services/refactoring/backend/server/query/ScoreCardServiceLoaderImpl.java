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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindScorecardNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ScoreCardServiceLoader;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class ScoreCardServiceLoaderImpl
        implements ScoreCardServiceLoader {

    @Inject
    private RefactoringQueryService refactoringQueryService;

    @Inject
    private WorkspaceProjectService projectService;

    public Set<String> find(final Path path,
                            final String packageName) {
        final Set<String> result = new HashSet<>();

        try {
            final WorkspaceProject workspaceProject = projectService.resolveProject(path);

            final List<RefactoringPageRow> rows = refactoringQueryService.query(FindScorecardNamesQuery.NAME,
                                                                                new HashSet<ValueIndexTerm>() {{
                                                                                    add(new ValueSharedPartIndexTerm(
                                                                                            "*",
                                                                                            PartType.SCORECARD_MODEL_NAME,
                                                                                            ValueIndexTerm.TermSearchType.WILDCARD));
                                                                                    add(new ValueModuleRootPathIndexTerm(
                                                                                            workspaceProject.getMainModule().getRootPath().toURI()));
                                                                                    add(new ValuePackageNameIndexTerm(
                                                                                            packageName));
                                                                                }});

            for (RefactoringPageRow row : rows) {
                result.add(row.getValue().toString());
            }
        } finally {
            return result;
        }
    }
}
