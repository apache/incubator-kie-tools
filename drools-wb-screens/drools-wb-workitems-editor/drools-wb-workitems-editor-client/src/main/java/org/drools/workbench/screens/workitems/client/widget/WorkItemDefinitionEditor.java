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

package org.drools.workbench.screens.workitems.client.widget;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.ResizeComposite;
import org.uberfire.ext.widgets.common.client.ace.AceEditor;
import org.uberfire.ext.widgets.common.client.ace.AceEditorTheme;

/**
 * This is the Work Item definition editor widget.
 */
public class WorkItemDefinitionEditor extends ResizeComposite {

    private final AceEditor editor = new AceEditor();

    public WorkItemDefinitionEditor() {
        editor.startEditor();
        editor.setModeByName( "drools" );
        editor.setTheme( AceEditorTheme.CHROME );
        initWidget( editor );
    }

    public void setContent( final String definition ) {
        editor.setText( definition );
    }

    public String getContent() {
        return editor.getValue();
    }

    public void setReadOnly( final boolean readOnly ) {
        editor.setReadOnly( readOnly );
    }

    public void insertAtCursor( final String text ) {
        editor.insertAtCursor( text );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        editor.setHeight( height + "px" );
        editor.redisplay();
    }

}
