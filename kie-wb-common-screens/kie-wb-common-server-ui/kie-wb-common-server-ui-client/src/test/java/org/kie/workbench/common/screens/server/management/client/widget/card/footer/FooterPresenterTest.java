/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.widget.card.footer;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FooterPresenterTest {

    @Mock
    private FooterPresenter.View view;

    @Mock
    private Command onSuccess;

    @Mock
    private Command onError;

    @Mock
    private Request request;

    @Mock
    private Response response;

    private FooterPresenter presenter;

    @Before
    public void setup() {

        presenter = spy(new FooterPresenter(view));

        doReturn(onSuccess).when(presenter).makeOnSuccess(anyString());
        doReturn(onError).when(presenter).makeOnError();
    }

    @Test
    public void testInit() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testSetup() {

        final String url = "url";
        final String version = "1.0.1";

        presenter.setup(url, version);

        verify(presenter).setupVersion(version);
        verify(presenter).setupUrl(url);
    }

    @Test
    public void testSetupVersion() {

        final String version = "1.0.1";

        presenter.setupVersion(version);

        verify(view).setupVersion(version);
    }

    @Test
    public void testSetupUrlWhenRequestIsValid() throws RequestException {

        final String url = "http://localhost:8080/kie-server/services/rest/server";
        final RequestBuilder builder = mock(RequestBuilder.class);

        doReturn(builder).when(presenter).makeRequest(url, onSuccess, onError);

        presenter.setupUrl(url);

        verify(builder).send();
    }

    @Test
    public void testSetupUrlWhenMakeRequestRaisesAnError() throws RequestException {

        final String url = "http://localhost:8080/kie-server/services/rest/server";
        final RequestBuilder builder = mock(RequestBuilder.class);
        final RequestException requestException = mock(RequestException.class);

        doReturn(builder).when(presenter).makeRequest(url, onSuccess, onError);
        doThrow(requestException).when(builder).send();

        presenter.setupUrl(url);

        verify(onError).execute();
    }

    @Test
    public void testMakeOnSuccess() {

        final String url = "http://localhost:8080/kie-server/services/rest/server";

        doCallRealMethod().when(presenter).makeOnSuccess(url);

        presenter.makeOnSuccess(url).execute();

        verify(view).setupUrl(url);
    }

    @Test
    public void testMakeOnError() {

        doCallRealMethod().when(presenter).makeOnError();

        presenter.makeOnError().execute();

        verify(view).hideUrlElements();
    }

    @Test
    public void testMakeRequest() {

        final String url = "http://localhost:8080/kie-server/services/rest/server";
        final String none = "none";
        final RequestCallback callback = mock(RequestCallback.class);
        final String expectedHTTPMethod = RequestBuilder.GET.toString();

        doReturn(callback).when(presenter).makeCallback(onSuccess, onError);

        final RequestBuilder builder = presenter.makeRequest(url, onSuccess, onError);

        assertEquals(expectedHTTPMethod, builder.getHTTPMethod());
        assertEquals(url, builder.getUrl());
        assertEquals(none, builder.getUser());
        assertEquals(none, builder.getPassword());
        assertEquals(callback, builder.getCallback());
    }

    @Test
    public void testCallbackOnResponseReceivedWhenKieServerIsAccessible() {

        doReturn(true).when(presenter).isKieServerAccessible(any());

        final RequestCallback callback = presenter.makeCallback(onSuccess, onError);
        callback.onResponseReceived(request, response);

        verify(onSuccess).execute();
        verify(onError, never()).execute();
    }

    @Test
    public void testCallbackOnResponseReceivedWhenKieServerIsNotAccessible() {

        doReturn(false).when(presenter).isKieServerAccessible(any());

        final RequestCallback callback = presenter.makeCallback(onSuccess, onError);
        callback.onResponseReceived(request, response);

        verify(onError).execute();
        verify(onSuccess, never()).execute();
    }

    @Test
    public void testCallbackOnError() {

        final Throwable throwable = mock(Throwable.class);

        final RequestCallback callback = presenter.makeCallback(onSuccess, onError);
        callback.onError(request, throwable);

        verify(onError).execute();
        verify(onSuccess, never()).execute();
    }

    @Test
    public void testIsKieServerAccessibleWhenStatusCodeIs200() {

        when(response.getStatusCode()).thenReturn(200);

        assertTrue(presenter.isKieServerAccessible(response));
    }

    @Test
    public void testIsKieServerAccessibleWhenStatusCodeIs401() {

        when(response.getStatusCode()).thenReturn(401);

        assertTrue(presenter.isKieServerAccessible(response));
    }

    @Test
    public void testIsKieServerAccessibleWhenStatusCodeIs500() {

        when(response.getStatusCode()).thenReturn(500);

        assertFalse(presenter.isKieServerAccessible(response));
    }
}
