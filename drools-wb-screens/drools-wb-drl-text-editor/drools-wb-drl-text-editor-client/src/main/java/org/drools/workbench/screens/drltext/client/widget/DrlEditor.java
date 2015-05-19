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

package org.drools.workbench.screens.drltext.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

/**
 * This is the default rule editor widget (just text editor based) - more to come later.
 */
public class DrlEditor
        extends Composite implements RequiresResize {

    interface ViewBinder
            extends
            UiBinder<Widget, DrlEditor> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    public static int SCROLL_BAR_SIZE = 32;

    @UiField
    FlowPanel drlEditorContainer;

    private final AceEditor editor = new AceEditor();

    public DrlEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        editor.startEditor();
        editor.setModeByName( "drools" );
        editor.setTheme( AceEditorTheme.CHROME );
        drlEditorContainer.add( editor );
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
        editor.setHeight( ( drlEditorContainer.getOffsetHeight() + SCROLL_BAR_SIZE ) + "px" );
        editor.redisplay();
    }

}
