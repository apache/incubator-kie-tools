/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.source;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

public class DrlEditor
        extends Composite
        implements RequiresResize {

    private final AceEditor editor = new AceEditor();

    public DrlEditor() {
        editor.startEditor();
        editor.setModeByName( "drools" );
        editor.setTheme( AceEditorTheme.CHROME );
        initWidget( editor );
    }

    public void setReadOnly( boolean readOnly ) {
        editor.setReadOnly( readOnly );
    }

    public void setText( final String input ) {
        final String content;
        if ( input == null ) {
            content = "";
        } else {
            content = input;
        }
        editor.setText( content );
        editor.setFocus();
    }

    public void insertAtCursor( String ins ) {
        editor.insertAtCursor( ins );
    }

    public String getText() {
        return editor.getValue();
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width,
                      height );
        editor.setHeight( height + "px" );
        editor.redisplay();
    }

}

