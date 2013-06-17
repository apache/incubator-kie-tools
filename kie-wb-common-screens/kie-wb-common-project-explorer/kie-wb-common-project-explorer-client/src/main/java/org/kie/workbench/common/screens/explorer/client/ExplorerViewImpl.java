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

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.business.BusinessViewWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.technical.TechnicalViewWidget;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;

/**
 * The Explorer's view implementation
 */
public class ExplorerViewImpl extends Composite implements ExplorerView {

    interface ExplorerViewImplBinder
            extends
            UiBinder<Widget, ExplorerViewImpl> {

    }

    private static ExplorerViewImplBinder uiBinder = GWT.create( ExplorerViewImplBinder.class );

    @Inject
    private BusinessViewWidget businessView;

    @Inject
    private TechnicalViewWidget technicalView;

    @UiField
    Button btnBusinessView;

    @UiField
    Button btnTechnicalView;

    private ExplorerPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void init( final BusinessViewPresenter presenter ) {
        businessView.init( presenter );
    }

    @Override
    public void init( final TechnicalViewPresenter presenter ) {
        technicalView.init( presenter );
    }

    @UiFactory
    //Use injected BusinessViewWidget instance in UiBinder
    public BusinessViewWidget getBusinessView() {
        return businessView;
    }

    @UiFactory
    //Use injected TechnicalViewWidget instance in UiBinder
    public TechnicalViewWidget getTechnicalView() {
        return technicalView;
    }

    @Override
    public void setGroups( final Collection<Group> groups,
                           final Group activeGroup ) {
        businessView.setGroups( groups,
                                activeGroup );
    }

    @Override
    public void setRepositories( final Collection<Repository> repositories,
                                 final Repository activeRepository ) {
        businessView.setRepositories( repositories,
                                      activeRepository );
    }

    @Override
    public void setProjects( final Collection<Project> projects,
                             final Project activeProject ) {
        businessView.setProjects( projects,
                                  activeProject );
    }

    @Override
    public void setPackages( final Collection<Package> packages,
                             final Package activePackage ) {
        businessView.setPackages( packages,
                                  activePackage );
    }

    @Override
    public void setItems( final Collection<Item> items ) {
        businessView.setItems( items );
    }

    @Override
    public void addRepository( final Repository newRepository ) {
        businessView.addRepository( newRepository );
    }

    @Override
    public void addProject( final Project newProject ) {
        businessView.addProject( newProject );
    }

    @Override
    public void addPackage( final Package newPackage ) {
        businessView.addPackage( newPackage );
    }

    @UiHandler("btnBusinessView")
    public void onClickBusinessViewButton( final ClickEvent event ) {
        businessView.setVisible( true );
        technicalView.setVisible( false );
    }

    @UiHandler("btnTechnicalView")
    public void onClickTechnicalViewButton( final ClickEvent event ) {
        businessView.setVisible( false );
        technicalView.setVisible( true );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
