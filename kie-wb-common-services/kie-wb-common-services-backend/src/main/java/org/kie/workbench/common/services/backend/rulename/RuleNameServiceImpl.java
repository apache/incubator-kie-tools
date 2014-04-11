/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.rulename;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceCopied;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@Service
@ApplicationScoped
public class RuleNameServiceImpl
        implements RuleNamesService {

    private SourceServices sourceServices;
    private ProjectService projectService;

    private final Map<Project, RuleNamesByPackageMap> ruleNames = new HashMap<Project, RuleNamesByPackageMap>();
    private final Map<Path, PathHandle> pathHandles = new HashMap<Path, PathHandle>();

    public RuleNameServiceImpl() {
    }

    @Inject
    public RuleNameServiceImpl(SourceServices sourceServices, ProjectService projectService) {
        this.sourceServices = sourceServices;
        this.projectService = projectService;
    }

    @Override
    public Collection<String> getRuleNames(
            final Path path,
            final String packageName) {

        final Project project = projectService.resolveProject(path);

        if (project == null) {
            return Collections.emptyList();
        } else {
            return getRuleNames(project, packageName);
        }
    }

    public void onProjectContextChange(@Observes final ProjectContextChangeEvent event) {
        visitPaths(Files.newDirectoryStream(Paths.convert(event.getProject().getRootPath())));
    }

    private void visitPaths(final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream) {
        for (final org.uberfire.java.nio.file.Path path : directoryStream) {
            if (Files.isDirectory(path)) {
                visitPaths(Files.newDirectoryStream(path));
            } else {
                processResourceAdd(Paths.convert(path));
            }
        }
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        processResourceAdd(resourceAddedEvent.getPath());
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent resourceDeletedEvent) {
        processResourceDelete(resourceDeletedEvent.getPath());
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        processResourceUpdate(resourceUpdatedEvent.getPath());
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
        if (isObservableResource(resourceCopiedEvent.getDestinationPath())) {
            addRuleNames(resourceCopiedEvent.getDestinationPath());
        }
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
        processRename(resourceRenamedEvent.getPath(), resourceRenamedEvent.getDestinationPath());
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        for (Path path : resourceBatchChangesEvent.getBatch().keySet()) {
            Collection<ResourceChange> resourceChanges = resourceBatchChangesEvent.getBatch().get(path);
            for (ResourceChange resourceChange : resourceChanges) {
                switch (resourceChange.getType()) {
                    case ADD:
                        processResourceAdd(path);
                        break;
                    case UPDATE:
                        processResourceUpdate(path);
                        break;
                    case DELETE:
                        processResourceDelete(path);
                        break;
                    case COPY:
                        processResourceAdd(((ResourceCopied) resourceChange).getDestinationPath());
                        break;
                    case RENAME:
                        ResourceRenamed resourceRenamed = (ResourceRenamed) resourceChange;
                        processRename(path, resourceRenamed.getDestinationPath());
                        break;
                }
            }
        }
    }

    private void processResourceAdd(Path path) {
        if (isObservableResource(path)) {
            addRuleNames(path);
        }
    }

    private void processResourceDelete(Path path) {
        if (isObservableResource(path)) {
            deleteRuleNames(path);
        }
    }

    private void processResourceUpdate(Path path) {
        if (isObservableResource(path)) {
            deleteRuleNames(path);
            addRuleNames(path);
        }
    }

    private void processRename(Path path, Path destinationPath) {
        if (isObservableResource(path)) {
            deleteRuleNames(path);
        }
        if (isObservableResource(destinationPath)) {
            addRuleNames(destinationPath);
        }
    }

    private void addRuleNames(Path path) {
        org.uberfire.java.nio.file.Path convertedPath = Paths.convert(path);

        if (sourceServices.hasServiceFor(convertedPath)) {
            String drl = sourceServices.getServiceFor(
                    convertedPath).getSource(convertedPath);

            Project project = projectService.resolveProject(path);

            RuleNameResolver ruleNameResolver = new RuleNameResolver(drl);
            if (ruleNames.containsKey(project)) {
                ruleNames.get(project).add(ruleNameResolver.getPackageName(), ruleNameResolver.getRuleNames());
            } else {
                RuleNamesByPackageMap map = new RuleNamesByPackageMap();
                map.add(ruleNameResolver.getPackageName(), ruleNameResolver.getRuleNames());
                ruleNames.put(project, map);
            }
            pathHandles.put(path, new PathHandle(ruleNameResolver.getPackageName(), ruleNameResolver.getRuleNames()));
        }
    }

    private void deleteRuleNames(Path path) {
        if (pathHandles.containsKey(path)) {
            PathHandle pathHandle = pathHandles.get(path);

            Project project = projectService.resolveProject(path);

            for (String deleteRuleName : pathHandle.ruleNames) {
                Set<String> strings = this.ruleNames.get(project).get(pathHandle.packageName);
                strings.remove(deleteRuleName);
            }
        }
    }

    private boolean isObservableResource(Path path) {
        return path != null
                && (path.getFileName().endsWith(".drl")
                || path.getFileName().endsWith(".gdst")
                || path.getFileName().endsWith(".rdrl")
                || path.getFileName().endsWith(".rdslr")
                || path.getFileName().endsWith(".template")
        );
    }

    public Set<String> getRuleNames(Project project, String packageName) {
        RuleNamesByPackageMap map = ruleNames.get(project);
        if (map == null) {
            return new HashSet<String>();
        }

        Set<String> result = map.get(packageName);
        if (result == null) {
            return new HashSet<String>();
        } else {
            return new HashSet<String>(result);
        }
    }

    private class RuleNamesByPackageMap {

        HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();

        void add(String packageName, Set<String> ruleNames) {
            if (map.containsKey(packageName)) {
                map.get(packageName).addAll(ruleNames);
            } else {
                map.put(packageName, ruleNames);
            }
        }

        public Set<String> get(String packageName) {
            return map.get(packageName);
        }
    }

    private class PathHandle {

        String packageName;
        Set<String> ruleNames;

        public PathHandle(String packageName, Set<String> ruleNames) {
            this.packageName = packageName;
            this.ruleNames = ruleNames;
        }
    }
}
