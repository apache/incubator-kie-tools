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

package org.guvnor.ala.ui.client.wizard.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public class ProviderConfigurationPagePresenter
        implements WizardPage {

    public interface View
            extends UberElement<ProviderConfigurationPagePresenter> {

        void setForm(final IsElement element);
    }

    private final View view;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    private ProviderConfigurationForm providerConfigurationForm;

    @Inject
    public ProviderConfigurationPagePresenter(final View view,
                                              final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        this.view = view;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setProviderConfigurationForm(final ProviderConfigurationForm providerConfigurationForm) {
        this.providerConfigurationForm = providerConfigurationForm;
        this.view.setForm(providerConfigurationForm.getView());
        providerConfigurationForm.addContentChangeHandler(this::onContentChanged);
    }

    @Override
    public void initialise() {
    }

    @Override
    public void prepareView() {

    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        providerConfigurationForm.isValid(callback);
    }

    @Override
    public String getTitle() {
        return providerConfigurationForm.getWizardTitle();
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void clear() {
        if (providerConfigurationForm != null) {
            providerConfigurationForm.clear();
        }
    }

    public ProviderConfiguration buildProviderConfiguration() {
        return providerConfigurationForm.buildProviderConfiguration();
    }

    protected void onContentChanged() {
        wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(ProviderConfigurationPagePresenter.this));
    }
}
