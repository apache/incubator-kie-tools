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

package org.drools.workbench.screens.guided.dtable.client.editor.clipboard;

import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

public interface Clipboard {

    /**
     * Set data on clipboard
     * @param data Selected cells and their original coordinates.
     */
    void setData( final Set<ClipboardData> data );

    /**
     * Get the data on the clipboard. Could be null.
     * @return
     */
    Set<ClipboardData> getData();

    /**
     * Does the clipboard contain (non-null) data.
     * @return
     */
    boolean hasData();

    void clear();

    /**
     * A single cell
     */
    interface ClipboardData {

        /**
         * Source Row index, relative to the first selection e.g. if (2, 3) is selected then (3, 3) has an index of 1.
         * @return
         */
        int getRowIndex();

        /**
         * Source Column index, relative to the first selection e.g. if (2, 3) is selected then (2, 4) has an index of 1.
         * @return
         */
        int getColumnIndex();

        /**
         * Source value.
         * @return
         */
        DTCellValue52 getValue();

    }

}
