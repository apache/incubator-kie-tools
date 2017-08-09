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

package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.Optional;

import com.google.gwt.event.dom.client.HasContextMenuHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

public interface BaseExpressionEditorView<P extends BaseExpressionEditorView.Editor, E extends Expression> extends UberElement<P>,
                                                                                                                   IsElement,
                                                                                                                   RequiresResize,
                                                                                                                   ProvidesResize,
                                                                                                                   HasKeyDownHandlers,
                                                                                                                   HasMouseDownHandlers,
                                                                                                                   HasContextMenuHandlers {

    void setHasName(final Optional<HasName> hasName);

    void setExpression(final E expression);

    GridWidget makeGridWidget();

    BaseUIModelMapper<E> makeUiModelMapper();

    interface Editor<E extends Expression> {

        IsElement getView();

        void setHasName(final Optional<HasName> hasName);

        void setExpression(final E expression);
    }
}
