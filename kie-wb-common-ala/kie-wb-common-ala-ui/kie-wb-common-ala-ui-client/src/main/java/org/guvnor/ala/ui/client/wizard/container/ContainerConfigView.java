/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.ui.client.wizard.container;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.widget.FormStatus;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.widget.StyleHelper.setFormStatus;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class ContainerConfigView
        implements IsElement,
                   ContainerConfigPresenter.View {

    @Inject
    @DataField("form-error")
    private Div formError;

    @Inject
    @DataField("form-error-message")
    private Span formErrorMessage;

    @Inject
    @DataField("container-name-form")
    private Div containerNameForm;

    @Inject
    @DataField("container-name")
    private TextInput containerName;

    @Inject
    @DataField("container-name-help-block")
    private Span containerNameHelp;

    @Inject
    @DataField("group-id-form")
    private Div groupIdForm;

    @Inject
    @DataField("group-id")
    private TextInput groupId;

    @Inject
    @DataField("artifact-id-form")
    private Div artifactIdForm;

    @Inject
    @DataField("artifact-id")
    private TextInput artifactId;

    @Inject
    @DataField("version-form")
    private Div versionForm;

    @Inject
    @DataField("version")
    private TextInput version;

    @Inject
    @DataField("artifact-selector-container")
    private Div artifactSelectorContainer;

    @Inject
    private TranslationService translationService;

    private ContainerConfigPresenter presenter;

    @Override
    public void init(final ContainerConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        containerName.setValue(EMPTY_STRING);
        groupId.setValue(EMPTY_STRING);
        artifactId.setValue(EMPTY_STRING);
        version.setValue(EMPTY_STRING);
        resetFormState();
    }

    @Override
    public String getContainerName() {
        return containerName.getValue();
    }

    @Override
    public void setContainerName(final String value) {
        containerName.setValue(value);
    }

    @Override
    public String getGroupId() {
        return groupId.getValue();
    }

    @Override
    public void setGroupId(final String value) {
        groupId.setValue(value);
    }

    @Override
    public String getArtifactId() {
        return artifactId.getValue();
    }

    @Override
    public void setArtifactId(final String value) {
        artifactId.setValue(value);
    }

    @Override
    public String getVersion() {
        return version.getValue();
    }

    @Override
    public void setVersion(final String value) {
        version.setValue(value);
    }

    @Override
    public void setArtifactSelectorPresenter(final org.jboss.errai.common.client.api.IsElement artifactSelector) {
        artifactSelectorContainer.appendChild(artifactSelector.getElement());
    }

    @Override
    public void setContainerNameStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(containerNameForm,
                      status);
    }

    @Override
    public void setGroupIdStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(groupIdForm,
                      status);
    }

    @Override
    public void setArtifactIdStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(artifactIdForm,
                      status);
    }

    @Override
    public void setVersionStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(versionForm,
                      status);
    }

    @Override
    public void setContainerNameHelpText(final String containerNameHelpText) {
        containerNameHelp.getStyle().removeProperty("display");
        containerNameHelp.setInnerHTML(containerNameHelpText);
    }

    @Override
    public void clearContainerNameHelpText() {
        containerNameHelp.getStyle().setProperty("display",
                                                 "none");
        containerNameHelp.setInnerHTML(EMPTY_STRING);
    }

    @Override
    public void showFormError(final String error) {
        formError.getStyle().removeProperty("display");
        formErrorMessage.setInnerHTML(error);
    }

    @Override
    public void clearFormError() {
        formError.getStyle().setProperty("display",
                                         "none");
        formErrorMessage.setInnerHTML(EMPTY_STRING);
    }

    private void resetFormState() {
        setContainerNameStatus(FormStatus.VALID);
        setGroupIdStatus(FormStatus.VALID);
        setArtifactIdStatus(FormStatus.VALID);
        setVersionStatus(FormStatus.VALID);
        clearFormError();
        clearContainerNameHelpText();
    }

    @EventHandler("container-name")
    private void onContainerNameChange(@ForEvent("change") final Event event) {
        presenter.onContainerNameChange();
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
