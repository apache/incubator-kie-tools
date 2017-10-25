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

package org.guvnor.ala.ui.client.handler;

import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.client.wizard.provider.ProviderConfigurationPagePresenter;
import org.guvnor.ala.ui.model.Provider;
import org.guvnor.ala.ui.model.ProviderConfiguration;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.callbacks.Callback;

/**
 * Generic interface that provider specific presenters must implement for being used from the NewProviderWizard or other
 * places where the provider configuration parameters must be shown.
 * @see ProviderConfigurationPagePresenter
 */
public interface ProviderConfigurationForm {

    IsElement getView();

    void addContentChangeHandler(final ContentChangeHandler contentChangeHandler);

    ProviderConfiguration buildProviderConfiguration();

    void clear();

    void isValid(final Callback<Boolean> callback);

    String getWizardTitle();

    void disable();

    void load(Provider provider);
}
