/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.client.wizard.pages.sourcerepository;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.resources.i18n.ExamplesScreenConstants;
import org.kie.workbench.common.screens.examples.client.wizard.pages.BaseExamplesWizardPage;
import org.kie.workbench.common.screens.examples.model.ExampleRepository;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.util.URIUtil;

@Dependent
public class SourceRepositoryPage extends BaseExamplesWizardPage implements SourceRepositoryPageView.Presenter {

    private SourceRepositoryPageView view;

    private ExampleRepository stockRepository;

    public SourceRepositoryPage() {
        //Zero-argument constructor for CDI proxies
    }

    @Inject
    public SourceRepositoryPage(final SourceRepositoryPageView view,
                                final TranslationService translator,
                                final Caller<ExamplesService> examplesService,
                                final Event<WizardPageStatusChangeEvent> pageStatusChangedEvent) {
        super(translator,
              examplesService,
              pageStatusChangedEvent);
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setPlaceHolder(translator.format(ExamplesScreenConstants.SourceRepositoryPage_WizardRepositoriesPlaceHolder));
    }

    @Override
    public void initialise() {
        view.initialise();
    }

    @Override
    public String getTitle() {
        return translator.format(ExamplesScreenConstants.SourceRepositoryPage_WizardSelectRepositoryPageTitle);
    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        final ExampleRepository selectedRepository = model.getSelectedRepository();
        callback.callback(validateUrl(selectedRepository));
    }

    @Override
    public void setPlaygroundRepository(ExampleRepository stockRepository) {
        this.stockRepository = stockRepository;
        if (stockRepository == null) {
            view.showRepositoryUrlInputForm();
            view.setCustomRepositoryOption();
            view.disableStockRepositoryOption();
            model.setSelectedRepository(null);
        } else {
            view.hideRepositoryUrlInputForm();
            view.setStockRepositoryOption();
            model.setSelectedRepository(stockRepository);
        }
    }

    @Override
    public void playgroundRepositorySelected() {
        model.setSelectedRepository(stockRepository);
        view.hideRepositoryUrlInputForm();
        view.setCustomRepositoryValue(null);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    @Override
    public void onCustomRepositorySelected() {
        view.showRepositoryUrlInputForm();
        model.setSelectedRepository(null);
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    @Override
    public void onCustomRepositoryValueChanged() {
        model.setSelectedRepository(new ExampleRepository(view.getCustomRepositoryValue()));
        pageStatusChangedEvent.fire(new WizardPageStatusChangeEvent(this));
    }

    private boolean validateUrl(final ExampleRepository selectedRepository) {
        if (selectedRepository == null) {
            view.setUrlGroupType(ValidationState.ERROR);
            view.showUrlHelpMessage(translator.format(ExamplesScreenConstants.SourceRepositoryPage_WizardSelectRepositoryURLMandatory));
            return false;
        }
        final String url = selectedRepository.getUrl();
        if (url == null || url.trim().isEmpty()) {
            selectedRepository.setUrlValid(false);
            view.setUrlGroupType(ValidationState.ERROR);
            view.showUrlHelpMessage(translator.format(ExamplesScreenConstants.SourceRepositoryPage_WizardSelectRepositoryURLMandatory));
            return false;
        } else if (!isUrlValid(url)) {
            selectedRepository.setUrlValid(false);
            view.setUrlGroupType(ValidationState.ERROR);
            view.showUrlHelpMessage(translator.format(ExamplesScreenConstants.SourceRepositoryPage_WizardSelectRepositoryURLFormatInvalid));
            return false;
        } else {
            selectedRepository.setUrlValid(true);
            view.setUrlGroupType(ValidationState.NONE);
            view.hideUrlHelpMessage();
            return true;
        }
    }

    boolean isUrlValid(final String url) {
        return URIUtil.isValid(url);
    }
}
