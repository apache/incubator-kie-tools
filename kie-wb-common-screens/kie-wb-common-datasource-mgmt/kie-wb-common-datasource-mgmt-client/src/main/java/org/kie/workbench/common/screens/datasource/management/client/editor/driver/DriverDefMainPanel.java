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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;

@Dependent
public class DriverDefMainPanel
        implements DriverDefMainPanelView.Presenter,
                   IsElement {

    private DriverDefMainPanelView view;

    private DriverDefMainPanelView.Handler handler;

    @Inject
    public DriverDefMainPanel(DriverDefMainPanelView view) {
        this.view = view;
        view.init(this);
    }

    public void setHandler(DriverDefMainPanelView.Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onNameChange() {
        if (handler != null) {
            handler.onNameChange();
        }
    }

    @Override
    public void onDriverClassChange() {
        if (handler != null) {
            handler.onDriverClassChange();
        }
    }

    @Override
    public void onGroupIdChange() {
        if (handler != null) {
            handler.onGroupIdChange();
        }
    }

    @Override
    public void onArtifactIdChange() {
        if (handler != null) {
            handler.onArtifactIdChange();
        }
    }

    @Override
    public void onVersionChange() {
        if (handler != null) {
            handler.onVersionChange();
        }
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setName(final String name) {
        view.setName(name);
    }

    public String getName() {
        return view.getName();
    }

    public void setNameErrorMessage(final String message) {
        view.setNameErrorMessage(message);
    }

    public void clearNameErrorMessage() {
        view.clearNameErrorMessage();
    }

    public void setDriverClass(final String driverClass) {
        view.setDriverClass(driverClass);
    }

    public String getDriverClass() {
        return view.getDriverClass();
    }

    public void setDriverClassErrorMessage(final String message) {
        view.setDriverClassErrorMessage(message);
    }

    public void clearDriverClassErrorMessage() {
        view.clearDriverClassErrorMessage();
    }

    public String getGroupId() {
        return view.getGroupId();
    }

    public void setGroupId(final String groupId) {
        view.setGroupId(groupId);
    }

    public void setGroupIdErrorMessage(final String message) {
        view.setGroupIdErrorMessage(message);
    }

    public void clearGroupIdErrorMessage() {
        view.clearGroupIdErrorMessage();
    }

    public String getArtifactId() {
        return view.getArtifactId();
    }

    public void setArtifactId(final String artifactId) {
        view.setArtifactId(artifactId);
    }

    public void setArtifactIdErrorMessage(final String message) {
        view.setArtifactIdErrorMessage(message);
    }

    public void clearArtifactIdErrorMessage() {
        view.clearArtifactIdErrorMessage();
    }

    public String getVersion() {
        return view.getVersion();
    }

    public void setVersion(final String version) {
        view.setVersion(version);
    }

    public void setVersionErrorMessage(final String message) {
        view.setVersionErrorMessage(message);
    }

    public void clearVersionErrorMessage() {
        view.clearVersionErrorMessage();
    }

    public void clear() {
        view.setName(null);
        view.clearNameErrorMessage();
        view.setGroupId(null);
        view.clearGroupIdErrorMessage();
        view.setArtifactId(null);
        view.clearArtifactIdErrorMessage();
        view.setVersion(null);
        view.clearVersionErrorMessage();
        view.setDriverClass(null);
        view.clearDriverClassErrorMessage();
    }
}
