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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.ProcessDataEvent;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestProcessDataEvent;
import org.kie.workbench.common.stunner.bpmn.project.backend.query.FindBpmnProcessIdsQuery;
import org.uberfire.backend.vfs.Path;

@Dependent
public class ProcessesDataService {

    private final RefactoringQueryService queryService;
    private final Event<ProcessDataEvent> processDataEvent;
    private Supplier<ResourceType> resourceType;
    private Supplier<String> queryName;

    // CDI proxy.
    public ProcessesDataService() {
        this(null, null);
    }

    @Inject
    public ProcessesDataService(final RefactoringQueryService queryService,
                                final Event<ProcessDataEvent> processDataEvent) {
        this.queryService = queryService;
        this.processDataEvent = processDataEvent;
        this.resourceType = () -> ResourceType.BPMN2;
        this.queryName = () -> FindBpmnProcessIdsQuery.NAME;
    }

    public List<String> getBusinessProcessIDs() {
        return getProcessIDs();
    }

    void onRequestProcessDataEvent(final @Observes RequestProcessDataEvent event) {
        fireData();
    }

    public void setResourceType(final Supplier<ResourceType> resourceType) {
        this.resourceType = resourceType;
    }

    public void setQueryName(final Supplier<String> queryName) {
        this.queryName = queryName;
    }

    private void fireData() {
        final List<String> processIds = getBusinessProcessIDs();
        processDataEvent.fire(new ProcessDataEvent(processIds.toArray(new String[processIds.size()])));
    }

    @SuppressWarnings("unchecked")
    private List<String> getProcessIDs() {
        final Set<ValueIndexTerm> queryTerms = new Sets.Builder<ValueIndexTerm>()
                .add(new ValueResourceIndexTerm("*",
                                                resourceType.get(),
                                                ValueIndexTerm.TermSearchType.WILDCARD))
                .build();
        return queryService
                .query(queryName.get(), queryTerms)
                .stream()
                .map(row -> (Map<String, Path>) row.getValue())
                .flatMap(mapRow -> mapRow.keySet().stream())
                .filter(pId -> !"null".equals(pId))
                .distinct()
                .collect(Collectors.toList());
    }
}
