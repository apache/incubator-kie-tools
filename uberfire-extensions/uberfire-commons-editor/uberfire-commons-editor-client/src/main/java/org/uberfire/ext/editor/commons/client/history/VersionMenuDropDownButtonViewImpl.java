/*
 * Copyright 2014 JBoss Inc
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

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.Command;

public class VersionMenuDropDownButtonViewImpl
        extends DropdownButton
        implements VersionMenuDropDownButtonView {

    private Presenter presenter;

    public VersionMenuDropDownButtonViewImpl() {
        super( CommonConstants.INSTANCE.LatestVersion() );
        setRightDropdown( true );
        getTriggerWidget().addStyleName( "btn-mini" );

        addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onMenuOpening();
            }
        } );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void addViewAllLabel( int index,
                                 Command command ) {
        add(
                new ViewAllLabel(
                        index,
                        command ) );
    }

    @Override
    public void setTextToVersion( int versionIndex ) {
        setText( CommonConstants.INSTANCE.Version( versionIndex ) );
    }

    @Override
    public void setTextToLatest() {
        setText( CommonConstants.INSTANCE.LatestVersion() );
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
        add( widget );
    }
}
