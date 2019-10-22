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
package org.kie.workbench.common.stunner.cm.backend.dataproviders;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.project.backend.service.ProcessesDataService;
import org.kie.workbench.common.stunner.cm.backend.query.FindCaseManagementIdsQuery;

@Dependent
public class CaseCalledElementFormDataProvider {

    private final ProcessesDataService dataService;

    @Inject
    public CaseCalledElementFormDataProvider(final ProcessesDataService dataService) {
        this.dataService = dataService;
    }

    @PostConstruct
    public void init() {
        dataService.setQueryName(() -> FindCaseManagementIdsQuery.NAME);
        dataService.setResourceType(() -> ResourceType.BPMN_CM);
    }

    public Map<Object, String> getBusinessProcessIDs() {
        return toMap(dataService.getBusinessProcessIDs());
    }

    private static Map<Object, String> toMap(final Iterable<String> items) {
        return StreamSupport.stream(items.spliterator(), false).collect(Collectors.toMap(s -> s, s -> s));
    }
}
