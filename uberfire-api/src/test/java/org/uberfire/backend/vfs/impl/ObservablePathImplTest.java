/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceCopied;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamed;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdated;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ObservablePathImplTest {

    @Spy
    @InjectMocks
    private ObservablePathImpl observablePathImpl;

    @Mock
    private PathPlaceRequest pathPlaceRequest;

    @Mock
    private Path destinationPath;

    @Mock
    private ParameterizedCommand<ObservablePath.OnConcurrentDelete> onDelete;

    @Mock
    private ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent> onUpdate;

    @Mock
    private ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent> onRename;

    @Mock
    private ParameterizedCommand<ObservablePath.OnConcurrentCopyEvent> onCopy;

    private static final User identityUser = new UserImpl("userName");
    private static final String MESSAGE = "test message",
            ASSET_PATH = "default://project/package/Asset.java",
            OBSERVE_SESSION_ID = "observeSession",
            RESOURCE_SESSION_ID = "resourceSession";

    @Before
    public void setup() {
        observablePathImpl.onConcurrentDelete(onDelete);
        observablePathImpl.onConcurrentUpdate(onUpdate);
        observablePathImpl.onConcurrentRename(onRename);
        observablePathImpl.onConcurrentCopy(onCopy);
        observablePathImpl.sessionInfo = new SessionInfoImpl(OBSERVE_SESSION_ID,
                                                             identityUser);
    }

    @Test
    public void testResourceDeleteEvent() {
        doReturn(createPath()).when(pathPlaceRequest).getPath();
        observablePathImpl.onResourceDeleted(new ResourceDeletedEvent(pathPlaceRequest.getPath(),
                                                                      MESSAGE,
                                                                      createSessionInfo()));

        verify(onDelete).execute(any());
        verify(observablePathImpl).executeConcurrentDeleteCommand(pathPlaceRequest.getPath(),
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
    }

    @Test
    public void testResourceUpdateEvent() {
        doReturn(createPath()).when(pathPlaceRequest).getPath();
        observablePathImpl.onResourceUpdated(new ResourceUpdatedEvent(pathPlaceRequest.getPath(),
                                                                      MESSAGE,
                                                                      createSessionInfo()));

        verify(onUpdate).execute(any());
        verify(observablePathImpl).executeConcurrentUpdateCommand(pathPlaceRequest.getPath(),
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
    }

    @Test
    public void testResourceRenameEvent() {
        doReturn(createPath()).when(pathPlaceRequest).getPath();
        observablePathImpl.onResourceRenamed(new ResourceRenamedEvent(pathPlaceRequest.getPath(),
                                                                      destinationPath,
                                                                      MESSAGE,
                                                                      createSessionInfo()));

        verify(onRename).execute(any());
        verify(observablePathImpl).executeConcurrentRenameCommand(pathPlaceRequest.getPath(),
                                                                  destinationPath,
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
    }

    @Test
    public void testResourceCopyEvent() {
        doReturn(createPath()).when(pathPlaceRequest).getPath();
        observablePathImpl.onResourceCopied(new ResourceCopiedEvent(pathPlaceRequest.getPath(),
                                                                    destinationPath,
                                                                    MESSAGE,
                                                                    createSessionInfo()));

        verify(onCopy).execute(any());
        verify(observablePathImpl).executeConcurrentCopyCommand(pathPlaceRequest.getPath(),
                                                                destinationPath,
                                                                RESOURCE_SESSION_ID,
                                                                identityUser);
    }

    @Test
    public void testResourceBatchEvent() {
        doReturn(createPath()).when(pathPlaceRequest).getPath();
        final Path path = pathPlaceRequest.getPath();
        final Map<Path, Collection<ResourceChange>> batchEvents = new HashMap<Path, Collection<ResourceChange>>() {
            {
                put(path,
                    new ArrayList<ResourceChange>() {{
                        add(new ResourceCopied(destinationPath, "copied event"));
                        add(new ResourceDeleted("deleted event"));
                        add(new ResourceRenamed(destinationPath, "renamed event"));
                        add(new ResourceUpdated("updated event"));
                    }});
            }
        };

        observablePathImpl.onResourceBatchEvent(
                new ResourceBatchChangesEvent(batchEvents, MESSAGE, createSessionInfo())
        );

        verify(onCopy).execute(any());
        verify(onDelete).execute(any());
        verify(onRename).execute(any());
        verify(onUpdate).execute(any());

        verify(observablePathImpl).executeConcurrentCopyCommand(pathPlaceRequest.getPath(),
                                                                destinationPath,
                                                                RESOURCE_SESSION_ID,
                                                                identityUser);
        verify(observablePathImpl).executeConcurrentDeleteCommand(pathPlaceRequest.getPath(),
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
        verify(observablePathImpl).executeConcurrentRenameCommand(pathPlaceRequest.getPath(),
                                                                  destinationPath,
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
        verify(observablePathImpl).executeConcurrentUpdateCommand(destinationPath,
                                                                  RESOURCE_SESSION_ID,
                                                                  identityUser);
    }

    private ObservablePath createPath() {
        final ObservablePath path = mock(ObservablePath.class);
        doReturn(ASSET_PATH).when(path).toURI();
        observablePathImpl.wrap(path);

        return path;
    }

    private SessionInfoImpl createSessionInfo() {
        return new SessionInfoImpl("resourceSession",
                                   identityUser);
    }
}
