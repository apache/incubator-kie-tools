/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.FlowPanel;
import org.drools.workbench.client.resources.i18n.AppConstants;
import org.guvnor.m2repo.client.event.M2RepoRefreshEvent;
import org.guvnor.m2repo.client.event.M2RepoSearchEvent;
import org.guvnor.m2repo.client.upload.UploadFormPresenter;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.search.ContextualSearch;
import org.kie.workbench.common.widgets.client.search.SearchBehavior;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPanel;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.Command;
import org.uberfire.security.annotations.Roles;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Perspective to show M2_REPO related screen
 */
@Roles( { "admin" } )
@ApplicationScoped
@WorkbenchPerspective( identifier = M2RepoPerspective.PERSPECTIVE_ID )
public class M2RepoPerspective extends FlowPanel {

    public static final String PERSPECTIVE_ID = "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective";

    @Inject
    private ContextualSearch contextualSearch;

    @Inject
    private Event<M2RepoSearchEvent> searchEvents;

    @Inject
    private Event<M2RepoRefreshEvent> refreshEvents;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    @WorkbenchPanel( parts = "M2RepoEditor" )
    FlowPanel m2RepoEditor;

    @PostConstruct
    private void init() {
        Layouts.setToFillParent( m2RepoEditor );
        add( m2RepoEditor );
        contextualSearch.setPerspectiveSearchBehavior( PERSPECTIVE_ID, new SearchBehavior() {
            @Override
            public void execute( String searchFilter ) {
                searchEvents.fire( new M2RepoSearchEvent( searchFilter ) );
            }

        } );
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory.newTopLevelMenu( AppConstants.INSTANCE.Upload() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        UploadFormPresenter uploadFormPresenter = iocManager.lookupBean( UploadFormPresenter.class ).getInstance();
                        uploadFormPresenter.showView();
                    }
                } )
                .endMenu()
                .newTopLevelMenu( AppConstants.INSTANCE.Refresh() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        refreshEvents.fire( new M2RepoRefreshEvent() );
                    }
                } )
                .endMenu()
                .build();
    }

}