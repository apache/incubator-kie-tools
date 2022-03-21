/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.Collections;

import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringPopupColumn;

@WithClassesToStub({Text.class})
public class StringColumnRendererTest extends BaseColumnRendererTest<String, StringColumnRenderer> {

    @Override
    public StringColumnRenderer getRenderer() {
        return new StringColumnRenderer() {
            @Override
            @SuppressWarnings("unused")
            protected IPathClipper getBoundingBoxPathClipper(final BoundingBox bb) {
                return boundingBoxPathClipper;
            }
        };
    }

    @Override
    protected String getValueToRender() {
        return "cheese";
    }

    @Override
    protected GridColumn getGridColumn() {
        return new StringPopupColumn(Collections.singletonList(headerMetaData),
                                     renderer,
                                     100.0);
    }
}
