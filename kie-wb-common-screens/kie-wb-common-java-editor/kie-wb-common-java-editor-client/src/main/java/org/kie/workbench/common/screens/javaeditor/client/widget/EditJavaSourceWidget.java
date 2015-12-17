/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.javaeditor.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorCallback;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

public class EditJavaSourceWidget
        extends Composite
        implements RequiresResize {


    private final AceEditor editor = new AceEditor( );

    private final List<TextChangeHandler> handlers = new ArrayList<TextChangeHandler>();

    private boolean disableHandlers = false;

    public interface TextChangeHandler {
        void onTextChange();
    }

    public EditJavaSourceWidget() {
        editor.startEditor();
        editor.setMode( AceEditorMode.JAVA );
        editor.setTheme( AceEditorTheme.CHROME );
        editor.setReadOnly( true );
        editor.addOnChangeHandler( new AceEditorCallback() {
            @Override
            public void invokeAceCallback( JavaScriptObject obj ) {
                onAceEditorChange();
            }
        } );
        initWidget( editor );
    }

    public void setContent( final String input ) {
        //the AceEditor raises the change event when the text is set programmatically
        disableHandlers = true;
        editor.setText( input != null ? input : "" );
        disableHandlers = false;
    }

    public String getContent() {
        return editor.getValue();
    }

    public void clear() {
        setContent( null );
    }

    public void setReadonly( boolean readonly ) {
        editor.setReadOnly( readonly );
    }

    public void addChangeHandler( TextChangeHandler changeHandler ) {
        if ( !handlers.contains( changeHandler ) ) {
            handlers.add( changeHandler );
        }
    }

    public void removeChangeHandler( TextChangeHandler changeHandler ) {
        handlers.remove( changeHandler );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();

        setPixelSize( ensureValidMeasure( width ), ensureValidMeasure( height ) );

        editor.setHeight( ensureValidMeasure( height ) + "px" );
        editor.redisplay();
    }

    public void refresh() {
        editor.redisplay();
    }

    private void onAceEditorChange() {
        if ( !disableHandlers ) {
            for ( TextChangeHandler handler : handlers ) {
                handler.onTextChange();
            }
        }
    }

    private int ensureValidMeasure( int value ) {
        if ( value < 0 ) {
            return 0;
        } else {
            return value;
        }
    }
}
