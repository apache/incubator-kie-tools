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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.UberElement;

public interface ParametersPopoverView extends PopoverView,
                                               UberElement<ParametersPopoverView.Presenter> {

    interface Presenter extends HasCellEditorControls.Editor<HasParametersControl> {

        void addParameter();

        void removeParameter(final InformationItem parameter);

        void updateParameterName(final InformationItem parameter,
                                 final String name);

        void updateParameterTypeRef(final InformationItem parameter,
                                    final QName typeRef);
    }

    void setParameters(final List<InformationItem> parameters);

    void focusParameter(final int index);
}
