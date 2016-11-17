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
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import javax.inject.Inject;
import javax.inject.Named;

@Templated
public class NewProjectView implements NewProjectScreen.View, IsElement {

    private NewProjectScreen presenter;


    @Named( "h1" )
    @Inject
    @DataField
    private Heading back;

    @Inject
    @DataField
    private Input projectName;

    @Inject
    @DataField
    Select ouDropdown;

    @Inject
    @DataField
    private Button cancel;

    @Inject
    @DataField
    private Button create;

    @Inject
    @DataField
    private Label ouLabel;

    @Inject
    private Document document;

    @Override
    public void init( NewProjectScreen presenter ) {
        this.presenter = presenter;
        back.setOnmouseover( f -> back.getStyle().setProperty( "cursor", "pointer" ) );
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "back" )
    public void back( Event e ) {
        presenter.back();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "cancel" )
    public void cancel( Event e ) {
        presenter.back();
    }

    @SinkNative( Event.ONCLICK )
    @EventHandler( "create" )
    public void createProject( Event e ) {
        presenter.createProject( projectName.getValue() );
    }

    @Override
    public void addOrganizationUnit( String ou ) {
        ouDropdown.add( createOption( ou ) );
    }

    @Override
    public void clearOrganizationUnits() {
        DOMUtil.removeAllChildren( ouDropdown );
    }

    @Override
    public void setOrganizationUnitSelected( String identifier ) {
        ouDropdown.setValue( identifier );
    }

    @Override
    public void setOUAlias( String ouAlias ) {
        ouLabel.setTextContent( ouAlias );
    }

    private Option createOption( String ou ) {
        Option option = ( Option ) document.createElement( "option" );
        option.setText( ou );
        return option;
    }
}