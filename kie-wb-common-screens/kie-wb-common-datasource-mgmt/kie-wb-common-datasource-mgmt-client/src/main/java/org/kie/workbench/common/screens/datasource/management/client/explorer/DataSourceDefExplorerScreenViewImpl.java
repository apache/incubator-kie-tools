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

package org.kie.workbench.common.screens.datasource.management.client.explorer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.datasource.management.client.explorer.project.ProjectDataSourceExplorer;
import org.kie.workbench.common.screens.datasource.management.client.explorer.global.GlobalDataSourceExplorer;

@Dependent
@Templated
public class DataSourceDefExplorerScreenViewImpl
        extends Composite
        implements DataSourceDefExplorerScreenView {


    @Inject
    @DataField
    private FlowPanel projectBrowserContainer;

    @Inject
    @DataField
    private FlowPanel globalBrowserContainer;

    @Inject
    @DataField
    private Anchor projectTab;

    @Inject
    @DataField
    private Anchor globalTab;

    private Presenter presenter;

    public DataSourceDefExplorerScreenViewImpl() {
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setProjectExplorer( ProjectDataSourceExplorer projectExplorer ) {
        projectBrowserContainer.add( projectExplorer );
    }

    @Override
    public void setGlobalExplorer( GlobalDataSourceExplorer globalExplorer ) {
        globalBrowserContainer.add( globalExplorer );
    }

    @EventHandler( "projectTab" )
    void onProjectTabClick( ClickEvent event ) {
        presenter.onProjectExplorerSelected();
    }

    @EventHandler( "globalTab" )
    public void onGlobalTabClick( ClickEvent event ) {
        presenter.onGlobalExplorerSelected();
    }

}
