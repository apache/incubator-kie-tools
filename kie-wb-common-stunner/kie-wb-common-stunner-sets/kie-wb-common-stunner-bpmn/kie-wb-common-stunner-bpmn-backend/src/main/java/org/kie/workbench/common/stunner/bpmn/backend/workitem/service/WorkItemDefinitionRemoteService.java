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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionParser;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionService;

@ApplicationScoped
public class WorkItemDefinitionRemoteService
        implements WorkItemDefinitionService<WorkItemDefinitionRemoteRequest> {

    public static Function<String, WorkItemsHolder> DEFAULT_LOOKUP_SERVICE =
            url -> new WorkItemsHolder(WorkItemRepository.getWorkDefinitions(url));

    private final Function<String, WorkItemsHolder> lookupService;

    public WorkItemDefinitionRemoteService() {
        this(DEFAULT_LOOKUP_SERVICE);
    }

    WorkItemDefinitionRemoteService(Function<String, WorkItemsHolder> lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    public Collection<WorkItemDefinition> execute(final WorkItemDefinitionRemoteRequest request) {
        return fetch(lookupService,
                     request.getUri(),
                     request.getNames());
    }

    public static Collection<WorkItemDefinition> fetch(final Function<String, WorkItemsHolder> lookupService,
                                                       final String serviceRepoUrl,
                                                       final String[] names) {
        final String defaultServiceRepo = null != serviceRepoUrl && serviceRepoUrl.trim().length() > 0 ? serviceRepoUrl : null;
        if (defaultServiceRepo != null) {
            final Map<String, WorkDefinitionImpl> workItemsMap = lookupService.apply(defaultServiceRepo).get();
            if (!workItemsMap.isEmpty()) {
                final Stream<WorkDefinitionImpl> items = isAllNames(names) ?
                        workItemsMap.values().stream() :
                        Arrays.stream(names)
                                .filter(workItemsMap::containsKey)
                                .map(workItemsMap::get);
                return items
                        .map(wid -> WorkItemDefinitionParser.parse(wid,
                                                                   WorkItemDefinitionRemoteService::buildUri,
                                                                   WorkItemDefinitionParser::buildDataURIFromURL))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptySet();
    }

    public static class WorkItemsHolder {

        private final Map<String, WorkDefinitionImpl> map;

        WorkItemsHolder(final Map<String, WorkDefinitionImpl> map) {
            this.map = map;
        }

        public Map<String, WorkDefinitionImpl> get() {
            return map;
        }
    }

    private static String buildUri(final WorkDefinitionImpl item) {
        final String path = null != item.getPath() ? item.getPath() : "";
        final String fileName = null != item.getFile() ? item.getFile() : "";
        if (isEmpy(path)) {
            return null;
        }
        return path + "/" + fileName;
    }

    private static boolean isEmpy(final String s) {
        return null == s || s.trim().length() == 0;
    }

    private static boolean isAllNames(final String[] names) {
        return names == null || names.length == 0;
    }
}
