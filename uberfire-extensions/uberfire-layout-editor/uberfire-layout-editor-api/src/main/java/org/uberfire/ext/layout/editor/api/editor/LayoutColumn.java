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

package org.uberfire.ext.layout.editor.api.editor;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutColumn {

    private String span;

    private List<LayoutRow> rows = new ArrayList<LayoutRow>();

    private List<LayoutComponent> layoutComponents = new ArrayList<LayoutComponent>();

    public LayoutColumn( @MapsTo("span") String span ) {
        this.span = span;
    }

    public void addRow( LayoutRow layoutRow) {
        rows.add(layoutRow);
    }

    public void addLayoutComponent( LayoutComponent layoutComponent ) {
        layoutComponents.add( layoutComponent );
    }

    public String getSpan() {
        return span;
    }

    public List<LayoutRow> getRows() {
        return rows;
    }

    public List<LayoutComponent> getLayoutComponents() {
        return layoutComponents;
    }

    public boolean hasElements() {
        return !rows.isEmpty() || !layoutComponents.isEmpty();
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof LayoutColumn) ) {
            return false;
        }

        LayoutColumn that = (LayoutColumn) o;

        if ( span != null ? !span.equals( that.span ) : that.span != null ) {
            return false;
        }
        if ( rows != null ? !rows.equals( that.rows ) : that.rows != null ) {
            return false;
        }
        return !( layoutComponents != null ? !layoutComponents.equals( that.layoutComponents ) : that.layoutComponents != null );

    }

    @Override
    public int hashCode() {
        int result = span != null ? span.hashCode() : 0;
        result = 31 * result + ( rows != null ? rows.hashCode() : 0 );
        result = 31 * result + ( layoutComponents != null ? layoutComponents.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {
        return "LayoutColumn{" +
                "span='" + span + '\'' +
                ", rows=" + rows +
                ", layoutComponents=" + layoutComponents +
                '}';
    }
}
