/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.WorkItemHandlerModel;

public class WorkItemHandlersPanel
        implements IsWidget, WorkItemHandlersPanelView.Presenter {

    private final WorkItemHandlersPanelView view;
    private List<WorkItemHandlerModel> handlerModels;

    @Inject
    public WorkItemHandlersPanel(WorkItemHandlersPanelView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setHandlerModels(List<WorkItemHandlerModel> handlerModels) {
        this.handlerModels = handlerModels;
        view.setModels(handlerModels);
    }

    @Override
    public void onAdd() {
        handlerModels.add(new WorkItemHandlerModel());
        view.setModels(handlerModels);
    }

    @Override
    public void onDelete(WorkItemHandlerModel model) {
        handlerModels.remove(model);
        view.setModels(handlerModels);
    }

    public void redraw() {
        view.redraw();
    }
}
