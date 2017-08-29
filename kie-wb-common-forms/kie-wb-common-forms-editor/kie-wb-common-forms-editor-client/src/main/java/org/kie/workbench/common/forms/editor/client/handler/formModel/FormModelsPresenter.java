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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.editor.client.handler.formModel.container.FormModelCreationContainer;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

@Dependent
public class FormModelsPresenter implements IsWidget {

    protected FormModelsView view;

    protected List<FormModelCreationContainer> containers;

    protected FormModelCreationContainer currentManager;

    protected ManagedInstance<FormModelCreationContainer> containerInstance;

    protected ManagedInstance<FormModelCreationViewManager> modelManagerInstance;

    @Inject
    public FormModelsPresenter(FormModelsView view,
                               ManagedInstance<FormModelCreationContainer> containerInstance,
                               ManagedInstance<FormModelCreationViewManager> modelManagerInstance) {
        this.view = view;
        this.containerInstance = containerInstance;
        this.modelManagerInstance = modelManagerInstance;
    }

    @PostConstruct
    protected void init() {
        containers = getRegisteredCreationManagers();

        containers.sort(Comparator.comparingInt(o -> o.getCreationViewManager().getPriority()));

        view.setCreationViews(containers);
    }

    protected List<FormModelCreationContainer> getRegisteredCreationManagers() {
        List<FormModelCreationContainer> registeredContainers = new ArrayList<>();

        modelManagerInstance.forEach(modelManager -> {
            FormModelCreationContainer container = containerInstance.get();

            container.setup(modelManager,
                            this::selectContainer);

            registeredContainers.add(container);
        });

        return registeredContainers;
    }

    public void initialize(Path projectPath) {
        view.reset();

        currentManager = containers.get(0);
        currentManager.selectManager();

        containers.forEach(container -> {
            container.initData(projectPath);
        });
    }

    public boolean isValid() {
        return currentManager.isValid();
    }

    public FormModel getFormModel() {
        return currentManager.getCreationViewManager().getFormModel();
    }

    public void selectContainer(FormModelCreationContainer container) {
        PortablePreconditions.checkNotNull("container",
                                           container);

        if (currentManager != null) {
            currentManager.hideCreationView();
        }
        currentManager = container;
        currentManager.showCreationView();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
