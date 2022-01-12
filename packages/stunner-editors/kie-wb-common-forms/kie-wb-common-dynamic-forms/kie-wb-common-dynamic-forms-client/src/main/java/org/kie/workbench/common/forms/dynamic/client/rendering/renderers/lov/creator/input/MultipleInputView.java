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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input;

import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;

public interface MultipleInputView<TYPE> extends IsWidget,
                                                 FormWidget<List<TYPE>> {

    void init(Presenter<TYPE> presenter);

    interface Presenter<TYPE> extends HasValue<List<TYPE>> {

        MultipleInputComponent getComponent();
    }

}
