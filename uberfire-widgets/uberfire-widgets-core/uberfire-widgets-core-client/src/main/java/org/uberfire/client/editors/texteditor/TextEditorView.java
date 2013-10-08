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

package org.uberfire.client.editors.texteditor;

import javax.annotation.PostConstruct;

import org.uberfire.client.common.ResizableTextArea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeLayoutPanel;

/**
 * A Text editor
 */
public class TextEditorView extends Composite
    implements
    RequiresResize,
    TextEditorPresenter.View {

    interface TextEditorViewBinder
        extends
        UiBinder<ResizeLayoutPanel, TextEditorView> {
    }

    private static TextEditorViewBinder uiBinder = GWT.create( TextEditorViewBinder.class );

    @UiField
    public ResizableTextArea            fileContent;

    private boolean                     isDirty  = false;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        fileContent.addChangeHandler( new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                isDirty = true;
            }
        } );

    }

    public void setContent(final String content) {
        fileContent.setText( content );
    }

    public String getContent() {
        return fileContent.getText();
    }

    @Override
    public void setFocus() {
        fileContent.setFocus( true );
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void makeReadOnly() {
        fileContent.setEnabled(false);
    }

    @Override
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width,
                      height );
        fileContent.onResize();
    }

}