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

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ExternalComponentEditor implements IsWidget {

    public interface View extends UberView<ExternalComponentEditor> {

    }

    @Inject
    View view;

    @Inject
    ExternalComponentPropertiesEditor propertiesEditor;

    @Inject
    ExternalComponentPresenter externalComponentPresenter;

    private Map<String, String> newProperties;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void withComponent(String componentId, Map<String, String> properties) {
        propertiesEditor.init(componentId, properties, this::propertiesChange);
        externalComponentPresenter.withComponent(componentId);
        propertiesChange(properties);
    }

    public void propertiesChange(Map<String, String> propertiesChange) {
        this.newProperties = propertiesChange;
        ExternalComponentMessage message = ExternalComponentMessage.create(propertiesChange);
        externalComponentPresenter.sendMessage(message);
    }

    public Map<String, String> getNewProperties() {
        return newProperties;
    }

    public ExternalComponentPresenter getExternalComponentPresenter() {
        return externalComponentPresenter;
    }

    public ExternalComponentPropertiesEditor getPropertiesEditor() {
        return propertiesEditor;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}