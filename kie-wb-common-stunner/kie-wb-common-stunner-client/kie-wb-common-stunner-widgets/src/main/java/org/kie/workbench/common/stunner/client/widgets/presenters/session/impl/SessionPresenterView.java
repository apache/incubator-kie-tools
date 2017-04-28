/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;

// TODO: i18n.
@Dependent
@Templated
public class SessionPresenterView extends Composite
        implements SessionPresenter.View {

    private static final int DELAY = 10000;
    private static final int TIMER = 100;

    @Inject
    @DataField
    private Label loadingPanel;

    @Inject
    @DataField
    private FlowPanel toolbarPanel;

    @Inject
    @DataField
    private FlowPanel canvasPanel;

    @Inject
    @DataField
    private FlowPanel palettePanel;

    private final NotifySettings settings = NotifySettings.newSettings();

    @PostConstruct
    public void init() {
        settings.setShowProgressbar(false);
        settings.setPauseOnMouseOver(true);
        settings.setAllowDismiss(true);
        settings.setDelay(DELAY);
        settings.setTimer(TIMER);
        showLoading(false);
    }

    @Override
    public SessionPresenterView setToolbarWidget(final IsWidget widget) {
        setWidgetForPanel(toolbarPanel,
                          widget);
        return this;
    }

    @Override
    public SessionPresenterView setPaletteWidget(final IsWidget widget) {
        setWidgetForPanel(palettePanel,
                          widget);
        return this;
    }

    @Override
    public SessionPresenterView showError(final String message) {
        settings.setType(NotifyType.DANGER);
        showNotification("Error",
                         message,
                         IconType.CLOSE);
        return this;
    }

    @Override
    public SessionPresenter.View showWarning(final String message) {
        settings.setType(NotifyType.WARNING);
        showNotification("Warning",
                         message,
                         IconType.CLOSE);
        return this;
    }

    @Override
    public SessionPresenterView showMessage(final String message) {
        settings.setType(NotifyType.SUCCESS);
        showNotification("Info",
                         message,
                         IconType.STICKY_NOTE);
        return this;
    }

    private void showNotification(final String title,
                                  final String message,
                                  final IconType icon) {
        Notify.notify(title,
                      message,
                      icon,
                      settings);
    }

    @Override
    public SessionPresenterView setCanvasWidget(final IsWidget widget) {
        setWidgetForPanel(canvasPanel,
                          widget);
        return this;
    }

    @Override
    public SessionPresenterView showLoading(final boolean loading) {
        loadingPanel.setVisible(loading);
        return this;
    }

    protected void setWidgetForPanel(final Panel panel,
                                     final IsWidget widget) {
        panel.clear();
        panel.add(widget);
    }

    public void destroy() {
        this.removeFromParent();
    }
}
