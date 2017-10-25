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

package org.guvnor.ala.ui.openshift.client.pipeline.template;

import javax.annotation.PostConstruct;
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
import static org.guvnor.ala.ui.openshift.client.resources.i18n.GuvnorAlaOpenShiftUIConstants.TemplateParamsFormView_Title;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class TemplateParamsFormView
        implements IsElement,
                   TemplateParamsFormPresenter.View {

    @Inject
    @DataField("runtime-form")
    private Div runtimeForm;

    @Inject
    @DataField("runtime-name")
    private TextInput runtimeName;

    @Inject
    @DataField("runtime-name-help-block")
    private Span runtimeNameHelp;

    @Inject
    @DataField("template-url-form")
    private Div templateURLForm;

    @Inject
    @DataField("template-url")
    private TextInput templateURL;

    @Inject
    @DataField("image-streams-url-form")
    private Div imageStreamsURLForm;

    @Inject
    @DataField("image-streams-url")
    private TextInput imageStreamsURL;

    @Inject
    @DataField("secrets-file-url-form")
    private Div secretsFileURLForm;

    @Inject
    @DataField("secrets-file-url")
    private TextInput secretsFileURL;

    @Inject
    @DataField("template-params-editor-container")
    private Div paramsEditorPresenterContainer;

    @Inject
    @DataField("required-params-help-form")
    private Div requitedParamsHelpForm;

    @Inject
    @DataField("required-params-help")
    private Span requiredParamsHelp;

    @Inject
    private TranslationService translationService;

    private TemplateParamsFormPresenter presenter;

    @Override
    public void init(final TemplateParamsFormPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    private void init() {
    }

    @Override
    public void setTemplateURL(final String templateURL) {
        this.templateURL.setValue(templateURL);
    }

    @Override
    public String getTemplateURL() {
        return templateURL.getValue();
    }

    @Override
    public void setImageStreamsURL(final String imageStreamsURL) {
        this.imageStreamsURL.setValue(imageStreamsURL);
    }

    @Override
    public String getImageStreamsURL() {
        return imageStreamsURL.getValue();
    }

    @Override
    public void setSecretsFileURL(final String secretsFileURL) {
        this.secretsFileURL.setValue(secretsFileURL);
    }

    @Override
    public String getSecretsFileURL() {
        return secretsFileURL.getValue();
    }

    @Override
    public void setRuntimeName(final String runtimeName) {
        this.runtimeName.setValue(runtimeName);
    }

    @Override
    public String getRuntimeName() {
        return runtimeName.getValue();
    }

    @Override
    public void setRuntimeNameStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(runtimeForm,
                      status);
    }

    @Override
    public void setTemplateURLStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(templateURLForm,
                      status);
    }

    @Override
    public void setImageStreamsURLStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(imageStreamsURLForm,
                      status);
    }

    @Override
    public void setSecretsFileURLStatus(final FormStatus status) {
        checkNotNull("status",
                     status);
        setFormStatus(secretsFileURLForm,
                      status);
    }

    @Override
    public void setRequiredParamsHelpText(final String requiredParamsHelpText) {
        requitedParamsHelpForm.getStyle().removeProperty("display");
        requiredParamsHelp.setInnerHTML(requiredParamsHelpText);
    }

    @Override
    public void clearRequiredParamsHelpText() {
        requitedParamsHelpForm.getStyle().setProperty("display",
                                                      "none");
        requiredParamsHelp.setInnerHTML(EMPTY_STRING);
    }

    @Override
    public void setRuntimeNameHelpText(final String runtimeNameHelpText) {
        runtimeNameHelp.getStyle().removeProperty("display");
        runtimeNameHelp.setInnerHTML(runtimeNameHelpText);
    }

    @Override
    public void clearRuntimeNameHelpText() {
        runtimeNameHelp.getStyle().setProperty("display",
                                               "none");
        runtimeNameHelp.setInnerHTML(EMPTY_STRING);
    }

    @Override
    public void clear() {
        setRuntimeName(EMPTY_STRING);
        setTemplateURL(EMPTY_STRING);
        setImageStreamsURL(EMPTY_STRING);
        setSecretsFileURL(EMPTY_STRING);
        clearRequiredParamsHelpText();
        clearRuntimeNameHelpText();
        resetFormState();
    }

    @Override
    public String getWizardTitle() {
        return translationService.getTranslation(TemplateParamsFormView_Title);
    }

    @Override
    public void setParamsEditorPresenter(org.jboss.errai.common.client.api.IsElement paramsEditorPresenter) {
        paramsEditorPresenterContainer.appendChild(paramsEditorPresenter.getElement());
    }

    private void resetFormState() {
        setRuntimeNameStatus(FormStatus.VALID);
        setTemplateURLStatus(FormStatus.VALID);
        setImageStreamsURLStatus(FormStatus.VALID);
        setSecretsFileURLStatus(FormStatus.VALID);
    }

    @EventHandler("runtime-name")
    private void onRuntimeNameChange(@ForEvent("change") final Event event) {
        presenter.onRuntimeNameChange();
    }

    @EventHandler("template-url")
    private void onTemplateURLChange(@ForEvent("change") final Event event) {
        presenter.onTemplateURLChange();
    }

    @EventHandler("image-streams-url")
    private void onImageStreamsURLChange(@ForEvent("change") final Event event) {
        presenter.onImageStreamsURLChange();
    }

    @EventHandler("secrets-file-url")
    private void onSecretsFileURLChange(@ForEvent("change") final Event event) {
        presenter.onSecretsFileURLChange();
    }
}