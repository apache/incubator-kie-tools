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

package org.kie.workbench.common.stunner.core.client.components.palette.view;

public abstract class AbstractPaletteGridBuilder<B> {

    protected int rows;
    protected int columns;
    protected int iconSize;
    protected int padding;

    protected abstract PaletteGrid build();

    @SuppressWarnings("unchecked")
    public B setRows(final int rows) {
        this.rows = rows;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setColumns(final int columns) {
        this.columns = columns;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setPadding(final int padding) {
        this.padding = padding;
        return (B) this;
    }
}
