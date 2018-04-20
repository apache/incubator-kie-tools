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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.workitemhandler;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkItemHandlerModel;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.knowledgesessions.KnowledgeSessionListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.list.ListItemView;

@Dependent
public class WorkItemHandlerListItemPresenter extends ListItemPresenter<WorkItemHandlerModel, KnowledgeSessionListItemPresenter, WorkItemHandlerListItemPresenter.View> {

    private WorkItemHandlerModel model;
    private KnowledgeSessionListItemPresenter parentPresenter;

    @Inject
    public WorkItemHandlerListItemPresenter(final View view) {
        super(view);
    }

    @Override
    public WorkItemHandlerListItemPresenter setup(final WorkItemHandlerModel listenerModel,
                                                  final KnowledgeSessionListItemPresenter parentPresenter) {

        this.model = listenerModel;
        this.parentPresenter = parentPresenter;

        view.init(this);
        view.setName(model.getName());
        view.setType(model.getType());

        return this;
    }

    public void setName(final String name) {
        model.setName(name);
        parentPresenter.fireChangeEvent();
    }

    public void setType(final String type) {
        model.setType(type);
        parentPresenter.fireChangeEvent();
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.signalWorkItemHandlerAddedOrRemoved();
        parentPresenter.fireChangeEvent();
    }

    @Override
    public WorkItemHandlerModel getObject() {
        return model;
    }

    public interface View extends ListItemView<WorkItemHandlerListItemPresenter>,
                                  IsElement {

        void setType(final String type);

        void setName(final String name);
    }
}
