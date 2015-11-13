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

package org.uberfire.ext.widgets.sandbox.client.markdown.editorlive;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;
import org.uberfire.ext.widgets.common.client.common.ResizableTextArea;
import org.uberfire.ext.widgets.sandbox.client.markdown.MarkdownTextContent;

/**
 * A Text editor
 */
public class MarkdownLiveEditorView extends Composite
        implements
        RequiresResize,
        MarkdownLiveEditorPresenter.View {

    interface MarkdownLiveEditorViewBinder
            extends
            UiBinder<ResizeLayoutPanel, MarkdownLiveEditorView> {

    }

    private static MarkdownLiveEditorViewBinder uiBinder = GWT.create( MarkdownLiveEditorViewBinder.class );

    @UiField
    protected ResizableTextArea fileContent;

    @Inject
    protected Event<MarkdownTextContent> event;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        fileContent.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( final KeyDownEvent changeEvent ) {
                event.fire( new MarkdownTextContent( fileContent.getText() ) );
            }
        } );

        fileContent.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( final KeyUpEvent changeEvent ) {
                event.fire( new MarkdownTextContent( fileContent.getText() ) );
            }
        } );

        fileContent.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent changeEvent ) {
                event.fire( new MarkdownTextContent( fileContent.getText() ) );
            }
        } );
    }

    public void setContent( final String content ) {
        fileContent.setText( content );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
        fileContent.onResize();
    }

}