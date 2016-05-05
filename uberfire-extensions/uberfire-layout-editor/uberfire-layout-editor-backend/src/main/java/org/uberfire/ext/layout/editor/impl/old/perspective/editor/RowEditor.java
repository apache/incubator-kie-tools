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

package org.uberfire.ext.layout.editor.impl.old.perspective.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Deprecated
public class RowEditor {

    private List<String> rowSpam = new ArrayList<String>();

    private List<ColumnEditor> columnEditors = new ArrayList<ColumnEditor>();

    public RowEditor() {

    }

    public RowEditor( List<String> rowSpam ) {
        this.rowSpam = rowSpam;
    }

    public List<ColumnEditor> getColumnEditors() {
        return columnEditors;
    }

    public void add( ColumnEditor columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public List<String> getRowSpam() {
        return rowSpam;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RowEditor ) ) {
            return false;
        }

        RowEditor rowEditor = (RowEditor) o;

        if ( columnEditors != null ? !columnEditors.equals( rowEditor.columnEditors ) : rowEditor.columnEditors != null ) {
            return false;
        }
        if ( rowSpam != null ? !rowSpam.equals( rowEditor.rowSpam ) : rowEditor.rowSpam != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rowSpam != null ? rowSpam.hashCode() : 0;
        result = 31 * result + ( columnEditors != null ? columnEditors.hashCode() : 0 );
        return result;
    }
}
