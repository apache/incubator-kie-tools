/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ContributorsManagementListItemView implements ContributorsManagementListItemPresenter.View,
                                                           IsElement {

    private ContributorsManagementListItemPresenter presenter;

    @Inject
    @DataField("checkbox")
    Input checkbox;

    @Inject
    @DataField("name")
    Span name;

    @Override
    public void init(final ContributorsManagementListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setUserName(final String userName) {
        name.setTextContent(userName);
    }

    @Override
    public void setSelected(final boolean selected) {
        checkbox.setChecked(selected);
    }

    @Override
    public boolean isSelected() {
        return checkbox.getChecked();
    }

    @Override
    public void setEnabled(boolean enabled) {
        checkbox.setDisabled(!enabled);
    }
}
