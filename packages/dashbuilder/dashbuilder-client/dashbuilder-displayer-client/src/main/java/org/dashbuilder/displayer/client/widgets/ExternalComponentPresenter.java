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
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import org.dashbuilder.displayer.client.component.ExternalComponentDispatcher;
import org.dashbuilder.displayer.client.component.ExternalComponentListener;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.dashbuilder.displayer.external.ExternalFilterRequest;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ExternalComponentPresenter implements ExternalComponentListener {

    /**
     * The base URL for components server. It should match the 
     */
    public static final String COMPONENT_SERVER_PATH = "dashbuilder/component";
    /**
     * Unique Runtime ID for the component. It is used to identify messages coming from the component.
     */
    final String componentRuntimeId = DOM.createUniqueId();

    private Consumer<ExternalFilterRequest> filterConsumer;

    public interface View extends UberView<ExternalComponentPresenter> {

        void setComponentURL(String url);

        void postMessage(ExternalComponentMessage message);

        void makeReady();

        void configurationIssue(String message);

        void configurationOk();

    }

    @Inject
    View view;

    @Inject
    ExternalComponentDispatcher dispatcher;

    @Inject
    ExternalComponentMessageHelper messageHelper;

    @PostConstruct
    public void init() {
        view.init(this);
        dispatcher.register(this);
    }

    @PreDestroy
    public void destroy() {
        dispatcher.unregister(this);
    }

    @Override
    public String getId() {
        return componentRuntimeId;
    }

    @Override
    public void onFilter(ExternalFilterRequest filterRequest) {
        if (filterConsumer != null) {
            filterConsumer.accept(filterRequest);
        }
    }

    @Override
    public void prepare() {
        view.makeReady();
    }

    @Override
    public void onConfigurationIssue(String message) {
        view.configurationIssue(message);
    }

    @Override
    public void configurationOk() {
        view.configurationOk();
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
        messageHelper.withId(message, componentRuntimeId);
        view.postMessage(message);
    }

    public View getView() {
        return view;
    }

    public void setFilterConsumer(Consumer<ExternalFilterRequest> filterConsumer) {
        this.filterConsumer = filterConsumer;
    }

    public String getComponentId() {
        return componentRuntimeId;
    }

    private String buildUrl(String componentId) {
        return buildUrl(componentId, "");
    }

    private String buildUrl(String componentId, String partition) {
        return buildUrl(GWT.getHostPageBaseURL(), componentId, partition);
    }

    String buildUrl(String baseUrl, String componentId, String partition) {
        String url = baseUrl;
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += COMPONENT_SERVER_PATH;
        if (partition != null && !partition.trim().isEmpty()) {
            url += "/" + partition;
        }
        url += "/" + componentId;
        url += "/" + "index.html";
        return url;
    }

}