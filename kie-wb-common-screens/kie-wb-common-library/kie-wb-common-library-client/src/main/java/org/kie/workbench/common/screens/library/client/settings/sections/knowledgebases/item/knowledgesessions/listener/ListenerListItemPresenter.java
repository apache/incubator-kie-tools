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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieEnumSelectElement;
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class ListenerListItemPresenter extends ListItemPresenter<ListenerModel, KnowledgeSessionListItemPresenter, ListenerListItemPresenter.View> {

    private final KieEnumSelectElement<ListenerModel.Kind> kindSelect;

    ListenerModel model;
    KnowledgeSessionListItemPresenter parentPresenter;

    @Inject
    public ListenerListItemPresenter(final View view,
                                     final KieEnumSelectElement<ListenerModel.Kind> kindSelect) {
        super(view);
        this.kindSelect = kindSelect;
    }

    @Override
    public ListenerListItemPresenter setup(final ListenerModel listenerModel,
                                           final KnowledgeSessionListItemPresenter parentPresenter) {

        this.model = listenerModel;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setType(model.getType());

        kindSelect.setup(
                view.getKindSelectContainer(),
                ListenerModel.Kind.values(),
                model.getKind(),
                kind -> {
                    model.setKind(kind);
                    parentPresenter.fireChangeEvent();
                });

        return this;
    }

    public void setType(final String type) {
        model.setType(type);
        parentPresenter.fireChangeEvent();
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.signalListenerAddedOrRemoved();
        parentPresenter.fireChangeEvent();
    }

    @Override
    public ListenerModel getObject() {
        return model;
    }

    public interface View extends ListItemView<ListenerListItemPresenter>,
                                  IsElement {

        void setType(final String type);

        HTMLElement getKindSelectContainer();
    }
}
