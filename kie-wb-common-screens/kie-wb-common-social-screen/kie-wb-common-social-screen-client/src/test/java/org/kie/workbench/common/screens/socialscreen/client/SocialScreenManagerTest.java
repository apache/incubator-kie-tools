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

package org.kie.workbench.common.screens.socialscreen.client;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SocialScreenManagerTest {

    private SocialScreenManager manager;
    private MetadataServiceCallerMock metadataService;

    @Before
    public void setUp() throws Exception {
        PlaceManager placeManager = mock(PlaceManager.class);
        metadataService = new MetadataServiceCallerMock();
        manager = new SocialScreenManager(placeManager, metadataService);

    }

    @Test
    public void testLoadMetadata() throws Exception {

        final Path path = openResource();

        Callback<Metadata> callback = createCallback(path);

        manager.getMetaData(callback);

        verify(callback).callback(any(Metadata.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadMetadataWhenThereAreNoActiveAssetEditors() throws Exception {

        Callback<Metadata> callback = spy(new Callback<Metadata>() {
            @Override
            public void callback(Metadata metadata) {
            }
        });

        manager.getMetaData(callback);
    }

    @Test
    public void testLoadMetadataCache() throws Exception {

        final Path path = openResource();

        Callback<Metadata> callback = createCallback(path);

        manager.getMetaData(callback);

        manager.getMetaData(callback);

        verify(callback, times(2)).callback(any(Metadata.class));

        verify(metadataService.service, times(1)).getMetadata(path);
    }

    @Test
    public void testLoadMetadataIfActiveAssetChanges() throws Exception {

        final Path path1 = openResource();

        Callback<Metadata> callback1 = createCallback(path1);

        manager.getMetaData(callback1);

        Path path2 = openResource();

        Callback<Metadata> callback2 = createCallback(path2);

        manager.getMetaData(callback2);

        verify(callback1, times(1)).callback(metadataService.metadatas.get(path1));
        verify(callback2, times(1)).callback(metadataService.metadatas.get(path2));

        verify(metadataService.service, times(1)).getMetadata(path1);
        verify(metadataService.service, times(1)).getMetadata(path2);

    }

    private Callback<Metadata> createCallback(final Path path) {
        return spy(new Callback<Metadata>() {
            @Override
            public void callback(Metadata metadata) {
                assertEquals(metadataService.metadatas.get(path), metadata);
            }
        });
    }

    private Path openResource() {
        Path path = mock(Path.class);
        SessionInfo sessionInfo = mock(SessionInfo.class);

        metadataService.metadatas.put(path, new Metadata());

        manager.onResourceOpenedEvent(new ResourceOpenedEvent(path, sessionInfo));

        return path;
    }

    private class MetadataServiceCallerMock
            implements Caller<MetadataService> {

        protected Map<Path, Metadata> metadatas = new HashMap<Path, Metadata>();

        private RemoteCallback callback;

        private MetadataService service = spy(new MetadataService() {
            @Override
            public Metadata getMetadata(Path resource) {
                callback.callback(metadatas.get(resource));
                return null;
            }

            @Override
            public Map<String, Object> configAttrs(Map<String, Object> attrs, Metadata metadata) {
                callback.callback(null);
                return null;
            }

            @Override
            public Map<String, Object> setUpAttributes(Path path, Metadata metadata) {
                callback.callback(null);
                return null;
            }
        });

        @Override
        public MetadataService call() {
            return service;
        }

        @Override
        public MetadataService call(RemoteCallback<?> remoteCallback) {
            callback = remoteCallback;
            return service;
        }

        @Override
        public MetadataService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            callback = remoteCallback;
            return service;
        }
    }
}
