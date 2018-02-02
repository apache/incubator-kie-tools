/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.events.DeleteModuleEvent;
import org.guvnor.common.services.project.model.Module;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;

/**
 * A bridge between changes made to an underlying VFS and Module abstractions. Modules can be deleted
 * either through the Workbench's UI or via REST or on a cloned repository that's pushed back to the Workbench.
 * Interested observers are signaled of a Module deletion event when a pom.xml file is detected as deleted.
 */
public abstract class AbstractDeleteModuleObserverBridge<T extends Module> {

    private IOService ioService;
    private Event<DeleteModuleEvent> deleteModuleEvent;

    public AbstractDeleteModuleObserverBridge() {
        //Zero-arg constructor for CDI proxying
    }

    public AbstractDeleteModuleObserverBridge(final IOService ioService,
                                              final Event<DeleteModuleEvent> deleteModuleEvent) {
        this.ioService = PortablePreconditions.checkNotNull("ioService",
                                                            ioService);
        this.deleteModuleEvent = PortablePreconditions.checkNotNull("deleteModuleEvent",
                                                                    deleteModuleEvent);
    }

    public void onBatchResourceChanges(final ResourceDeletedEvent event) {
        if (event.getPath().getFileName().equals("pom.xml")) {
            fireDeleteEvent(event.getPath());
        }
    }

    public void onBatchResourceChanges(final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        for (final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> entry : resourceBatchChangesEvent.getBatch().entrySet()) {
            if (entry.getKey().getFileName().equals("pom.xml") && isDelete(entry.getValue())) {
                fireDeleteEvent(entry.getKey());
            }
        }
    }

    private boolean isDelete(final Collection<ResourceChange> value) {
        for (final ResourceChange resourceChange : value) {
            if (resourceChange instanceof ResourceDeleted) {
                return true;
            }
        }
        return false;
    }

    private void fireDeleteEvent(final org.uberfire.backend.vfs.Path _path) {
        final Path path = ioService.get(URI.create(_path.toURI()));
        final T project = getModule(path.getParent());
        deleteModuleEvent.fire(new DeleteModuleEvent(project));
    }

    protected abstract T getModule(final Path path);
}
