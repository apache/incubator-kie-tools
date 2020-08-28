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

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ExternalComponentPresenter {

    /**
     * The base URL for components server. It should match the 
     */
    private static final String COMPONENT_SERVER_PATH = "dashbuilder/component";
    /**
     * The property that should be used by components to find its unique ID during Runtime.
     */
    private static final String COMPONENT_RUNTIME_ID_PROP = "component_id";
    /**
     * Unique Runtime ID for the component. It is used to identify messages coming from the component.
     */
    final String componentRuntimeId = DOM.createUniqueId();

    private Consumer<ExternalComponentMessage> messageConsumer;

    public interface View extends UberView<ExternalComponentPresenter> {

        void setComponentURL(String url);

        void postMessage(ExternalComponentMessage message);
    }

    @Inject
    View view;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void withComponent(String componentId) {
        String url = buildUrl(componentId);
        view.setComponentURL(url);
    }

    public void withComponent(String componentId, String partition) {
        String url = buildUrl(componentId, partition);
        view.setComponentURL(url);
    }

    public void sendMessage(ExternalComponentMessage message) {
        message.setProperty(COMPONENT_RUNTIME_ID_PROP, componentRuntimeId);
        view.postMessage(message);
    }

    public void receiveMessage(ExternalComponentMessage message) {
        Object destinationId = message.getProperty(COMPONENT_RUNTIME_ID_PROP);
        if (!componentRuntimeId.equals(destinationId)) {
            return;
        }
        if (messageConsumer != null) {
            messageConsumer.accept(message);
        }
    }

    public View getView() {
        return view;
    }

    public void setMessageConsumer(Consumer<ExternalComponentMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public String getComponentId() {
        return componentRuntimeId;
    }
    
    private String buildUrl(String componentId) {
        return buildUrl(componentId, "");
    }

    private String buildUrl(String componentId, String partition) {
        return String.join("/",
                           GWT.getHostPageBaseURL(),
                           COMPONENT_SERVER_PATH,
                           partition, 
                           componentId, 
                           "index.html");
    }

}