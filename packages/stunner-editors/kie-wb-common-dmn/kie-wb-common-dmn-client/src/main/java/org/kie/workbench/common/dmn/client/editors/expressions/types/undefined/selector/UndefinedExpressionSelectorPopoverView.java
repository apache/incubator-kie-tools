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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector;

import java.util.List;

import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.UberElement;

public interface UndefinedExpressionSelectorPopoverView extends PopoverView,
                                                                UberElement<UndefinedExpressionSelectorPopoverView.Presenter> {

    interface Presenter extends HasCellEditorControls.Editor<UndefinedExpressionGrid> {

        void onExpressionEditorDefinitionSelected(final ExpressionEditorDefinition definition);
    }

    void setExpressionEditorDefinitions(final List<ExpressionEditorDefinition> definitions);
}
