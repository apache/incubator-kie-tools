/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerConstants;

public abstract class AbstractGwtDisplayerView<P extends AbstractGwtDisplayer>
        extends Composite
        implements AbstractGwtDisplayer.View<P> {

    private FlowPanel panel = new FlowPanel();
    private Label label = new Label();
    private IsWidget visualization = null;
    private Timer refreshTimer = null;
    protected P presenter = null;

    public AbstractGwtDisplayerView() {
        initWidget(panel);
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    public P getPresenter() {
        return presenter;
    }

    public void setVisualization(IsWidget widget) {
        visualization = widget;
    }

    @Override
    public void setId(String id) {
        panel.getElement().setId(id);
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void showLoading() {
        displayMessage(DisplayerConstants.INSTANCE.initializing());
    }

    @Override
    public void showVisualization() {
        if (visualization != null) {
            panel.clear();
            panel.add(visualization);
        }
    }

    @Override
    public void errorMissingSettings() {
        displayMessage(DisplayerConstants.INSTANCE.error() + DisplayerConstants.INSTANCE.error_settings_unset());
    }

    @Override
    public void errorMissingHandler() {
        displayMessage(DisplayerConstants.INSTANCE.error() + DisplayerConstants.INSTANCE.error_handler_unset());
    }

    @Override
    public void errorDataSetNotFound(String dataSetUUID) {
        displayMessage(CommonConstants.INSTANCE.dataset_lookup_dataset_notfound(dataSetUUID));
    }

    @Override
    public void error(ClientRuntimeError e) {
        displayMessage(DisplayerConstants.INSTANCE.error() + e.getMessage());

        if (e.getThrowable() != null) {
            GWT.log(e.getMessage(), e.getThrowable());
        } else {
            GWT.log(e.getMessage());
        }
    }

    @Override
    public void enableRefreshTimer(int seconds) {
        if (refreshTimer == null) {
            refreshTimer = new Timer() {
                public void run() {
                    if (presenter.isDrawn()) {
                        presenter.redraw();
                    }
                }
            };
        }
        refreshTimer.schedule(seconds * 1000);
    }

    @Override
    public void cancelRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    public void displayMessage(String msg) {
        panel.clear();
        panel.add(label);
        label.setText(msg);
    }

}