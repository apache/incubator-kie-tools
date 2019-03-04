/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.included;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPage;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.IncludedModelsPage_Title;

@Dependent
public class IncludedModelsPage extends DMNPage {

    private final IncludedModelsPagePresenter includedModelsPresenter;

    private final IncludedModelsPageState pageState;

    private final FlashMessages flashMessages;

    @Inject
    public IncludedModelsPage(final HTMLDivElement pageView,
                              final TranslationService translationService,
                              final FlashMessages flashMessages,
                              final IncludedModelsPagePresenter includedModelsPresenter,
                              final IncludedModelsPageState pageState) {
        super(IncludedModelsPage_Title, pageView, translationService);
        this.flashMessages = flashMessages;
        this.includedModelsPresenter = includedModelsPresenter;
        this.pageState = pageState;
    }

    @Override
    public void onFocus() {
        getPageView().innerHTML = "";
        getPageView().appendChild(getFlashMessages().getElement());
        getPageView().appendChild(getIncludedModelsPresenter().getElement());
    }

    @Override
    public void onLostFocus() {
        getFlashMessages().hideMessages();
    }

    public void setup(final IncludedModelsPageStateProvider stateProvider) {
        getPageState().init(stateProvider);
        getIncludedModelsPresenter().refresh();
    }

    private FlashMessages getFlashMessages() {
        return flashMessages;
    }

    private IncludedModelsPageState getPageState() {
        return pageState;
    }

    private IncludedModelsPagePresenter getIncludedModelsPresenter() {
        return includedModelsPresenter;
    }
}
