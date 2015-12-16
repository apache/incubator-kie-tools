/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dsltext.client.editor;

import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

/**
 * The view for the Domain Specific Language editor
 */
public class DSLEditorViewImpl
        extends KieEditorViewImpl
        implements DSLEditorView {

    private final AceEditor editor = new AceEditor();

    public DSLEditorViewImpl() {
        editor.startEditor();
        editor.setMode( AceEditorMode.TEXT );
        editor.setTheme( AceEditorTheme.CHROME );
        initWidget( editor );
    }

    @Override
    public void setReadOnly( final boolean readOnly ) {
        editor.setReadOnly( readOnly );
    }

    @Override
    public void setContent( final String input ) {
        final String content;
        if ( input == null ) {
            content = "";
        } else {
            content = input;
        }
        editor.setText( content );
        editor.setFocus();
    }

    @Override
    public String getContent() {
        return editor.getValue();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
//        int width = getParent().getOffsetWidth();
//        setPixelSize( width,
//                      height );
        editor.setHeight( height + "px" );
        editor.redisplay();
    }

}
