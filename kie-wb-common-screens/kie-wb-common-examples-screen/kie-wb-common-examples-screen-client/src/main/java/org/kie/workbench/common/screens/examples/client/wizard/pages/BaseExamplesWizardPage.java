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

package org.kie.workbench.common.screens.examples.client.wizard.pages;

import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.client.wizard.model.ExamplesWizardModel;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

public abstract class BaseExamplesWizardPage implements ExamplesWizardPage {

    protected TranslationService translator;
    protected Caller<ExamplesService> examplesService;
    protected Event<WizardPageStatusChangeEvent> pageStatusChangedEvent;

    protected ExamplesWizardModel model;

    protected BaseExamplesWizardPage() {
        //Zero-argument constructor for CDI proxies
    }

    public BaseExamplesWizardPage( final TranslationService translator,
                                   final Caller<ExamplesService> examplesService,
                                   final Event<WizardPageStatusChangeEvent> pageStatusChangedEvent ) {
        this.translator = translator;
        this.examplesService = examplesService;
        this.pageStatusChangedEvent = pageStatusChangedEvent;
    }

    @Override
    public void setModel( final ExamplesWizardModel model ) {
        this.model = model;
    }

    @Override
    public void destroy() {
        //Do nothing by default
    }
}
