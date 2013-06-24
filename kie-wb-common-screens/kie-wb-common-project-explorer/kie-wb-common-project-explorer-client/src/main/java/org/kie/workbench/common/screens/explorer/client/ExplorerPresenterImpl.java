/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

/**
 * Repository, Package, Folder and File explorer
 */
@WorkbenchScreen(identifier = "org.kie.guvnor.explorer")
public class ExplorerPresenterImpl implements ExplorerPresenter {

    @Inject
    private ExplorerView view;

    @Inject
    private BusinessViewPresenter businessViewPresenter;

    @Inject
    private TechnicalViewPresenter technicalViewPresenter;

    @PostConstruct
    public void init() {
        selectBusinessView();
    }

    @WorkbenchPartView
    public UberView<ExplorerPresenterImpl> getView() {
        return this.view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectExplorerConstants.INSTANCE.explorerTitle();
    }

    @Override
    public void selectBusinessView() {
        businessViewPresenter.setVisible( true );
        technicalViewPresenter.setVisible( false );
    }

    @Override
    public void selectTechnicalView() {
        businessViewPresenter.setVisible( false );
        technicalViewPresenter.setVisible( true );
    }

}
