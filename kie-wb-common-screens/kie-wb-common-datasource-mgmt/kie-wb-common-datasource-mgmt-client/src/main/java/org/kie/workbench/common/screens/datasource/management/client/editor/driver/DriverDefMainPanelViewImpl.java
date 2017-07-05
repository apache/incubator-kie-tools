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

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.clearSpanMessage;
import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.setGroupOnError;
import static org.kie.workbench.common.screens.datasource.management.client.util.UIUtil.setSpanMessage;

@Dependent
@Templated
public class DriverDefMainPanelViewImpl
        implements IsElement,
                   DriverDefMainPanelView {

    @Inject
    @DataField("name-form-group")
    private Div nameFormGroup;

    @Inject
    @DataField("name")
    private TextInput nameTextBox;

    @Inject
    @DataField("name-help")
    private Span nameHelp;

    @Inject
    @DataField("driver-class-form-group")
    private Div driverClassFormGroup;

    @Inject
    @DataField("driver-class")
    private TextInput driverClassTextBox;

    @Inject
    @DataField("driver-class-help")
    private Span driverClassHelp;

    @Inject
    @DataField("group-id-form-group")
    private Div groupIdFormGroup;

    @Inject
    @DataField("group-id")
    private TextInput groupIdTextBox;

    @Inject
    @DataField("group-id-help")
    private Span groupIdHelp;

    @Inject
    @DataField("artifact-id-form-group")
    private Div artifactIdFormGroup;

    @Inject
    @DataField("artifact-id")
    private TextInput artifactIdTextBox;

    @Inject
    @DataField("artifact-id-help")
    private Span artifactIdHelp;

    @Inject
    @DataField("version-form-group")
    private Div versionFormGroup;

    @Inject
    @DataField("version")
    private TextInput versionTextBox;

    @Inject
    @DataField("version-help")
    private Span versionHelp;

    private DriverDefMainPanelView.Presenter presenter;

    public DriverDefMainPanelViewImpl() {
    }

    @Override
    public void init(final DriverDefMainPanelView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(final String name) {
        this.nameTextBox.setValue(name);
    }

    @Override
    public String getName() {
        return nameTextBox.getValue();
    }

    @Override
    public void setNameErrorMessage(final String message) {
        setGroupOnError(nameFormGroup,
                        true);
        setSpanMessage(nameHelp,
                       message);
    }

    @Override
    public void clearNameErrorMessage() {
        setGroupOnError(nameFormGroup,
                        false);
        clearSpanMessage(nameHelp);
    }

    @Override
    public void setDriverClass(final String driverClass) {
        this.driverClassTextBox.setValue(driverClass);
    }

    @Override
    public String getDriverClass() {
        return driverClassTextBox.getValue();
    }

    @Override
    public void setDriverClassErrorMessage(final String message) {
        setGroupOnError(driverClassFormGroup,
                        true);
        setSpanMessage(driverClassHelp,
                       message);
    }

    @Override
    public void clearDriverClassErrorMessage() {
        setGroupOnError(driverClassFormGroup,
                        false);
        clearSpanMessage(driverClassHelp);
    }

    @Override
    public void setGroupId(final String groupId) {
        groupIdTextBox.setValue(groupId);
    }

    @Override
    public String getGroupId() {
        return groupIdTextBox.getValue();
    }

    @Override
    public void setGroupIdErrorMessage(final String message) {
        setGroupOnError(groupIdFormGroup,
                        true);
        setSpanMessage(groupIdHelp,
                       message);
    }

    @Override
    public void clearGroupIdErrorMessage() {
        setGroupOnError(groupIdFormGroup,
                        false);
        clearSpanMessage(groupIdHelp);
    }

    @Override
    public void setArtifactId(final String artifactId) {
        artifactIdTextBox.setValue(artifactId);
    }

    @Override
    public String getArtifactId() {
        return artifactIdTextBox.getValue();
    }

    @Override
    public void setArtifactIdErrorMessage(final String message) {
        setGroupOnError(artifactIdFormGroup,
                        true);
        setSpanMessage(artifactIdHelp,
                       message);
    }

    @Override
    public void clearArtifactIdErrorMessage() {
        setGroupOnError(artifactIdFormGroup,
                        false);
        clearSpanMessage(artifactIdHelp);
    }

    @Override
    public void setVersion(final String version) {
        versionTextBox.setValue(version);
    }

    @Override
    public String getVersion() {
        return versionTextBox.getValue();
    }

    @Override
    public void setVersionErrorMessage(final String message) {
        setGroupOnError(versionFormGroup,
                        true);
        setSpanMessage(versionHelp,
                       message);
    }

    @Override
    public void clearVersionErrorMessage() {
        setGroupOnError(versionFormGroup,
                        false);
        clearSpanMessage(versionHelp);
    }

    @EventHandler("name")
    private void onNameChange(@ForEvent("change") final Event event) {
        presenter.onNameChange();
    }

    @EventHandler("driver-class")
    private void onDriverClassChange(@ForEvent("change") final Event event) {
        presenter.onDriverClassChange();
    }

    @EventHandler("group-id")
    private void onGroupIdChange(@ForEvent("change") final Event event) {
        presenter.onGroupIdChange();
    }

    @EventHandler("artifact-id")
    private void onArtifactIdChange(@ForEvent("change") final Event event) {
        presenter.onArtifactIdChange();
    }

    @EventHandler("version")
    private void onVersionChange(@ForEvent("change") final Event event) {
        presenter.onVersionChange();
    }
}
