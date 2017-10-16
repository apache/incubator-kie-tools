/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.client.handler.formModel.container;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationViewManager;
import org.kie.workbench.common.forms.editor.client.handler.formModel.SelectModelCreatorManagerCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class FormModelCreationContainer implements FormModelCreationContainerView.Presenter,
                                                   IsElement {

    private FormModelCreationContainerView view;

    private SelectModelCreatorManagerCallback callback;

    private FormModelCreationViewManager creationViewManager;

    @Inject
    public FormModelCreationContainer(FormModelCreationContainerView view) {
        this.view = view;
    }

    public void setup(FormModelCreationViewManager creationViewManager,
                      SelectModelCreatorManagerCallback callback) {
        PortablePreconditions.checkNotNull("creationViewManager",
                                           creationViewManager);
        PortablePreconditions.checkNotNull("SelectModelCreatorManagerCallback",
                                           callback);

        this.creationViewManager = creationViewManager;
        this.callback = callback;

        view.init(this);
    }

    public FormModelCreationViewManager getCreationViewManager() {
        return creationViewManager;
    }

    @Override
    public void selectManager() {
        view.select();
        callback.selectContainerCallback(this);
    }

    public void showCreationView() {
        view.showCreationView();
    }

    public void hideCreationView() {
        creationViewManager.reset();
        view.hideCreationView();
    }

    public boolean isValid() {
        return creationViewManager.isValid();
    }

    public UberElement getCreationView() {
        return creationViewManager.getView();
    }

    @Override
    public String getFormModelLabel() {
        return creationViewManager.getLabel();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void initData(Path projectPath) {
        creationViewManager.init(projectPath);
    }
}
