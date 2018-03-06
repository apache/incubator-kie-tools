/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.assets;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AssetQueryServiceTest {

    private AssetQueryService assetQueryService;

    @Mock
    private Caller<LibraryService> caller;

    @Mock
    private LibraryService libraryService;

    @Mock
    private ProjectAssetsQuery query;

    private List<RemoteCallback<?>> remoteCallbacks;
    private List<ErrorCallback<Message>> errorCallbacks;

    @Before
    public void setup() {
        remoteCallbacks = new ArrayList<>();
        errorCallbacks = new ArrayList<>();
        assetQueryService = new AssetQueryService(caller);

        when(caller.call(any(), any()))
        .then(inv -> {
            remoteCallbacks.add(inv.getArgumentAt(0, RemoteCallback.class));
            errorCallbacks.add(inv.getArgumentAt(1, ErrorCallback.class));

            return libraryService;
        });
    }

    @Test
    public void onlyLatestGetAssetCallbackIsInvocable() throws Exception {
        final boolean[] observed = { false, false };
        assetQueryService.getAssets(query).call(ignore -> {
            throw new AssertionError("First callback should not be invoked.");
        }, (msg, error) -> {
            throw new AssertionError("First error callback should not be invoked.");
        });

        assetQueryService.getAssets(query).call(ignore -> observed[0] = true, (msg, error) -> observed[1] = true);

        RemoteCallback<?> firstCallback = remoteCallbacks.get(0);
        ErrorCallback<Message> firstErrorCallback = errorCallbacks.get(0);
        RemoteCallback<?> secondCallback = remoteCallbacks.get(1);
        ErrorCallback<Message> secondErrorCallback = errorCallbacks.get(1);

        firstCallback.callback(null);
        firstErrorCallback.error(null, null);

        secondCallback.callback(null);
        assertTrue("Latest callback not invoked.", observed[0]);
        secondErrorCallback.error(null, null);
        assertTrue("Latest error callback not invoked.", observed[1]);
    }

    @Test
    public void onlyLatestGetAssetCountCallbackIsInvocable() throws Exception {
        final boolean[] observed = { false, false };
        assetQueryService.getNumberOfAssets(query).call(ignore -> {
            throw new AssertionError("First callback should not be invoked.");
        }, (msg, error) -> {
            throw new AssertionError("First error callback should not be invoked.");
        });

        assetQueryService.getNumberOfAssets(query).call(ignore -> observed[0] = true, (msg, error) -> observed[1] = true);

        RemoteCallback<?> firstCallback = remoteCallbacks.get(0);
        ErrorCallback<Message> firstErrorCallback = errorCallbacks.get(0);
        RemoteCallback<?> secondCallback = remoteCallbacks.get(1);
        ErrorCallback<Message> secondErrorCallback = errorCallbacks.get(1);

        firstCallback.callback(null);
        firstErrorCallback.error(null, null);

        secondCallback.callback(null);
        assertTrue("Latest callback not invoked.", observed[0]);
        secondErrorCallback.error(null, null);
        assertTrue("Latest error callback not invoked.", observed[1]);
    }

    @Test
    public void getAssetsAndGetNumberOfAssetsDoNotEffectEachother() throws Exception {
        final boolean[] getAssets = { false, false };
        final boolean[] getNumberOfAssets = { false, false };

        assetQueryService.getAssets(query).call(ignore -> getAssets[0] = true, (msg, error) -> getAssets[1] = true);
        assetQueryService.getNumberOfAssets(query).call(ignore -> getNumberOfAssets[0] = true, (msg, error) -> getNumberOfAssets[1] = true);

        assertFalse(getAssets[0]);
        remoteCallbacks.get(0).callback(null);
        assertTrue(getAssets[0]);

        assertFalse(getAssets[1]);
        errorCallbacks.get(0).error(null, null);
        assertTrue(getAssets[1]);

        assertFalse(getNumberOfAssets[0]);
        remoteCallbacks.get(1).callback(null);
        assertTrue(getNumberOfAssets[0]);

        assertFalse(getNumberOfAssets[1]);
        errorCallbacks.get(1).error(null, null);
        assertTrue(getNumberOfAssets[1]);
    }

}
