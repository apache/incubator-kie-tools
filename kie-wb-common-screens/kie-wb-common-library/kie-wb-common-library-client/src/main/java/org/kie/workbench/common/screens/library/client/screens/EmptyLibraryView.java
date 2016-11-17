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
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.InfoPopup;

import javax.inject.Inject;
import javax.inject.Named;

@Templated
public class EmptyLibraryView implements EmptyLibraryScreen.View, IsElement {

    private EmptyLibraryScreen presenter;

    @Inject
    @DataField
    private Button newProject;

    @Inject
    @DataField
    private Button example;

    @Named( "h1" )
    @Inject
    @DataField
    private Heading welcome;

    @Inject
    @DataField
    private Anchor newProjectLink;

    @Inject
    TranslationService ts;

    @Override
    public void init( EmptyLibraryScreen presenter ) {
        this.presenter = presenter;
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "newProject" )
    public void newProject( Event e ) {
        presenter.newProject();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "newProjectLink" )
    public void newProjectLink( Event e ) {
        presenter.newProject();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "example" )
    public void example( Event e ) {
        presenter.importExample();
    }

    @Override
    public void setup( String username ) {
        welcome.setInnerHTML(
                ts.getTranslation( LibraryConstants.EmptyLibraryView_Welcome ) + " " + username + "." );
    }

    @Override
    public void openNoRightsPopup() {
        InfoPopup.generate( ts.getTranslation( LibraryConstants.Error_NoAccessRights ) );
    }
}