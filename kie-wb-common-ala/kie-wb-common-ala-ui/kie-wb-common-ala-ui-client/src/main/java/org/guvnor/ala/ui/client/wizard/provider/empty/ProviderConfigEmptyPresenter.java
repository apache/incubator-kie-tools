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

import org.guvnor.ala.ui.client.handler.ProviderConfigurationForm;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

/**
 * Empty provider config presenter that does nothing. This presenter is rarely used, and is only showed in cases
 * were the UI module for given provider type has missing components.
 */
@Dependent
public class ProviderConfigEmptyPresenter
        implements ProviderConfigurationForm {

    public interface View
            extends UberElement<ProviderConfigEmptyPresenter> {

        String getWizardTitle();
    }

    private final View view;

    @Inject
    public ProviderConfigEmptyPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    @Override
    public void addContentChangeHandler(final ContentChangeHandler contentChangeHandler) {
        //this empty presenter does nothing.
    }

    @Override
    public ProviderConfiguration buildProviderConfiguration() {
        //this empty presenter does nothing.
        return null;
    }

    @Override
    public void clear() {
        //this empty presenter does nothing.
    }

    public void isValid(final Callback<Boolean> callback) {
        //this empty presenter does nothing.
        callback.callback(false);
    }

    @Override
    public String getWizardTitle() {
        return view.getWizardTitle();
    }

    @Override
    public void disable() {
        //this empty presenter does nothing.
    }

    @Override
    public void load(final Provider provider) {
        //this empty presenter does nothing.
    }
}
