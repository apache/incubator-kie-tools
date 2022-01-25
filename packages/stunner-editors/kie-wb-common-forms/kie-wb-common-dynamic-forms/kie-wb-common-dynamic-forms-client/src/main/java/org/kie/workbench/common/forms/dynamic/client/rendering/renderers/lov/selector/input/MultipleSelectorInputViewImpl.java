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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class MultipleSelectorInputViewImpl<TYPE> extends Composite implements MultipleSelectorInputView<TYPE> {

    private Presenter presenter;

    @Inject
    @DataField
    private Div selector;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

        IsWidget selectorWidget = presenter.getSelector();
        selectorWidget.asWidget().getElement().getStyle().setWidth(100, Style.Unit.PCT);

        DOMUtil.removeAllChildren(selector);
        DOMUtil.appendWidgetToElement(selector, selectorWidget);
    }

    @Override
    public HasValue<List<TYPE>> wrapped() {
        return presenter;
    }
}
