/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.search;

import org.kie.workbench.common.widgets.client.search.common.Searchable;
import org.uberfire.mvp.Command;

public class DMNSearchableElement implements Searchable {

    private Command onFound;

    private String text;

    private int row;

    private int column;

    @Override
    public boolean matches(final String text) {
        return this.text.toUpperCase().contains(text.toUpperCase());
    }

    @Override
    public Command onFound() {
        return onFound;
    }

    public void setOnFound(final Command onFound) {
        this.onFound = onFound;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public void setColumn(final int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}