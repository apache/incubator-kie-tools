/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.asset.management.client.editors.repository.structure;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataPresenter;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.editors.repository.structure.configure.ConfigureScreenPopupViewImpl;
import org.guvnor.common.services.project.model.GAV;
import org.gwtbootstrap3.client.ui.Row;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
public class RepositoryStructureViewImpl
        extends Composite
        implements RepositoryStructureView {

    private RepositoryStructurePresenter presenter;

    interface RepositoryStructureViewImplBinder
            extends
            UiBinder<Widget, RepositoryStructureViewImpl> {

    }

    private static RepositoryStructureViewImplBinder uiBinder = GWT.create(RepositoryStructureViewImplBinder.class);

    @UiField
    Row dataViewContainer;

    @UiField(provided = true)
    RepositoryStructureDataPresenter dataPresenter;

    @UiField
    Row modulesViewContainer;

    @UiField(provided = true)
    ProjectModulesView modulesView;

    @Inject
    ConfigureScreenPopupViewImpl configureScreenPopupView;

    @Inject
    public RepositoryStructureViewImpl(final RepositoryStructureDataPresenter dataPresenter,
                                       final ProjectModulesView modulesView) {
        this.dataPresenter = dataPresenter;
        this.modulesView = modulesView;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(final RepositoryStructurePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ConfigureScreenPopupViewImpl getConfigureScreenPopupView() {
        return configureScreenPopupView;
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public void clearDataView() {
        dataPresenter.clear();
    }

    @Override
    public ProjectModulesView getModulesView() {
        return modulesView;
    }

    @Override
    public void setModulesViewVisible(final boolean visible) {
        modulesViewContainer.setVisible(visible);
    }

    @Override
    public void setDataPresenterModel(final GAV gav) {
        dataPresenter.setGav(gav);
    }

    @Override
    public GAV getDataPresenterGav() {
        return dataPresenter.getGav();
    }

    @Override
    public void clear() {
        dataPresenter.clear();
    }

    @Override
    public void setDataPresenterMode(final RepositoryStructureDataView.ViewMode mode) {
        dataPresenter.setMode(mode);
    }
}
