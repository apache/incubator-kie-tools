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
import org.kie.workbench.common.services.shared.kmodule.ListenerModel;

public class ListenersPanel
        implements IsWidget, ListenersPanelView.Presenter {

    private final ListenersPanelView view;
    private List<ListenerModel> listeners;

    @Inject
    public ListenersPanel(ListenersPanelView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void setListeners(List<ListenerModel> listeners) {
        this.listeners = listeners;
        view.setModels(listeners);
    }

    @Override
    public void onAdd() {
        listeners.add(new ListenerModel());
        view.setModels(listeners);
    }

    @Override
    public void onDelete(ListenerModel model) {
        listeners.remove(model);
        view.setModels(listeners);
    }

    public void redraw() {
        view.redraw();
    }
}
