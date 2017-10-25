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

package org.guvnor.ala.ui.client.wizard.provider.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ProviderConfigEmptyView_ProviderTypeNotProperlyConfiguredMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ProviderConfigEmptyView_Title;

@Dependent
@Templated
public class ProviderConfigEmptyView
        implements IsElement,
                   ProviderConfigEmptyPresenter.View {

    private ProviderConfigEmptyPresenter presenter;

    @Inject
    @DataField("container")
    private Div container;

    @DataField("message-heading")
    private HTMLElement messageHeading = Window.getDocument().createElement("h1");

    @Inject
    private TranslationService translationService;

    @PostConstruct
    private void init() {
        messageHeading.setTextContent(translationService.getTranslation(ProviderConfigEmptyView_ProviderTypeNotProperlyConfiguredMessage));
    }

    @Override
    public void init(final ProviderConfigEmptyPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getWizardTitle() {
        return translationService.getTranslation(ProviderConfigEmptyView_Title);
    }
}