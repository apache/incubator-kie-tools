/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public abstract class EditableTextHeaderMetaData<W extends ValueBoxBase<String>, E extends BaseDOMElement<String, W> & TakesValue<String> & Focusable> implements EditableHeaderMetaData {

    private static final String DEFAULT_COLUMN_GROUP = "";

    protected final String columnGroup;
    protected final Supplier<String> titleGetter;
    protected final Consumer<String> titleSetter;
    protected final SingletonDOMElementFactory<W, E> factory;

    public EditableTextHeaderMetaData(final Supplier<String> titleGetter,
                                      final Consumer<String> titleSetter,
                                      final SingletonDOMElementFactory<W, E> factory) {
        this(titleGetter,
             titleSetter,
             factory,
             DEFAULT_COLUMN_GROUP);
    }

    public EditableTextHeaderMetaData(final Supplier<String> titleGetter,
                                      final Consumer<String> titleSetter,
                                      final SingletonDOMElementFactory<W, E> factory,
                                      final String columnGroup) {
        this.columnGroup = columnGroup;
        this.titleGetter = titleGetter;
        this.titleSetter = titleSetter;
        this.factory = factory;
    }

    @Override
    public String getTitle() {
        return titleGetter.get();
    }

    @Override
    public void setTitle(final String title) {
        titleSetter.accept(title);
    }

    @Override
    public String getColumnGroup() {
        return columnGroup;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public void destroyResources() {
        factory.destroyResources();
    }

    @Override
    public void edit(final GridBodyCellEditContext context) {
        factory.attachDomElement(context,
                                 (e) -> e.setValue(getTitle()),
                                 (e) -> e.setFocus(true));
    }

    @Override
    public GridCellEditAction getSupportedEditAction() {
        return GridCellEditAction.DOUBLE_CLICK;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EditableTextHeaderMetaData)) {
            return false;
        }

        EditableTextHeaderMetaData that = (EditableTextHeaderMetaData) o;

        if (!titleGetter.get().equals(that.titleGetter.get())) {
            return false;
        }
        return getColumnGroup().equals(that.getColumnGroup());
    }

    @Override
    public int hashCode() {
        int result = titleGetter.get().hashCode();
        result = ~~result;
        result = 31 * result + getColumnGroup().hashCode();
        result = ~~result;
        return result;
    }
}