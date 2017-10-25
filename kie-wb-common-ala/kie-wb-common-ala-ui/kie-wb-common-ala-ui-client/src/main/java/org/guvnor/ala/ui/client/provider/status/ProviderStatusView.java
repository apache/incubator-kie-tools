/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
@Templated
public class ProviderStatusView
        implements org.jboss.errai.ui.client.local.api.IsElement,
                   ProviderStatusPresenter.View {

    @Inject
    @DataField("container")
    private Div container;

    private ProviderStatusPresenter presenter;

    @Override
    public void init(final ProviderStatusPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addListItem(final IsElement listItem) {
        container.appendChild(checkNotNull("listItem",
                                           listItem).getElement());
    }

    public void removeListItem(final IsElement listItem) {
        container.removeChild(checkNotNull("listItem",
                                           listItem).getElement());
    }

    @Override
    public void clear() {
        removeAllChildren(container);
    }
}
