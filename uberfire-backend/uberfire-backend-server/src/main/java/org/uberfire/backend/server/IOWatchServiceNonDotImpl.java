/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server;

import java.util.concurrent.ExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.backend.server.io.watch.AbstractIOWatchService;
import org.uberfire.commons.concurrent.Unmanaged;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@ApplicationScoped
public class IOWatchServiceNonDotImpl extends AbstractIOWatchService {

    public IOWatchServiceNonDotImpl() {
    }

    @Inject
    public IOWatchServiceNonDotImpl(Event<ResourceBatchChangesEvent> resourceBatchChanges,
                                    Event<ResourceUpdatedEvent> resourceUpdatedEvent,
                                    Event<ResourceRenamedEvent> resourceRenamedEvent,
                                    Event<ResourceDeletedEvent> resourceDeletedEvent,
                                    Event<ResourceAddedEvent> resourceAddedEvent,
                                    @Unmanaged ExecutorService executorService) {

        super(resourceBatchChanges,
              resourceUpdatedEvent,
              resourceRenamedEvent,
              resourceDeletedEvent,
              resourceAddedEvent,
              executorService);
    }

    @Override
    public boolean doFilter(WatchEvent<?> object) {
        final WatchContext context = (WatchContext) object.context();
        if (object.kind().equals(StandardWatchEventKind.ENTRY_MODIFY)) {
            if (shouldFilter(context.getOldPath())) {
                return true;
            }
        } else if (object.kind().equals(StandardWatchEventKind.ENTRY_CREATE)) {
            if (shouldFilter(context.getPath())) {
                return true;
            }
        } else if (object.kind().equals(StandardWatchEventKind.ENTRY_RENAME)) {
            if (shouldFilter(context.getOldPath())) {
                return true;
            }
        } else if (object.kind().equals(StandardWatchEventKind.ENTRY_DELETE)) {
            if (shouldFilter(context.getOldPath())) {
                return true;
            }
        }
        return false;
    }

    boolean shouldFilter(Path path) {
        return path != null && path.getFileName() != null && path.getFileName().toString().startsWith(".");
    }
}
