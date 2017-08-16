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

import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public class InputClauseColumnHeaderMetaData extends ClauseColumnHeaderMetaData<TextArea, TextAreaDOMElement> {

    private static final String INPUT_CLAUSE_GROUP = "InputClauseColumnHeaderMetaData$InputClauseColumnMetaData";

    public InputClauseColumnHeaderMetaData(final Supplier<String> titleGetter,
                                           final Consumer<String> titleSetter,
                                           final SingletonDOMElementFactory<TextArea, TextAreaDOMElement> factory) {
        super(titleGetter,
              titleSetter,
              factory,
              INPUT_CLAUSE_GROUP);
    }

    @Override
    public boolean equals(final Object o) {
        // No implementation of equals/hashCode as each instance is considered different to another
        return this == o;
    }
}
