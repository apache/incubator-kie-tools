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

package org.guvnor.ala.ui.client.wizard.providertype;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.EnableProviderTypePageView_Title;
import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;

@Dependent
@Templated
public class EnableProviderTypePageView
        implements IsElement,
                   EnableProviderTypePagePresenter.View {

    @Inject
    @DataField
    private Div container;

    private EnableProviderTypePagePresenter presenter;

    @Inject
    private TranslationService translationService;

    @Override
    public void init(final EnableProviderTypePagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        removeAllChildren(container);
    }

    @Override
    public void addProviderType(final org.jboss.errai.common.client.api.IsElement element) {
        container.appendChild(element.getElement());
    }

    @Override
    public String getTitle() {
        return translationService.getTranslation(EnableProviderTypePageView_Title);
    }
}