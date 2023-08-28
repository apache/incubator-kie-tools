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

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.client.resources.i18n.DisplayerConstants;
import org.dashbuilder.patternfly.label.Label;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;

public abstract class AbstractDisplayerView<P extends AbstractDisplayer>
                                           implements AbstractDisplayer.View<P> {

    private HTMLElement rootPanel;
    @Inject
    private Label label = new Label();
    private HTMLElement visualization = null;
    private Timer refreshTimer = null;
    protected P presenter;

    @Inject
    protected Elemental2DomUtil domUtil;

    @Override
    public void init(P presenter) {
        this.presenter = presenter;
        rootPanel = (HTMLElement) DomGlobal.document.createElement("div");

    }

    public P getPresenter() {
        return presenter;
    }

    public void setVisualization(HTMLElement widget) {
        visualization = widget;
    }

    @Override
    public void setId(String id) {
        rootPanel.id = id;
    }

    @Override
    public void clear() {
        domUtil.removeAllElementChildren(rootPanel);
    }

    @Override
    public void showLoading() {
        displayMessage(DisplayerConstants.INSTANCE.initializing());
    }

    @Override
    public void showVisualization() {
        if (visualization != null) {
            this.clear();
            rootPanel.appendChild(visualization);
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
        clear();
        rootPanel.appendChild(label.getElement());
        label.setText(msg);
    }
    
    @Override
    public HTMLElement getElement() {
        return rootPanel;
    }

}
