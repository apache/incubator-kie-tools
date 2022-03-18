/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.widget;

import javax.inject.Inject;

import elemental2.dom.HTMLOptionElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("KieSelectElementView.html#option")
public class KieSelectOptionView implements ListItemView<KieSelectOptionElement>,
                                            IsElement {

    @Inject
    @DataField("option")
    private HTMLOptionElement option;

    private KieSelectOptionElement presenter;

    @Override
    public void init(final KieSelectOptionElement presenter) {
        this.presenter = presenter;
    }

    public void setLabel(final String label) {
        this.option.textContent = label;
    }

    public void setValue(final String value) {
        this.option.value = value;
    }
}
