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

package org.kie.workbench.common.workbench.client.error;

import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.jboss.errai.bus.client.api.InvalidBusContentException;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.exception.KieServicesHttpException;
import org.kie.workbench.common.workbench.client.entrypoint.GenericErrorPopup;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceHistoryHandler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback.isInvalidBusContentException;
import static org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback.isKieServerForbiddenException;
import static org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback.isKieServerUnauthorizedException;
import static org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback.isServerOfflineException;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultWorkbenchErrorCallbackTest {

    @Mock
    private GenericErrorPopup genericErrorPopup;

    @Mock
    private GenericErrorTimeController genericErrorTimeController;

    @Mock
    private GenericErrorLoggerProxy genericErrorLoggerProxy;

    @Mock
    private User user;

    @Mock
    private PlaceHistoryHandler placeHistoryHandler;

    @InjectMocks
    private DefaultWorkbenchErrorCallback callback;

    @Test
    public void testForbiddenException() {
        assertTrue(isKieServerForbiddenException(new KieServicesHttpException(null,
                                                                              403,
                                                                              null,
                                                                              null)));
        assertTrue(isKieServerForbiddenException(new DataSetLookupException(null,
                                                                            null,
                                                                            new Exception("Unexpected HTTP response code when requesting URI ''! Error code: 403, message: <html><head><title>Error</title></head><body>Forbidden</body></html>"))));
        assertFalse(isKieServerForbiddenException(new KieServicesHttpException(null,
                                                                               401,
                                                                               null,
                                                                               null)));
        assertFalse(isKieServerForbiddenException(new DataSetLookupException(null,
                                                                             null,
                                                                             new Exception("Unexpected HTTP response code when requesting URI ''! Error code: 401, message: <html><head><title>Error</title></head><body>Unauthorized</body></html>"))));
        assertFalse(isKieServerForbiddenException(new Exception("Some Unexpected HTTP response code when requesting URI")));
    }

    @Test
    public void testUnauthorizedException() {
        assertTrue(isKieServerUnauthorizedException(new KieServicesHttpException(null,
                                                                                 401,
                                                                                 null,
                                                                                 null)));
        assertTrue(isKieServerUnauthorizedException(new DataSetLookupException(null,
                                                                               null,
                                                                               new Exception("Unexpected HTTP response code when requesting URI ''! Error code: 401, message: <html><head><title>Error</title></head><body>Unauthorized</body></html>"))));
        assertFalse(isKieServerUnauthorizedException(new KieServicesHttpException(null,
                                                                                  403,
                                                                                  null,
                                                                                  null)));
        assertFalse(isKieServerUnauthorizedException(new DataSetLookupException(null,
                                                                                null,
                                                                                new Exception("Unexpected HTTP response code when requesting URI ''! Error code: 403, message: <html><head><title>Error</title></head><body>Forbidden</body></html>"))));
        assertFalse(isKieServerUnauthorizedException(new Exception("Some Unexpected HTTP response code when requesting URI")));
    }

    @Test
    public void testInvalidBusContentException() {
        assertTrue(isInvalidBusContentException(new InvalidBusContentException("text/html", "content")));
        assertFalse(isInvalidBusContentException(new RuntimeException()));
    }

    @Test
    public void testIsServerOfflineException() {
        assertTrue(isServerOfflineException(new Exception("Script error. (:0)")));
        assertTrue(isServerOfflineException(new Exception("Error parsing JSON: SyntaxError: Unexpected token � in JSON at position 1")));
        assertTrue(isServerOfflineException(new Exception("Error parsing JSON: SyntaxError: JSON.parse: unexpected character at line 1 column 2 of the JSON data")));
        assertFalse(isServerOfflineException(new Exception("test")));
        assertFalse(isServerOfflineException(new Exception("")));
    }

    @Test
    public void testGenericPopup() {
        callback.queue(new RuntimeException("ex"));

        when(genericErrorTimeController.isExpired()).thenReturn(true);

        callback.processQueue();

        InOrder inOrder = inOrder(genericErrorPopup);
        inOrder.verify(genericErrorPopup).show();
        inOrder.verify(genericErrorPopup).setup(any(), any(), anyString());
        verify(genericErrorLoggerProxy).log(anyString(),
                                            anyString(),
                                            anyString());
    }

    @Test
    public void testDoNotShowGenericPopupIfTimeNotExpired() {
        callback.queue(new RuntimeException("ex"));

        when(genericErrorTimeController.isExpired()).thenReturn(false);

        callback.processQueue();

        verify(genericErrorPopup, never()).show();
        verify(genericErrorPopup, never()).setup(any(), any(), anyString());
        verify(genericErrorLoggerProxy).log(anyString(),
                                            anyString(),
                                            anyString());
    }

    @Test
    public void testQueue() {
        callback.queue(new Exception("a"));
        callback.queue(new Exception("b"));
        callback.queue(new Exception("c"));

        when(genericErrorTimeController.isExpired()).thenReturn(true);

        callback.processQueue();

        InOrder inOrder = inOrder(genericErrorPopup);
        inOrder.verify(genericErrorPopup, times(1)).show();
        inOrder.verify(genericErrorPopup).setup(eq("Uncaught exception: a"), any(), anyString());

        callback.dequeue();
        callback.processQueue();

        inOrder.verify(genericErrorPopup, times(1)).show();
        inOrder.verify(genericErrorPopup).setup(eq("Uncaught exception: b"), any(), anyString());

        callback.dequeue();
        callback.processQueue();

        inOrder.verify(genericErrorPopup, times(1)).show();
        inOrder.verify(genericErrorPopup).setup(eq("Uncaught exception: c"), any(), anyString());
    }
}
