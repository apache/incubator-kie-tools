/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class EmptyChangeRequestListView implements IsElement,
                                                   EmptyChangeRequestListPresenter.View {

    @Inject
    @DataField("submit-change-request")
    private HTMLButtonElement submitChangeRequest;

    private EmptyChangeRequestListPresenter presenter;

    @Override
    public void init(EmptyChangeRequestListPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("submit-change-request")
    public void onSubmitChangeRequestClicked(final ClickEvent event) {
        this.presenter.goToSubmitChangeRequest();
    }

    @Override
    public void enableSubmitChangeRequestButton(final boolean isEnabled) {
        this.submitChangeRequest.disabled = !isEnabled;
    }
}
