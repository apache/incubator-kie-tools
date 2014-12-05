/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class MetadataWidget
        extends Composite
        implements HasBusyIndicator {

    interface Binder
            extends
            UiBinder<Widget, MetadataWidget> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private Metadata metadata = null;
    private boolean readOnly;

    //    @UiField Label title;
    @UiField
    CategorySelectorWidget categories;
    @UiField
    Label note;
    @UiField
    Label uri;
    @UiField
    TextBox subject;
    @UiField
    TextBox type;
    @UiField
    TextBox external;
    @UiField
    TextBox source;

    private BusyIndicatorView busyIndicatorView;

    public MetadataWidget( BusyIndicatorView busyIndicatorView ) {
        this.busyIndicatorView = busyIndicatorView;
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void setContent( final Metadata metadata,
                            final boolean readOnly ) {
        this.metadata = checkNotNull( "metadata", metadata );
        this.readOnly = readOnly;

        loadData();
    }

    private void loadData() {

        categories.setContent( metadata, this.readOnly );

        note.setText( metadata.getCheckinComment() );

        uri.setText( metadata.getPath().toURI() );

        subject.setText( metadata.getSubject() );
        subject.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                metadata.setSubject( subject.getText() );
            }
        } );

        type.setText( metadata.getType() );
        type.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                metadata.setType( type.getText() );
            }
        } );

        external.setText( metadata.getExternalRelation() );
        external.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                metadata.setExternalRelation( external.getText() );
            }
        } );

        source.setText( metadata.getExternalSource() );
        source.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                metadata.setExternalSource( source.getText() );
            }
        } );

    }

    @Deprecated
    public Metadata getContent() {
        return metadata;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        busyIndicatorView.showBusyIndicator( message );
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    public void setNote( String text ) {
        note.setText( text );
    }
}

