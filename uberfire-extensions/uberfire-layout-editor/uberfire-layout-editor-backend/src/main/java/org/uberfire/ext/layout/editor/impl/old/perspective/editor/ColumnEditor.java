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
public class ColumnEditor {

    private String span;

    private List<RowEditor> rows = new ArrayList<RowEditor>();

    private List<ScreenEditor> screens = new ArrayList<ScreenEditor>();

    private List<HTMLEditor> htmls = new ArrayList<HTMLEditor>();

    public ColumnEditor() {
    }

    public ColumnEditor( String span ) {
        this.span = span;
    }

    public void addRow( RowEditor rowEditor ) {
        rows.add( rowEditor );
    }

    public void addScreen( ScreenEditor screenEditor ) {
        screens.add( screenEditor );
    }

    public void addHTML( HTMLEditor htmlEditor ) {
        htmls.add( htmlEditor );
    }

    public String getSpan() {
        return span;
    }

    public List<RowEditor> getRows() {
        return rows;
    }

    public List<ScreenEditor> getScreens() {
        return screens;
    }

    public List<HTMLEditor> getHtmls() {
        return htmls;
    }

    public boolean hasElements() {
        return !rows.isEmpty() || !screens.isEmpty() || !htmls.isEmpty();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ColumnEditor ) ) {
            return false;
        }

        ColumnEditor that = (ColumnEditor) o;

        if ( htmls != null ? !htmls.equals( that.htmls ) : that.htmls != null ) {
            return false;
        }
        if ( rows != null ? !rows.equals( that.rows ) : that.rows != null ) {
            return false;
        }
        if ( screens != null ? !screens.equals( that.screens ) : that.screens != null ) {
            return false;
        }
        if ( span != null ? !span.equals( that.span ) : that.span != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + ( rows != null ? rows.hashCode() : 0 );
        result = 31 * result + ( screens != null ? screens.hashCode() : 0 );
        result = 31 * result + ( htmls != null ? htmls.hashCode() : 0 );
        return result;
    }
}
