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
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

import java.util.ArrayList;
import java.util.List;

public class ColumnEditorWidget implements EditorWidget {

    private final RowEditorWidget parent;

    private final String span;

    private final ComplexPanel container;

    private List<EditorWidget> childs = new ArrayList<EditorWidget>();

    public ColumnEditorWidget( RowEditorWidget row,
                               ComplexPanel container,
                               String span ) {
        this.container = container;
        this.parent = row;
        this.span = span;
        row.addChild( this );
    }

    public EditorWidget getParent() {
        return parent;
    }

    public ComplexPanel getWidget() {
        return container;
    }

    @Override
    public List<EditorWidget> getChildren() {
        return childs;
    }

    @Override
    public void addChild( EditorWidget editorWidget ) {
        childs.add( editorWidget );
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        childs.remove( editorWidget );
    }

    public List<EditorWidget> getChilds() {
        return childs;
    }

    public String getSpan() {
        return span;
    }

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

    public boolean childsIsRowEditorWidgetUI() {
        return getChilds().get( 0 ) instanceof RowEditorWidget;
    }
}