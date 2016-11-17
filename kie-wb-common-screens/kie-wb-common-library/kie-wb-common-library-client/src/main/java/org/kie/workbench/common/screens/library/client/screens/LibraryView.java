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
package org.kie.workbench.common.screens.library.client.screens;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.*;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.InfoPopup;
import org.kie.workbench.common.screens.library.client.widgets.ProjectItemWidget;
import org.uberfire.mvp.Command;

import javax.inject.Inject;

@Templated
public class LibraryView implements LibraryScreen.View, IsElement {

    private LibraryScreen presenter;

    @DataField
    @Inject
    Div projectList;

    @DataField
    @Inject
    Button newProjectButton;

    @DataField
    @Inject
    Button importExample;

    @DataField
    @Inject
    Input filterText;

    @Inject
    Document document;

    @Inject
    ManagedInstance<ProjectItemWidget> itemWidgetsInstances;

    @Inject
    TranslationService ts;

    @Override
    public void init( LibraryScreen presenter ) {
        this.presenter = presenter;
        filterText.setAttribute( "placeholder", ts.getTranslation( LibraryConstants.LibraryView_Filter ) );
    }

    @Override
    public void clearProjects() {
        DOMUtil.removeAllChildren( projectList );
    }

    @Override
    public void addProject( String project, Command details, Command select ) {
        ProjectItemWidget projectItemWidget = itemWidgetsInstances.get();
        projectItemWidget.init( project, details, select );
        projectList.appendChild( projectItemWidget.getElement() );
    }

    @Override
    public void clearFilterText() {
        this.filterText.setValue( "" );
    }

    @Override
    public void noRightsPopup() {
        InfoPopup.generate( ts.getTranslation( LibraryConstants.Error_NoAccessRights ) );
    }


    @SinkNative( Event.ONCLICK )
    @EventHandler( "newProjectButton" )
    public void newProject( Event e ) {

        presenter.newProject();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "importExample" )
    public void importExample( Event e ) {
        presenter.importExample();
    }

    @SinkNative( Event.ONKEYUP )
    @EventHandler( "filterText" )
    public void filterTextChange( Event e ) {
        presenter.updateProjectsBy( filterText.getValue() );
    }


    private Option createOption( String ou ) {
        Option option = ( Option ) document.createElement( "option" );
        option.setText( ou );
        return option;
    }
}