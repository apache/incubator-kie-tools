/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.common;

import org.drools.guvnor.client.resources.ShowcaseImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LazyStackPanelHeader extends AbstractLazyStackPanelHeader {

    interface LazyStackPanelHeaderBinder
        extends
        UiBinder<Widget, LazyStackPanelHeader> {
    }

    private static LazyStackPanelHeaderBinder uiBinder           = GWT.create( LazyStackPanelHeaderBinder.class );

    @UiField
    Image                                     icon;

    @UiField
    HorizontalPanel                           container;

    private ClickHandler                      expandClickHandler = new ClickHandler() {

                                                                     public void onClick(ClickEvent event) {
                                                                         onTitleClicked();
                                                                     }
                                                                 };

    public LazyStackPanelHeader(String headerText,
                                ImageResource headerIcon) {
        this();
        Image titleIcon = new Image( headerIcon );
        container.add( titleIcon );
        Label titleLabel = new Label( headerText );
        titleLabel.setStyleName( "guvnor-cursor" );
        titleLabel.addClickHandler( expandClickHandler );
        container.add( titleLabel );
    }

    public LazyStackPanelHeader(String headerText) {
        this();
        Label titleLabel = new Label( headerText );
        titleLabel.setStyleName( "guvnor-cursor" );
        titleLabel.addClickHandler( expandClickHandler );
        container.add( titleLabel );
    }

    public LazyStackPanelHeader() {

        add( uiBinder.createAndBindUi( this ) );

        icon.addClickHandler( expandClickHandler );

        setIconImage();

        addOpenHandler( new OpenHandler<AbstractLazyStackPanelHeader>() {
            public void onOpen(OpenEvent<AbstractLazyStackPanelHeader> event) {
                expanded = true;
                setIconImage();
            }
        } );

        addCloseHandler( new CloseHandler<AbstractLazyStackPanelHeader>() {
            public void onClose(CloseEvent<AbstractLazyStackPanelHeader> event) {
                expanded = false;
                setIconImage();
            }
        } );
    }

    public void expand() {
        if ( !expanded ) {
            onTitleClicked();
        }
    }

    public void collapse() {
        if ( expanded ) {
            onTitleClicked();
        }
    }

    private void setIconImage() {
        if ( expanded ) {
            icon.setResource( ShowcaseImages.INSTANCE.collapse() );
        } else {
            icon.setResource( ShowcaseImages.INSTANCE.expand() );
        }

    }

    private void onTitleClicked() {
        if ( expanded ) {
            CloseEvent.fire( this,
                             this );
        } else {
            OpenEvent.fire( this,
                            this );
        }
    }
}
