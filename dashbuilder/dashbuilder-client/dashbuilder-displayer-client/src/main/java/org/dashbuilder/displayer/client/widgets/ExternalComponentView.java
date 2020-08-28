/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.MessageEvent;
import jsinterop.base.Js;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ExternalComponentView extends Composite implements ExternalComponentPresenter.View {

    @Inject
    @DataField
    HTMLDivElement componentRoot;

    @Inject
    @DataField
    HTMLIFrameElement externalComponentIFrame;

    private boolean componentReady = false;

    private JavaScriptObject lastProps;

    private ExternalComponentMessage lastMessage;

    @Override
    public void init(ExternalComponentPresenter presenter) {
        DomGlobal.window.addEventListener("message", e -> {
            MessageEvent<Object> event = Js.cast(e);
            if (event.data instanceof ExternalComponentMessage) {
                ExternalComponentMessage message = Js.cast(event.data);
                presenter.receiveMessage(message);
            }
        });
    }

    @Override
    public void setComponentURL(String url) {
        externalComponentIFrame.src = url;
        componentReady = false;
        externalComponentIFrame.onload = e -> {
            componentReady = true;
            if (lastProps != null) {
                postMessageToComponent(lastProps);
            }
            if (lastMessage != null) {
                postMessageToComponent(lastMessage);
            }
            return null;
        };
    }

    @Override
    public void postMessage(ExternalComponentMessage message) {
        this.lastMessage = message;
        if (componentReady) {
            postMessageToComponent(message);
        }

    }

    private void postMessageToComponent(Object message) {
        if (externalComponentIFrame != null && externalComponentIFrame.contentWindow != null) {
            externalComponentIFrame.contentWindow.postMessage(message, Window.Location.getHref());
        }
    }

}