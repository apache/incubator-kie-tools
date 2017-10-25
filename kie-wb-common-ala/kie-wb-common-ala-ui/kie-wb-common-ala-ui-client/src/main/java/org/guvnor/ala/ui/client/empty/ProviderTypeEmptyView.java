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

package org.guvnor.ala.ui.client.empty;

import javax.enterprise.context.Dependent;

import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ProviderTypeEmptyView
        implements IsElement,
                   ProviderTypeEmptyPresenter.View {

    private ProviderTypeEmptyPresenter presenter;

    @Override
    public void init(final ProviderTypeEmptyPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("empty-add-provider-type")
    public void onAddProviderType(@ForEvent("click") final Event event) {
        presenter.onAddProviderType();
    }

    @EventHandler("new-provider-type-anchor")
    public void onAddProviderTypeAnchor(@ForEvent("click") final Event event) {
        presenter.onAddProviderType();
    }
}
