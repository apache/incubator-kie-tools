/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

/**
 * A selected range within the data
 */
public class SelectedRange {

    private final int uiRowIndex;
    private final int uiColumnIndex;
    private int width;
    private int height;

    public SelectedRange(final int uiRowIndex,
                         final int uiColumnIndex) {
        this(uiRowIndex,
             uiColumnIndex,
             1,
             1);
    }

    public SelectedRange(final int uiRowIndex,
                         final int uiColumnIndex,
                         final int width,
                         final int height) {
        this.uiRowIndex = uiRowIndex;
        this.uiColumnIndex = uiColumnIndex;
        this.width = width;
        this.height = height;
    }

    public int getUiRowIndex() {
        return uiRowIndex;
    }

    public int getUiColumnIndex() {
        return uiColumnIndex;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SelectedRange)) {
            return false;
        }

        SelectedRange that = (SelectedRange) o;

        if (uiRowIndex != that.uiRowIndex) {
            return false;
        }
        if (uiColumnIndex != that.uiColumnIndex) {
            return false;
        }
        if (width != that.width) {
            return false;
        }
        return height == that.height;
    }

    @Override
    public int hashCode() {
        int result = uiRowIndex;
        result = ~~result;
        result = 31 * result + uiColumnIndex;
        result = ~~result;
        result = 31 * result + width;
        result = ~~result;
        result = 31 * result + height;
        result = ~~result;
        return result;
    }
}
