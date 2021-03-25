/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.project.backend.query.FindBpmnProcessIdsQuery;
import org.kie.workbench.common.stunner.bpmn.project.service.ProjectOpenReusableSubprocessService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
@Service
public class ProjectOpenReusableSubprocessServiceImpl implements ProjectOpenReusableSubprocessService {

    private final RefactoringQueryService queryService;
    private final Supplier<ResourceType> resourceType;
    private final Supplier<String> queryName;
    private final Set<ValueIndexTerm> queryTerms;

    // CDI proxy.
    protected ProjectOpenReusableSubprocessServiceImpl() {
        this(null);
    }

    @Inject
    public ProjectOpenReusableSubprocessServiceImpl(final RefactoringQueryService queryService) {
        this.queryService = queryService;
        this.resourceType = () -> ResourceType.BPMN2;
        this.queryName = () -> FindBpmnProcessIdsQuery.NAME;
        this.queryTerms = new Sets.Builder<ValueIndexTerm>()
                .add(new ValueResourceIndexTerm("*",
                                                resourceType.get(),
                                                ValueIndexTerm.TermSearchType.WILDCARD))
                .build();
    }

    String getQueryName() {
        return queryName.get();
    }

    Set<ValueIndexTerm> createQueryTerms() {
        return queryTerms;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> openReusableSubprocess(String processId) {
        List<String> answer = new ArrayList<>();
        Map<String, Path> subprocesses = queryService
                .query(getQueryName(), createQueryTerms())
                .stream()
                .map(row -> (Map<String, Path>) row.getValue())
                .filter(row -> row.get(processId) != null)
                .findFirst()
                .orElse(null);

        if (subprocesses == null) {
            return answer;
        }

        answer.add(subprocesses.get(processId).getFileName());
        answer.add(subprocesses.get(processId).toURI());
        return answer;
    }
}
