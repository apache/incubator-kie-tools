/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

public class RowDnDEvent {

    private final String rowIdBegin;
    private final String rowIdEnd;
    private final RowDrop.Orientation orientation;

    public RowDnDEvent(String rowIdBegin,
                       String rowIdEnd,
                       RowDrop.Orientation orientation) {
        this.rowIdBegin = rowIdBegin;
        this.rowIdEnd = rowIdEnd;
        this.orientation = orientation;
    }

    public RowDrop.Orientation getOrientation() {
        return orientation;
    }

    public String getRowIdBegin() {
        return rowIdBegin;
    }

    public String getRowIdEnd() {
        return rowIdEnd;
    }
}
