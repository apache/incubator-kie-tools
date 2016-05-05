/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.editor.commons.client.history;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class VersionMenuDropDownButtonViewImpl
        extends Composite
        implements VersionMenuDropDownButtonView {

    interface Binder
            extends
            UiBinder<Widget, VersionMenuDropDownButtonViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    Button button;

    @UiField
    DropDownMenu menuItems;

    private Presenter presenter;

    public VersionMenuDropDownButtonViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        menuItems.clear();
    }

    @Override
    public void addViewAllLabel( final int index,
                                 final Command command ) {
        menuItems.add( new ViewAllLabel( index, command ) );
    }

    @Override
    public void setTextToVersion( int versionIndex ) {
        button.setText( CommonConstants.INSTANCE.Version( versionIndex ) );
    }

    @Override
    public void setTextToLatest() {
        button.setText( CommonConstants.INSTANCE.LatestVersion() );
    }

    @Override
    public void addLabel( VersionRecord versionRecord,
                          boolean isSelected,
                          int versionIndex ) {
        VersionMenuItemLabel widget = new VersionMenuItemLabel(
                versionRecord,
                versionIndex,
                isSelected,
                new Callback<VersionRecord>() {
                    @Override
                    public void callback( VersionRecord result ) {
                        presenter.onVersionRecordSelected( result );
                    }
                } );
        widget.setWidth( "400px" );
        menuItems.add( widget );
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        button.setEnabled( enabled );
    }

    @UiHandler("button")
    public void handleClick( ClickEvent event ) {
        presenter.onMenuOpening();
    }
}
