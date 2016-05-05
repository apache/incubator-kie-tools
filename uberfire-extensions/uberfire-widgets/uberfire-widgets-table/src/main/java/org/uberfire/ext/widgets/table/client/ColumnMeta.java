/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.table.client;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

public class ColumnMeta<T> implements Comparable {
    private Column<T, ?> column;
    private String caption;
    private Header<String> header;
    private boolean visible = true;
    private int position = -1;
    private boolean extraColumn;

    public ColumnMeta( Column<T, ?> column,
                       String caption) {
        this.column = column;
        this.caption = caption;
    }

    public ColumnMeta( Column<T, ?> column,
                       String caption,
                       boolean visible) {
        this.column = column;
        this.caption = caption;
        this.visible = visible;
    }

    public ColumnMeta( Column<T, ?> column,
                       String caption,
                       boolean visible,
                       int position) {
        this.column = column;
        this.caption = caption;
        this.visible = visible;
        this.position = position;
    }

    public ColumnMeta( Column<T, ?> column, String caption, boolean visible, boolean extraColumn) {
        this(column, caption, visible);
        this.extraColumn = extraColumn;
    }

    public boolean isExtraColumn() {
        return extraColumn;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Header<String> getHeader() {
        return header;
    }

    public void setHeader(Header<String> header) {
        this.header = header;
    }

    public Column<T, ?> getColumn() {
        return column;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ColumnMeta)) {
            return 0;
        }
        ColumnMeta otherMeta = (ColumnMeta) o;
        if (position == -1 && otherMeta.getPosition() == -1) return 0;
        if (position == -1) return  1;
        if (otherMeta.getPosition() == -1) return -1;
        if (position < otherMeta.getPosition()) {
            return -1;
        } else if (position > otherMeta.getPosition()) {
            return 1;
        } else {
            return 0;
        }
    }
}
