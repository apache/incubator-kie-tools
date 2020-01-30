/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.listener;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;

@Templated("#root")
public class ListenerListItemView implements ListenerListItemPresenter.View {

    @Inject
    @DataField("kind-select-container")
    private KieEnumSelectElement<ListenerModel.Kind> kindSelect;

    @Inject
    @DataField("type")
    private HTMLInputElement type;

    @Inject
    @Named("span")
    @DataField("remove-button")
    private HTMLElement removeButton;

    private ListenerListItemPresenter presenter;

    @Override
    public void init(final ListenerListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        presenter.remove();
    }

    @EventHandler("type")
    public void onTypeChanged(final ChangeEvent ignore) {
        presenter.setType(type.value);
    }

    @Override
    public void setType(final String type) {
        this.type.value = type;
    }

    @Override
    public void setupKindSelect(final ListenerModel model,
                                final KnowledgeSessionListItemPresenter parentPresenter) {
        kindSelect.setup(
                ListenerModel.Kind.values(),
                model.getKind(),
                kind -> {
                    model.setKind(kind);
                    parentPresenter.fireChangeEvent();
                });
    }
}
