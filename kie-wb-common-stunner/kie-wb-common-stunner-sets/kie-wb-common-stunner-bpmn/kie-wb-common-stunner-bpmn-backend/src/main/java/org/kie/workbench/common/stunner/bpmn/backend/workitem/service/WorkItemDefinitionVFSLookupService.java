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

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

import static org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionParser.parse;

@ApplicationScoped
@Service
public class WorkItemDefinitionVFSLookupService
        implements WorkItemDefinitionService<Metadata> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionVFSLookupService.class.getName());

    private final VFSService vfsService;
    private final WorkItemDefinitionResources resources;

    // CDI proxy.
    protected WorkItemDefinitionVFSLookupService() {
        this.vfsService = null;
        this.resources = null;
    }

    @Inject
    public WorkItemDefinitionVFSLookupService(final VFSService vfsService,
                                              final WorkItemDefinitionResources resources) {
        this.vfsService = vfsService;
        this.resources = resources;
    }

    @Override
    public Collection<WorkItemDefinition> execute(final Metadata metadata) {
        return search(metadata);
    }

    public Collection<WorkItemDefinition> search(final Metadata metadata) {
        return resources.resolveResources(metadata)
                .stream()
                .flatMap(path -> search(metadata, path).stream())
                .collect(Collectors.toSet());
    }

    public Collection<WorkItemDefinition> search(final Metadata metadata,
                                                 final Path root) {
        final DirectoryStream<Path> files =
                vfsService.newDirectoryStream(root,
                                              WorkItemDefinitionVFSLookupService::isWorkItemPathValid);
        return StreamSupport.stream(files.spliterator(),
                                    false)
                .flatMap(resource -> get(metadata, resource).stream())
                .collect(Collectors.toList());
    }

    public Collection<WorkItemDefinition> get(final Metadata metadata,
                                              final Path resource) {
        final String content = vfsService.readAllString(resource);
        try {

            return parse(content,
                         wid -> resource.toURI(),
                         icon -> resources.generateIconDataURI(metadata,
                                                               resource,
                                                               icon));
        } catch (Exception e) {
            LOG.error("Error parsing work item definitions for path [" + resource + "]", e);
            return Collections.emptyList();
        }
    }

    private static boolean isWorkItemPathValid(final Path path) {
        return WorkItemDefinitionResources.isWorkItemDefinition(path) &&
                !WorkItemDefinitionResources.isHidden(path);
    }
}
