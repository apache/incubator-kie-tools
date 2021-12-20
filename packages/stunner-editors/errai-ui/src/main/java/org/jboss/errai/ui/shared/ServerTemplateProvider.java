/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.shared;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ui.client.local.spi.TemplateProvider;
import org.jboss.errai.ui.client.local.spi.TemplateRenderingCallback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * Retrieves templates from the server using the provided template location as URL.
 * 
 * @author Christian Sadilek <csadilek@redhat.com>
 */
@ApplicationScoped
public class ServerTemplateProvider implements TemplateProvider {

  @Override
  public void provideTemplate(final String url, final TemplateRenderingCallback renderingCallback) {
    final RequestBuilder request = new RequestBuilder(RequestBuilder.GET, url);
    request.setCallback(new RequestCallback() {
      @Override
      public void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() == Response.SC_OK) {
          renderingCallback.renderTemplate(response.getText());
        }
        else {
          throw new RuntimeException("Failed to retrieve template from server at " + url + " (status code: "
                  + response.getStatusCode() + ")");
        }
      }

      @Override
      public void onError(Request request, Throwable exception) {
        throw new RuntimeException("Failed to retrieve template from server at " + url, exception);
      }
    });

    try {
      request.send();
    } 
    catch (RequestException e) {
      throw new RuntimeException("Failed to retrieve template from server at" + request.getUrl(), e);
    }
  }
}
