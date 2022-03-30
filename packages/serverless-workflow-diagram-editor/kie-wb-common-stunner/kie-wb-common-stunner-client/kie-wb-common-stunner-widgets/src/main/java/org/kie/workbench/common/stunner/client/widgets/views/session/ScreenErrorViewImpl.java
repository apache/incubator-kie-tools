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

package org.kie.workbench.common.stunner.client.widgets.views.session;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLLabelElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;

@Dependent
@Templated
public class ScreenErrorViewImpl implements ScreenErrorView,
        IsElement {

    @Inject
    @DataField
    private HTMLLabelElement message;

    @Override
    public ScreenErrorView showError(final ClientRuntimeError error) {
        return showMessage(error.toString());
    }

    @Override
    public ScreenErrorView showMessage(final String message) {
        this.message.title = (message);
        this.message.textContent = (message);
        return this;
    }

    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(this.getElement());
    }
}
