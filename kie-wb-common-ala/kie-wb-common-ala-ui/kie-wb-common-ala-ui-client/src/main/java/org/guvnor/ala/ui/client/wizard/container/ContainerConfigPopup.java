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

package org.guvnor.ala.ui.client.wizard.container;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.widget.popup.BaseOkCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ContainerConfigPopup
        extends BaseOkCancelPopup {

    private ParameterizedCommand<ContainerConfig> onSuccessCommand;

    private Command onCancelCommand;

    private ContainerConfigPresenter configPresenter;

    @Inject
    public ContainerConfigPopup(final View view,
                                final ContainerConfigPresenter configPresenter) {
        super(view);
        this.configPresenter = configPresenter;
    }

    @PostConstruct
    @Override
    public void init() {
        super.init();
        view.setContent(configPresenter.getView().getElement());
    }

    public void show(final String title,
                     final ParameterizedCommand<ContainerConfig> onSuccessCommand,
                     final Command onCancelCommand,
                     final List<String> alreadyInUseNames) {
        this.onSuccessCommand = onSuccessCommand;
        this.onCancelCommand = onCancelCommand;
        configPresenter.clear();
        configPresenter.setup(alreadyInUseNames);
        view.show(title);
    }

    @Override
    protected void onOK() {
        if (configPresenter.validateForSubmit()) {
            view.hide();
            onSuccessCommand.execute(configPresenter.getContainerConfig());
        }
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        onCancelCommand.execute();
    }
}