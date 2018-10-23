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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.Command;

import static com.google.gwt.http.client.Response.SC_OK;
import static com.google.gwt.http.client.Response.SC_UNAUTHORIZED;

@Dependent
public class FooterPresenter {

    private final View view;

    @Inject
    public FooterPresenter(final View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setup(final String url,
                      final String version) {

        setupVersion(version);
        setupUrl(url);
    }

    void setupVersion(final String version) {
        view.setupVersion(version);
    }

    void setupUrl(final String url) {

        final Command onSuccess = makeOnSuccess(url);
        final Command onError = makeOnError();

        try {
            makeRequest(url, onSuccess, onError).send();
        } catch (final RequestException e) {
            onError.execute();
        }
    }

    Command makeOnSuccess(final String url) {
        return () -> view.setupUrl(url);
    }

    Command makeOnError() {
        return view::hideUrlElements;
    }

    RequestBuilder makeRequest(final String url,
                               final Command onSuccess,
                               final Command onError) {

        final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        final String none = "none";

        builder.setUser(none);
        builder.setPassword(none);
        builder.setCallback(makeCallback(onSuccess, onError));
        builder.setHeader("Accept", "*/*");
        return builder;
    }

    RequestCallback makeCallback(final Command onSuccess, final Command onError) {
        return new RequestCallback() {

            public void onResponseReceived(final Request request,
                                           final Response response) {
                if (isKieServerAccessible(response)) {
                    onSuccess.execute();
                } else {
                    onError.execute();
                }
            }

            public void onError(final Request request,
                                final Throwable exception) {
                onError.execute();
            }
        };
    }

    boolean isKieServerAccessible(final Response response) {
        final int statusCode = response.getStatusCode();
        return statusCode == SC_OK || statusCode == SC_UNAUTHORIZED;
    }

    public interface View extends IsWidget {

        void setupUrl(final String url);

        void setupVersion(final String version);

        void hideUrlElements();
    }
}
