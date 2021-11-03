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

package org.uberfire.backend.vfs.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceCopied;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

@Portable
@Dependent
public class ObservablePathImpl implements ObservablePath {

    private Path path;
    private transient Path original;

    @Inject
    transient SessionInfo sessionInfo;

    private transient List<Command> onRenameCommand = new ArrayList<Command>();
    private transient List<Command> onDeleteCommand = new ArrayList<Command>();
    private transient List<Command> onUpdateCommand = new ArrayList<Command>();
    private transient List<Command> onCopyCommand = new ArrayList<Command>();
    private transient List<ParameterizedCommand<OnConcurrentRenameEvent>> onConcurrentRenameCommand = new ArrayList<ParameterizedCommand<OnConcurrentRenameEvent>>();
    private transient List<ParameterizedCommand<OnConcurrentDelete>> onConcurrentDeleteCommand = new ArrayList<ParameterizedCommand<OnConcurrentDelete>>();
    private transient List<ParameterizedCommand<OnConcurrentUpdateEvent>> onConcurrentUpdateCommand = new ArrayList<ParameterizedCommand<OnConcurrentUpdateEvent>>();
    private transient List<ParameterizedCommand<OnConcurrentCopyEvent>> onConcurrentCopyCommand = new ArrayList<ParameterizedCommand<OnConcurrentCopyEvent>>();

    public ObservablePathImpl() {
    }

    public static String removeExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0,
                                      index);
        }
    }

    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return -1;
        }
        final int extensionPos = filename.lastIndexOf(".");
        return extensionPos;
    }

    @Override
    public ObservablePath wrap(final Path path) {
        if (path instanceof ObservablePathImpl) {
            this.original = ((ObservablePathImpl) path).path;
        } else {
            this.original = path;
        }
        this.path = this.original;
        return this;
    }

    // Lazy-population of "original" for ObservablePathImpl de-serialized from a serialized PerspectiveDefinition that circumvent the "wrap" feature.
    // Renamed resources hold a reference to the old "original" Path which is needed to maintain an immutable hashCode used as part of the compound
    // Key for Activity and Place Management). However re-hydration stores the PartDefinition in a HashSet using the incorrect hashCode. By not
    // storing the "original" in the serialized form we can guarantee hashCodes in de-serialized PerspectiveDefinitions remain immutable.
    // See https://bugzilla.redhat.com/show_bug.cgi?id=1200472 for the re-producer.
    public Path getOriginal() {
        if (this.original == null) {
            wrap(this.path);
        }
        return this.original;
    }

    @Override
    public String getFileName() {
        return path.getFileName();
    }

    @Override
    public String toURI() {
        return path.toURI();
    }

    @Override
    public int compareTo(final Path o) {
        return path.compareTo(o);
    }

    @Override
    public void onRename(final Command command) {
        this.onRenameCommand.add(command);
    }

    @Override
    public void onDelete(final Command command) {
        this.onDeleteCommand.add(command);
    }

    @Override
    public void onUpdate(final Command command) {
        this.onUpdateCommand.add(command);
    }

    @Override
    public void onCopy(final Command command) {
        this.onCopyCommand.add(command);
    }

    @Override
    public void dispose() {
        onRenameCommand.clear();
        onDeleteCommand.clear();
        onUpdateCommand.clear();
        onCopyCommand.clear();
        onConcurrentRenameCommand.clear();
        onConcurrentDeleteCommand.clear();
        onConcurrentUpdateCommand.clear();
        onConcurrentCopyCommand.clear();
        if (IOC.getBeanManager() != null) {
            IOC.getBeanManager().destroyBean(this);
        }
    }

    void onResourceRenamed(@Observes final ResourceRenamedEvent renamedEvent) {
        if (path != null && path.equals(renamedEvent.getPath())) {
            path = renamedEvent.getDestinationPath();
            if (sessionInfo.getId().equals(renamedEvent.getSessionInfo().getId())) {
                executeRenameCommands();
            } else {
                executeConcurrentRenameCommand(renamedEvent.getPath(),
                                               renamedEvent.getDestinationPath(),
                                               renamedEvent.getSessionInfo().getId());
            }
        }
    }

    void onResourceDeleted(@Observes final ResourceDeletedEvent deletedEvent) {
        if (path != null && path.equals(deletedEvent.getPath())) {
            if (sessionInfo.getId().equals(deletedEvent.getSessionInfo().getId())) {
                executeDeleteCommands();
            } else {
                executeConcurrentDeleteCommand(deletedEvent.getPath(),
                                               deletedEvent.getSessionInfo().getId());
            }
        }
    }

    void onResourceUpdated(@Observes final ResourceUpdatedEvent updatedEvent) {
        if (path != null && path.equals(updatedEvent.getPath())) {
            if (sessionInfo.getId().equals(updatedEvent.getSessionInfo().getId())) {
                executeUpdateCommands();
            } else {
                executeConcurrentUpdateCommand(updatedEvent.getPath(),
                                               updatedEvent.getSessionInfo().getId());
            }
        }
    }

    void onResourceCopied(@Observes final ResourceCopiedEvent copiedEvent) {
        if (path != null && path.equals(copiedEvent.getPath())) {
            if (sessionInfo.getId().equals(copiedEvent.getSessionInfo().getId())) {
                executeCopyCommands();
            } else {
                executeConcurrentCopyCommand(copiedEvent.getPath(),
                                             copiedEvent.getDestinationPath(),
                                             copiedEvent.getSessionInfo().getId());
            }
        }
    }

    void onResourceBatchEvent(@Observes final ResourceBatchChangesEvent batchEvent) {
        if (path != null && batchEvent.containPath(path)) {
            if (sessionInfo.getId().equals(batchEvent.getSessionInfo().getId())) {
                for (final ResourceChange change : batchEvent.getChanges(path)) {
                    switch (change.getType()) {
                        case COPY:
                            executeCopyCommands();
                            break;
                        case DELETE:
                            executeDeleteCommands();
                            break;
                        case RENAME:
                            path = ((ResourceRenamed) change).getDestinationPath();
                            executeRenameCommands();
                            break;
                        case UPDATE:
                            executeUpdateCommands();
                            break;
                    }
                }
            } else {
                for (final ResourceChange change : batchEvent.getChanges(path)) {
                    switch (change.getType()) {
                        case COPY:
                            executeConcurrentCopyCommand(path,
                                                         ((ResourceCopied) change).getDestinationPath(),
                                                         batchEvent.getSessionInfo().getId());
                            break;
                        case DELETE:
                            executeConcurrentDeleteCommand(path,
                                                           batchEvent.getSessionInfo().getId());
                            break;
                        case RENAME:
                            executeConcurrentRenameCommand(path,
                                                           ((ResourceRenamed) change).getDestinationPath(),
                                                           batchEvent.getSessionInfo().getId());
                            path = ((ResourceRenamed) change).getDestinationPath();
                            break;
                        case UPDATE:
                            executeConcurrentUpdateCommand(path,
                                                           batchEvent.getSessionInfo().getId());
                            break;
                    }
                }
            }
        }
    }

    private void executeRenameCommands() {
        if (!onRenameCommand.isEmpty()) {
            for (final Command command : onRenameCommand) {
                command.execute();
            }
        }
    }

    void executeConcurrentRenameCommand(final Path path,
                                        final Path destinationPath,
                                        final String sessionId) {
        if (!onConcurrentRenameCommand.isEmpty()) {
            for (final ParameterizedCommand<OnConcurrentRenameEvent> command : onConcurrentRenameCommand) {
                final OnConcurrentRenameEvent event = new OnConcurrentRenameEvent() {
                    @Override
                    public Path getSource() {
                        return path;
                    }

                    @Override
                    public Path getTarget() {
                        return destinationPath;
                    }

                    @Override
                    public String getId() {
                        return sessionId;
                    }

                };
                command.execute(event);
            }
        }
    }

    private void executeCopyCommands() {
        if (!onCopyCommand.isEmpty()) {
            for (final Command command : onCopyCommand) {
                command.execute();
            }
        }
    }

    void executeConcurrentCopyCommand(final Path path,
                                      final Path destinationPath,
                                      final String id) {
        if (!onConcurrentCopyCommand.isEmpty()) {
            final OnConcurrentCopyEvent copyEvent = new OnConcurrentCopyEvent() {
                @Override
                public Path getSource() {
                    return path;
                }
                
                
                @Override
                public String getId() {
                    return id;
                }

                @Override
                public Path getTarget() {
                    return destinationPath;
                }
            };
            for (final ParameterizedCommand<OnConcurrentCopyEvent> command : onConcurrentCopyCommand) {
                command.execute(copyEvent);
            }
        }
    }

    private void executeUpdateCommands() {
        if (!onUpdateCommand.isEmpty()) {
            for (final Command command : onUpdateCommand) {
                command.execute();
            }
        }
    }

    void executeConcurrentUpdateCommand(final Path path,
                                        final String sessionId) {
        if (!onConcurrentUpdateCommand.isEmpty()) {
            final OnConcurrentUpdateEvent event = new OnConcurrentUpdateEvent() {
                @Override
                public Path getPath() {
                    return path;
                }
                
                @Override
                public String getId() {
                    return sessionId;
                }

            };
            for (final ParameterizedCommand<OnConcurrentUpdateEvent> command : onConcurrentUpdateCommand) {
                command.execute(event);
            }
        }
    }

    private void executeDeleteCommands() {
        if (!onDeleteCommand.isEmpty()) {
            for (final Command command : onDeleteCommand) {
                command.execute();
            }
        }
    }

    void executeConcurrentDeleteCommand(final Path path,
                                        final String sessionId) {
        if (!onConcurrentDeleteCommand.isEmpty()) {
            final OnConcurrentDelete event = new OnConcurrentDelete() {
                @Override
                public Path getPath() {
                    return path;
                }

                @Override
                public String getId() {
                    return sessionId;
                }

            };
            for (final ParameterizedCommand<OnConcurrentDelete> command : onConcurrentDeleteCommand) {
                command.execute(event);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Path)) {
            return false;
        }

        if (o instanceof ObservablePathImpl) {
            return this.getOriginal().equals(((ObservablePathImpl) o).getOriginal());
        }

        return this.getOriginal().equals(o);
    }

    @Override
    public int hashCode() {
        return this.getOriginal().toURI().hashCode();
    }

    @Override
    public String toString() {
        return toURI();
    }
}
