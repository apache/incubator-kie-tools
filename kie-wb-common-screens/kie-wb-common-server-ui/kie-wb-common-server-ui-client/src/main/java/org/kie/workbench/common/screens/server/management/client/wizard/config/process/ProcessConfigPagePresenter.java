/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.wizard.config.process;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class ProcessConfigPagePresenter implements WizardPage {

    private final ProcessConfigPresenter processConfigPresenter;

    @Inject
    public ProcessConfigPagePresenter( final ProcessConfigPresenter processConfigPresenter ) {
        this.processConfigPresenter = processConfigPresenter;
    }

    @Override
    public String getTitle() {
        return processConfigPresenter.getView().getConfigPageTitle();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( true );
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return processConfigPresenter.getView().asWidget();
    }

    public ProcessConfig buildProcessConfig(){
        return processConfigPresenter.buildProcessConfig();
    }

    public void clear() {
        processConfigPresenter.clear();
    }
}
