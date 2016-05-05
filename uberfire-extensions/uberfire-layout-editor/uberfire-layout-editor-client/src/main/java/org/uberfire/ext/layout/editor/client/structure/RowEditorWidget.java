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

package org.uberfire.ext.layout.editor.client.structure;

import com.google.gwt.user.client.ui.ComplexPanel;
import org.uberfire.ext.layout.editor.client.components.HasOnRemoveNotification;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

import java.util.ArrayList;
import java.util.List;

public class RowEditorWidget implements EditorWidget {

    private final EditorWidget parent;
    private final ComplexPanel container;
    private List<String> rowSpans = new ArrayList<String>();

    private List<EditorWidget> columnEditors = new ArrayList<EditorWidget>();

    public RowEditorWidget( EditorWidget parent,
                            ComplexPanel container,
                            String rowSpamString ) {
        this.parent = parent;
        this.container = container;
        parseRowSpanString( rowSpamString );
        parent.addChild( this );
    }

    public RowEditorWidget( EditorWidget parent,
                            ComplexPanel container,
                            List<String> rowSpans ) {
        this.parent = parent;
        this.container = container;
        this.rowSpans = rowSpans;
        parent.addChild( this );
    }

    public EditorWidget getParent() {
        return parent;
    }

    public ComplexPanel getWidget() {
        return container;
    }

    public List<String> getRowSpans() {
        return rowSpans;
    }

    private void parseRowSpanString( String rowSpamString ) {
        String[] spans = rowSpamString.split( " " );
        for ( String span : spans ) {
            rowSpans.add( span );
        }
    }

    @Override
    public List<EditorWidget> getChildren() {
        return columnEditors;
    }

    public void addChild( EditorWidget columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public void removeFromParent() {
        parent.removeChild( this );
        notifyChildRemoval( columnEditors );

    }


    protected void notifyChildRemoval( List<EditorWidget> childEditors ) {
        if ( childEditors == null ) return;

        for ( EditorWidget editor : childEditors ) {

            if ( editor.getType() instanceof HasOnRemoveNotification ) {
                ( ( HasOnRemoveNotification ) editor.getType() ).onRemoveComponent();
            }

            if ( editor.getChildren() != null ) notifyChildRemoval( editor.getChildren() );
        }
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        columnEditors.remove( editorWidget );
    }

    public List<EditorWidget> getColumnEditors() {
        return columnEditors;
    }

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

}