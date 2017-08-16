/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public abstract class EditableHeaderMetaData<W extends Widget & Focusable & HasText, E extends BaseDOMElement<String, W>> extends BaseHeaderMetaData {

    private static final String DEFAULT_COLUMN_GROUP = "";

    protected final Supplier<String> titleGetter;
    protected final Consumer<String> titleSetter;
    protected final SingletonDOMElementFactory<W, E> factory;

    public EditableHeaderMetaData(final Supplier<String> titleGetter,
                                  final Consumer<String> titleSetter,
                                  final SingletonDOMElementFactory<W, E> factory) {
        this(titleGetter,
             titleSetter,
             factory,
             DEFAULT_COLUMN_GROUP);
    }

    public EditableHeaderMetaData(final Supplier<String> titleGetter,
                                  final Consumer<String> titleSetter,
                                  final SingletonDOMElementFactory<W, E> factory,
                                  final String columnGroup) {
        super(titleGetter.get(),
              columnGroup);
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
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    public void edit(final GridBodyCellRenderContext context) {
        factory.attachDomElement(context,
                                 (e) -> e.getWidget().setText(getTitle()),
                                 (e) -> e.getWidget().setFocus(true));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EditableHeaderMetaData)) {
            return false;
        }

        EditableHeaderMetaData that = (EditableHeaderMetaData) o;

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