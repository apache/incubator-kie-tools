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

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLIFrameElement;
import elemental2.dom.HTMLParagraphElement;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
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

    @Inject
    @DataField
    HTMLDivElement configurationIssueRoot;

    @Inject
    @DataField
    HTMLParagraphElement configurationDetails;

    @Inject
    ExternalComponentMessageHelper messageHelper;

    private boolean componentLoaded = false;

    private ExternalComponentMessage lastMessage;

    private ExternalComponentMessage initMessage;

    @Override
    public void init(ExternalComponentPresenter presenter) {
        showComponent();
    }

    @Override
    public void setComponentURL(String url) {
        externalComponentIFrame.src = url;
        componentLoaded = false;
        externalComponentIFrame.onload = this::onInvoke;
    }

    @Override
    public void postMessage(ExternalComponentMessage message) {
        if (componentLoaded) {
            postMessageToComponent(message);
        } else {
            if (messageHelper.isInit(message)) {
                this.initMessage = message;
            } else {
                this.lastMessage = message;
            }
        }
    }

    private void postMessageToComponent(Object message) {
        if (externalComponentIFrame != null && externalComponentIFrame.contentWindow != null) {
            externalComponentIFrame.contentWindow.postMessage(message, "*");
        }
    }

    @Override
    public void makeReady() {
        // ready not supported at the moment.
    }

    @Override
    public void configurationIssue(String message) {
        configurationDetails.textContent = message;
        showConfigurationIssue();
    }

    @Override
    public void configurationOk() {
        showComponent();
    }

    private void showConfigurationIssue() {
        configurationIssueRoot.style.display = "block";
        externalComponentIFrame.style.display = "none";
    }

    private void showComponent() {
        configurationIssueRoot.style.display = "none";
        externalComponentIFrame.style.display = "block";
    }

    /**
     * Workaround to resolve generics after GWT upgrade. See https://issues.redhat.com/browse/AF-2542
     */
    private Object onInvoke(Event e) {
        componentLoaded = true;
        if (initMessage != null) {
            postMessageToComponent(initMessage);
        }
        if (lastMessage != null) {
            postMessageToComponent(lastMessage);
        }
        return null;
    }

}
