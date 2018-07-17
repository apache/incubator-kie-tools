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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableTextHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public abstract class ClauseColumnHeaderMetaData<W extends ValueBoxBase<String>, E extends BaseDOMElement<String, W> & TakesValue<String> & Focusable> extends EditableTextHeaderMetaData<W, E> {

    public ClauseColumnHeaderMetaData(final Supplier<String> titleGetter,
                                      final Consumer<String> titleSetter,
                                      final SingletonDOMElementFactory<W, E> factory,
                                      final String columnGroup) {
        super(titleGetter,
              titleSetter,
              factory,
              columnGroup);
    }
}
