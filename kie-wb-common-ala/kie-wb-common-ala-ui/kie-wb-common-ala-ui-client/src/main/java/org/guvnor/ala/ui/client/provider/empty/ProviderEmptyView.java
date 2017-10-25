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

package org.guvnor.ala.ui.client.provider.empty;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ProviderEmptyView
        implements IsElement,
                   ProviderEmptyPresenter.View {

    @Inject
    @DataField("empty-provider-type-id")
    private Span providerTypeId;

    private ProviderEmptyPresenter presenter;

    @Override
    public void init(final ProviderEmptyPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProviderTypeName(final String providerTypeName) {
        providerTypeId.setTextContent(providerTypeName);
    }

    @EventHandler("empty-provider-add")
    public void onAddProvider(@ForEvent("click") final Event event) {
        presenter.addProvider();
    }

    @EventHandler("add-provider-anchor")
    public void onAddContainerAnchor(@ForEvent("click") final Event event) {
        presenter.addProvider();
    }
}
