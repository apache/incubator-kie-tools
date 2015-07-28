/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.social.hp.client.homepage.header;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderView extends Composite
        implements HeaderPresenter.View {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, HeaderView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    ParameterizedCommand onSelectCommand;

    Command viewAllCommand;

    @UiField
    Select updatesList;

// widgets from UX (not yet implemented)
//    @UiField
//    SpanElement numberItensAction;
//
//    @UiField
//    Anchor viewAll;

    @Inject
    private PlaceManager placeManager;

    @Override
    public void setOnSelectCommand( ParameterizedCommand onSelectCommand ) {
        this.onSelectCommand = onSelectCommand;
    }

    @Override
    public void setViewAllCommand( Command viewAllCommand ) {
        this.viewAllCommand = viewAllCommand;
    }

    @Override
    public void setNumberOfItemsLabel( String numberOfItemsLabel ) {
        //  this.numberItensAction.setInnerText( numberOfItemsLabel );
    }

    @Override
    public void setUpdatesMenuList( final List<String> items ) {
        updatesList.clear();
        for ( String item : items ) {
            final Option option = new Option();
            option.setText( item );
            updatesList.add( option );
        }
        updatesList.refresh();
    }

    public HeaderView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler( "updatesList" )
    void onSelect( ChangeEvent e ) {
        onSelectCommand.execute( updatesList.getValue() );
    }

// widgets from UX (not yet implemented)
//    @UiHandler("viewAll")
//    void viewAll( ClickEvent e ) {
//        viewAllCommand.execute();
//    }
}