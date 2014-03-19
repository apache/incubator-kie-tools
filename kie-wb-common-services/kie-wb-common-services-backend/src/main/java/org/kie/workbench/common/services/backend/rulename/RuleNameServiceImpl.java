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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.shared.rulename.RuleNameService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

public class RuleNameServiceImpl
        implements RuleNameService {

    private SourceServices sourceServices;

    private final Map<String, List<String>> ruleNames = new HashMap<String, List<String>>();
    private final Map<Path, PathHandle> pathHandles = new HashMap<Path, PathHandle>();

    @Inject
    public RuleNameServiceImpl(SourceServices sourceServices) {
        this.sourceServices = sourceServices;
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        if (isObservableResource(resourceAddedEvent.getPath())) {
            addRuleNames(resourceAddedEvent.getPath());
        }
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent resourceDeletedEvent) {
        if (isObservableResource(resourceDeletedEvent.getPath())) {
            deleteRuleNames(resourceDeletedEvent.getPath());
        }
    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        if (isObservableResource(resourceUpdatedEvent.getPath())) {
            deleteRuleNames(resourceUpdatedEvent.getPath());
            addRuleNames(resourceUpdatedEvent.getPath());
        }
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {

    }

    private void addRuleNames(Path path) {
        org.uberfire.java.nio.file.Path convertedPath = Paths.convert(path);

        String drl = sourceServices.getServiceFor(
                convertedPath).getSource(convertedPath);

        RuleNameResolver ruleNameResolver = new RuleNameResolver(drl);
        if (ruleNames.containsKey(ruleNameResolver.getPackageName())) {
            ruleNames.get(ruleNameResolver.getPackageName()).addAll(ruleNameResolver.getRuleNames());
        } else {
            ruleNames.put(ruleNameResolver.getPackageName(), ruleNameResolver.getRuleNames());
        }
        pathHandles.put(path, new PathHandle(ruleNameResolver.getPackageName(), ruleNameResolver.getRuleNames()));
    }

    private void deleteRuleNames(Path path) {
        if (pathHandles.containsKey(path)) {
            PathHandle pathHandle = pathHandles.get(path);

            for (String deleteRuleName : pathHandle.ruleNames) {
                List<String> strings = this.ruleNames.get(pathHandle.packageName);
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

    @Override
    public List<String> getRuleNames(String packageName) {
        List<String> result = ruleNames.get(packageName);
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    private class PathHandle {

        String packageName;
        List<String> ruleNames;

        public PathHandle(String packageName, ArrayList<String> ruleNames) {
            this.packageName = packageName;
            this.ruleNames = ruleNames;
        }
    }
}
