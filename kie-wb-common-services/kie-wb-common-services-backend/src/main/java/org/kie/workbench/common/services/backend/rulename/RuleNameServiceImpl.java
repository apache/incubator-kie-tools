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

    @Inject
    public RuleNameServiceImpl(SourceServices sourceServices) {
        this.sourceServices = sourceServices;
    }

    public void processResourceAdd(@Observes final ResourceAddedEvent resourceAddedEvent) {
        if (isObservableResource(resourceAddedEvent.getPath())) {
            org.uberfire.java.nio.file.Path convertedPath = Paths.convert(resourceAddedEvent.getPath());

            String drl = sourceServices.getServiceFor(
                    convertedPath).getSource(convertedPath);

            // Needs package resolver
            ruleNames.put("some.pkg", new RuleNameResolver(drl).resolve());
        }
    }

    public void processResourceDelete(@Observes final ResourceDeletedEvent resourceDeletedEvent) {

    }

    public void processResourceUpdate(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
    }

    public void processResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
    }

    public void processResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
    }

    public void processBatchChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {

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
}
