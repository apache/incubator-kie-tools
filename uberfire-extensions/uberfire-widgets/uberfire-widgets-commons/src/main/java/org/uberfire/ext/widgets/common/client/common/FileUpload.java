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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.uberfire.mvp.Command;

public class FileUpload
        extends Composite {

    private static final String FAKEPATH = "c:\\fakepath\\";
    private final Command command;

    interface FileUploadBinder extends UiBinder<Widget, FileUpload> {

    }

    @UiField
    InputGroupAddon uploadButton;

    @UiField
    InputGroupAddon chooseButton;

    @UiField
    Input file;

    @UiField
    Input fileText;

    private boolean isDisabled = false;

    private static FileUploadBinder uiBinder = GWT.create( FileUploadBinder.class );

    public FileUpload() {
        this( null, false );
    }

    public FileUpload( final Command command ) {
        this( command, true );
    }

    public FileUpload( final Command command,
                       boolean displayUploadButton ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.command = command;
        fileText.setReadOnly( true );

        file.addChangeHandler( getFileChangeHandler() );

        chooseButton.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                ( (InputElement) file.getElement().cast() ).click();
            }
        }, ClickEvent.getType() );

        if ( displayUploadButton ) {
            uploadButton.addDomHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    if ( isDisabled ) {
                        return;
                    }
                    if ( command != null ) {
                        command.execute();
                    }
                }
            }, ClickEvent.getType() );
        } else {
            uploadButton.removeFromParent();
            uploadButton = null;
        }
    }

    public void setName( final String name ) {
        file.setName( name );
    }

    public void upload() {
        command.execute();
    }

    public String getFilename() {
        return file.getValue();
    }

    public void setEnabled( boolean b ) {
        if ( uploadButton == null ) {
            return;
        }
        if ( !b ) {
            isDisabled = true;
            uploadButton.addStyleName( "disabled" );
        } else {
            isDisabled = false;
            uploadButton.removeStyleName( "disabled" );
        }
    }

    protected ChangeHandler getFileChangeHandler() {
        return new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                String fileName = file.getValue();
                if( fileName.toLowerCase().startsWith( FAKEPATH ) ){
                    fileName = fileName.substring( FAKEPATH.length() );
                }
                fileText.setValue( fileName );
            }
        };
    }

}
