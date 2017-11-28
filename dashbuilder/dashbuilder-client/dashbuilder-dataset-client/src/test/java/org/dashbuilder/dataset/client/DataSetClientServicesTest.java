/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.dataset.client;

import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.service.DataSetLookupServices;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetClientServicesTest {

    @Mock
    private ClientDataSetManager clientDataSetManager;

    @Mock
    private DataSetMetadata dataSetMetadata;

    @Mock
    private DataSetLookupServices dataSetLookupServices;

    private CallerMock<DataSetLookupServices> dataSetLookupServicesCallerMock;

    private boolean isCallbackCalled = false;

    private boolean isNotFoundCalled = false;

    private boolean isOnErrorCalled = false;

    @Before
    public void setup() {
        dataSetLookupServicesCallerMock = new CallerMock<>(dataSetLookupServices);
    }

    @Test
    public void testFetchMetadataWhenMetadataIsNotNull() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         dataSetLookupServicesCallerMock);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(dataSetMetadata);

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertTrue(isDataSetMetadataCallbackCalled());
        assertFalse(isDataSetMetadataNotFoundCallbackCalled());
        assertFalse(isDataSetMetadataOnErrorCallbackCalled());
        assertNull(services.getRemoteMetadataMap().get(uuid));
    }

    @Test
    public void testFetchMetadataWhenSetLookupServicesIsNull() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         null);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(null);

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertFalse(isDataSetMetadataCallbackCalled());
        assertTrue(isDataSetMetadataNotFoundCallbackCalled());
        assertFalse(isDataSetMetadataOnErrorCallbackCalled());
        assertNull(services.getRemoteMetadataMap().get(uuid));
    }

    @Test
    public void testFetchMetadataWhenRemoteMetadataMapContainsTheUUID() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         dataSetLookupServicesCallerMock);

        services.getRemoteMetadataMap().put(uuid,
                                            null);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(null);

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertTrue(isDataSetMetadataCallbackCalled());
        assertFalse(isDataSetMetadataNotFoundCallbackCalled());
        assertFalse(isDataSetMetadataOnErrorCallbackCalled());
        assertNull(services.getRemoteMetadataMap().get(uuid));
    }

    @Test
    public void testFetchMetadataWhenResultIsNull() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         dataSetLookupServicesCallerMock);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(null);
        when(dataSetLookupServices.lookupDataSetMetadata(eq(uuid))).thenReturn(null);

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertFalse(isDataSetMetadataCallbackCalled());
        assertTrue(isDataSetMetadataNotFoundCallbackCalled());
        assertFalse(isDataSetMetadataOnErrorCallbackCalled());
        assertNull(services.getRemoteMetadataMap().get(uuid));
    }

    @Test
    public void testFetchMetadataWhenResultIsNotNull() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         dataSetLookupServicesCallerMock);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(null);
        when(dataSetLookupServices.lookupDataSetMetadata(eq(uuid))).thenReturn(dataSetMetadata);

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertTrue(isDataSetMetadataCallbackCalled());
        assertFalse(isDataSetMetadataNotFoundCallbackCalled());
        assertFalse(isDataSetMetadataOnErrorCallbackCalled());
        assertEquals(services.getRemoteMetadataMap().get(uuid),
                     dataSetMetadata);
    }

    @Test
    public void testFetchMetadataWhenDataSetLookupServicesReturnsAnError() throws Exception {
        final String uuid = "uuid";
        final DataSetClientServices services = makeDataSetClientServices(clientDataSetManager,
                                                                         dataSetLookupServicesCallerMock);

        when(clientDataSetManager.getDataSetMetadata(uuid)).thenReturn(null);

        doThrow(Exception.class).when(dataSetLookupServices).lookupDataSetMetadata(any());

        services.fetchMetadata(uuid,
                               makeDataSetMetadataCallback());

        assertFalse(isDataSetMetadataCallbackCalled());
        assertFalse(isDataSetMetadataNotFoundCallbackCalled());
        assertTrue(isDataSetMetadataOnErrorCallbackCalled());
        assertNull(services.getRemoteMetadataMap().get(uuid));
    }

    private DataSetMetadataCallback makeDataSetMetadataCallback() {
        return new DataSetMetadataCallback() {
            @Override
            public void callback(final DataSetMetadata metadata) {
                callbackCalled();
            }

            @Override
            public void notFound() {
                notFoundCalled();
            }

            @Override
            public boolean onError(final ClientRuntimeError error) {
                onErrorCalled();

                return false;
            }
        };
    }

    private void onErrorCalled() {
        isOnErrorCalled = true;
    }

    private void notFoundCalled() {
        isNotFoundCalled = true;
    }

    private void callbackCalled() {
        isCallbackCalled = true;
    }

    public boolean isDataSetMetadataCallbackCalled() {
        return isCallbackCalled;
    }

    public boolean isDataSetMetadataNotFoundCallbackCalled() {
        return isNotFoundCalled;
    }

    public boolean isDataSetMetadataOnErrorCallbackCalled() {
        return isOnErrorCalled;
    }

    private DataSetClientServices makeDataSetClientServices(final ClientDataSetManager clientDataSetManager,
                                                            final CallerMock<DataSetLookupServices> dataSetLookupServicesCallerMock) {
        return new DataSetClientServices(clientDataSetManager,
                                         null,
                                         null,
                                         null,
                                         null,
                                         null,
                                         null,
                                         dataSetLookupServicesCallerMock,
                                         null,
                                         null);
    }
}
