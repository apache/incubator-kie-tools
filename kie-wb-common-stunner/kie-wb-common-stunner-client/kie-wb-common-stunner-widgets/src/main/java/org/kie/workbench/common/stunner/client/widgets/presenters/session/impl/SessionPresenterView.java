/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.presenters.session.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinition;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

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
    private ResizeFlowPanel canvasPanel;

    @Inject
    @DataField
    private FlowPanel palettePanel;

    @Inject
    @DataField
    private SessionContainer sessionContainer;

    private final NotifySettings settings = NotifySettings.newSettings();

    private ScrollType scrollType = ScrollType.AUTO;

    private double paletteInitialTop;

    private double paletteInitialLeft;

    @PostConstruct
    public void init() {
        settings.setShowProgressbar(false);
        settings.setPauseOnMouseOver(true);
        settings.setAllowDismiss(true);
        settings.setDelay(DELAY);
        settings.setTimer(TIMER);
        addDomHandler((e) -> {
                          e.preventDefault();
                          e.stopPropagation();
                      },
                      ContextMenuEvent.getType());
        showLoading(false);

        //getting initial palette position
        paletteInitialTop = palettePanel.getAbsoluteTop();
        paletteInitialLeft = palettePanel.getAbsoluteLeft();
    }

    @EventHandler("sessionContainer")
    protected void onScroll(@ForEvent("scroll") ScrollEvent e) {
        // on the editor scroll recalculate palette position to be fixed on the screen
        palettePanel.getElement().getStyle().setTop(paletteInitialTop + e.getRelativeElement().getScrollTop(), Style.Unit.PX);
        palettePanel.getElement().getStyle().setLeft(paletteInitialLeft + e.getRelativeElement().getScrollLeft(), Style.Unit.PX);

        e.preventDefault();
    }

    @Override
    public IsWidget getCanvasWidget() {
        return canvasPanel.getWidget(0);
    }

    @Override
    public IsWidget getToolbarWidget() {
        return toolbarPanel.getWidget(0);
    }

    @Override
    public IsWidget getPaletteWidget() {
        return palettePanel.getWidget(0);
    }

    @Override
    public ScrollType getContentScrollType() {
        return scrollType;
    }

    @Override
    public SessionPresenterView setToolbarWidget(final IsWidget widget) {
        setWidgetForPanel(toolbarPanel,
                          widget);
        return this;
    }

    @Override
    public SessionPresenterView setPaletteWidget(final PaletteWidget<PaletteDefinition> paletteWidget) {
        setWidgetForPanel(palettePanel,
                          ElementWrapperWidget.getWidget(paletteWidget.getElement()));
        return this;
    }

    @Override
    public SessionPresenterView setCanvasWidget(final IsWidget widget) {
        setWidgetForPanel(canvasPanel,
                          widget);
        return this;
    }

    @Override
    public void setContentScrollType(final ScrollType type) {
        final Style style = sessionContainer.getElement().getStyle();
        switch (type) {
            case AUTO:
                style.setOverflow(Style.Overflow.AUTO);
                break;
            case CUSTOM:
                style.setOverflow(Style.Overflow.HIDDEN);
        }
    }

    @Override
    public SessionPresenterView showError(final String message) {
        settings.setType(NotifyType.DANGER);
        showNotification("Error",
                         buildHtmlEscapedText(message),
                         IconType.CLOSE);
        return this;
    }

    @Override
    public SessionPresenter.View showWarning(final String message) {
        settings.setType(NotifyType.WARNING);
        showNotification("Warning",
                         buildHtmlEscapedText(message),
                         IconType.CLOSE);
        return this;
    }

    @Override
    public SessionPresenterView showMessage(final String message) {
        settings.setType(NotifyType.SUCCESS);
        showNotification("Info",
                         buildHtmlEscapedText(message),
                         IconType.STICKY_NOTE);
        return this;
    }

    private void showNotification(final String title,
                                  final String message,
                                  final IconType icon) {
        Notify.notify(title,
                      buildHtmlEscapedText(message),
                      icon,
                      settings);
    }

    @Override
    public SessionPresenterView showLoading(final boolean loading) {
        loadingPanel.setVisible(loading);
        return this;
    }

    @Override
    public void onResize() {
        canvasPanel.onResize();
    }

    protected void setWidgetForPanel(final Panel panel,
                                     final IsWidget widget) {
        panel.clear();
        panel.add(widget);
    }

    public void destroy() {
        this.removeFromParent();
    }

    private static String buildHtmlEscapedText(final String message) {
        return new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
    }
}