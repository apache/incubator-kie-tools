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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.backend.lookup.impl.VFSLookupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

@ApplicationScoped
@Service
public class WorkItemDefinitionServiceImpl
        implements WorkItemDefinitionService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionServiceImpl.class.getName());

    public static final String PROPERTY_SERVICE_REPO = "org.jbpm.service.repository";
    public static final String PROPERTY_SERVICE_REPO_TASKNAMES = "org.jbpm.service.servicetasknames";

    private final IOService ioService;
    private final WorkItemDefinitionResources resources;
    private final Function<IOService, VFSLookupManager> lookupManagerBuilder;
    private VFSLookupManager<WorkItemDefinitions> vfsLookupManager;

    // CDI proxy.
    protected WorkItemDefinitionServiceImpl() {
        this(null, null);
    }

    @Inject
    public WorkItemDefinitionServiceImpl(final @Named("ioStrategy") IOService ioService,
                                         final WorkItemDefinitionResources resources) {
        this(ioService,
             resources,
             VFSLookupManager::new);
    }

    WorkItemDefinitionServiceImpl(final IOService ioService,
                                  final WorkItemDefinitionResources resources,
                                  final Function<IOService, VFSLookupManager> lookupManagerBuilder) {
        this.ioService = ioService;
        this.resources = resources;
        this.lookupManagerBuilder = lookupManagerBuilder;
    }

    @PostConstruct
    public void init() {
        vfsLookupManager =
                newVFSLookupManager()
                        .setPathAcceptor(WorkItemDefinitionResources::isWorkItemDefinition)
                        .setItemSupplier(this::getAll);
    }

    private WorkItemDefinitions getAll(final Path path) {
        final String content = ioService.readAllString(resources.resolvePath(path));
        try {
            final Collection<WorkItemDefinition> parsed =
                    WorkItemDefinitionParser.parse(content,
                                                   icon -> resources
                                                           .generateIconDataURI(path,
                                                                                icon));
            return new WorkItemDefinitions(parsed);
        } catch (Exception e) {
            LOG.error("Error parsing work item definitions for path [" + path + "]", e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<WorkItemDefinition> get(final Path path) {
        final WorkItemDefinitions all = getAll(path);
        return null != all ? all.definitions : null;
    }

    @Override
    public Collection<WorkItemDefinition> search(final Path path) {
        return vfsLookupManager.getItemsByPath(resources.resolveSearchPath(path))
                .stream()
                .flatMap(items -> items.definitions.stream())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<WorkItemDefinition> fetch(final String url,
                                                final String[] names) {
        return WorkItemDefinitionParser.parse(url,
                                              names);
    }

    @SuppressWarnings("unchecked")
    private VFSLookupManager<WorkItemDefinitions> newVFSLookupManager() {
        return lookupManagerBuilder.apply(ioService);
    }

    public static Collection<WorkItemDefinition> loadRepositoryFromSystemProperties(final WorkItemDefinitionService service) {
        final String url = System.getProperty(PROPERTY_SERVICE_REPO);
        final String taskNamesRaw = System.getProperty(PROPERTY_SERVICE_REPO_TASKNAMES);
        final String[] taskNames =
                null != taskNamesRaw && taskNamesRaw.trim().length() > 0 ?
                        taskNamesRaw.split("\\s*,\\s*") :
                        new String[0];
        return service.fetch(url,
                             taskNames);
    }

    static class WorkItemDefinitions {

        final Collection<WorkItemDefinition> definitions;

        private WorkItemDefinitions(final Collection<WorkItemDefinition> definitions) {
            this.definitions = definitions;
        }
    }
}
