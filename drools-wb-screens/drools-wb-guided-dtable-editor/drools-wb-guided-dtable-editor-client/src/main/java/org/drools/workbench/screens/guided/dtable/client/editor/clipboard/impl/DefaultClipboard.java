/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.editor.clipboard.impl;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.editor.clipboard.Clipboard;

@ApplicationScoped
public class DefaultClipboard implements Clipboard {

    private Set<ClipboardData> data;

    @Override
    public void setData( final Set<ClipboardData> data ) {
        this.data = data;
    }

    @Override
    public Set<ClipboardData> getData() {
        return this.data;
    }

    @Override
    public boolean hasData() {
        return !( this.data == null || this.data.isEmpty() );
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    public static class ClipboardDataImpl implements ClipboardData {

        private int rowIndex;
        private int columnIndex;
        private DTCellValue52 value;

        public ClipboardDataImpl( final int rowIndex,
                                  final int columnIndex,
                                  final DTCellValue52 value ) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.value = value;
        }

        @Override
        public int getRowIndex() {
            return rowIndex;
        }

        @Override
        public int getColumnIndex() {
            return columnIndex;
        }

        @Override
        public DTCellValue52 getValue() {
            return value;
        }

    }

}
