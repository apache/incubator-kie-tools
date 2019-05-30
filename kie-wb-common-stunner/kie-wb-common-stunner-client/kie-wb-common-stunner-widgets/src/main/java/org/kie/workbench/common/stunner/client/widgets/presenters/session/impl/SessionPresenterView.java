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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.palette.PaletteWidget;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinition;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

import static org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants.SessionPresenterView_Error;
import static org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants.SessionPresenterView_Info;
import static org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants.SessionPresenterView_Notifications;
import static org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants.SessionPresenterView_Warning;

// TODO: i18n.
@Dependent
@Templated
public class SessionPresenterView extends Composite
        implements SessionPresenter.View {

    protected static final int DELAY = 1000;
    protected static final int NOTIFICATION_LOCK_TIMEOUT = DELAY + 1000;

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

    @Inject
    private TranslationService translationService;

    private final NotifySettings settings = NotifySettings.newSettings();

    private ScrollType scrollType = ScrollType.AUTO;
    private double paletteInitialTop;
    private double paletteInitialLeft;
    private HandlerRegistration handlerRegistration;
    private final AtomicBoolean notifying = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        settings.setShowProgressbar(false);
        settings.setPauseOnMouseOver(true);
        settings.setNewestOnTop(true);
        settings.setAllowDismiss(true);
        settings.setDelay(DELAY);
        settings.setAnimation(Animation.NO_ANIMATION, Animation.FADE_OUT);
        handlerRegistration = addDomHandler((e) -> {
                                                e.preventDefault();
                                                e.stopPropagation();
                                            },
                                            ContextMenuEvent.getType());

        addAttachHandler(event -> {
            if (event.isAttached()) {
                getElement().getParentElement().getStyle().setHeight(100.0, Style.Unit.PCT);
                getElement().getParentElement().getStyle().setWidth(100.0, Style.Unit.PCT);
            }
        });

        showLoading(false);

        //getting initial palette position
        paletteInitialTop = palettePanel.getAbsoluteTop();
        paletteInitialLeft = palettePanel.getAbsoluteLeft();
    }

    @EventHandler("sessionContainer")
    protected void onScroll(@ForEvent("scroll") ScrollEvent e) {
        // on the editor scroll recalculate palette position to be fixed on the screen
        palettePanel.getElement().getStyle().setTop(paletteInitialTop + e.getRelativeElement().getScrollTop(),
                                                    Style.Unit.PX);
        palettePanel.getElement().getStyle().setLeft(paletteInitialLeft + e.getRelativeElement().getScrollLeft(),
                                                     Style.Unit.PX);

        e.preventDefault();
    }

    void onCanvasFocusedSelectionEvent(final @Observes CanvasFocusedShapeEvent event) {
        getSessionContainer().getElement().setScrollLeft(event.getX());
        getSessionContainer().getElement().setScrollTop(event.getY());
    }

    SessionContainer getSessionContainer() {
        return sessionContainer;
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

        getSettings().setType(kieNotificationCssClass(NotifyType.DANGER));
        showNotification(translate(SessionPresenterView_Error), message, IconType.EXCLAMATION_CIRCLE);

        return this;
    }

    @Override
    public SessionPresenter.View showWarning() {
        singleNotify(() -> {
            getSettings().setType(kieNotificationCssClass(NotifyType.WARNING));
            showNotification(translate(SessionPresenterView_Warning),
                             translate(SessionPresenterView_Notifications),
                             IconType.EXCLAMATION_TRIANGLE);
        });
        return this;
    }

    //show only one notification at a time
    private void singleNotify(final Runnable notification) {
        //check if any other notification is ongoing and set a lock
        if (notifying.compareAndSet(false, true)) {
            //timer to remove the lock on notification
            new Timer() {
                @Override
                public void run() {
                    notifying.set(false);
                }
            }.schedule(NOTIFICATION_LOCK_TIMEOUT);

            Notify.hideAll();
            notification.run();
        }
    }

    @Override
    public SessionPresenterView showMessage(final String message) {
        getSettings().setType(kieNotificationCssClass(NotifyType.SUCCESS));
        showNotification(translate(SessionPresenterView_Info), message, IconType.INFO_CIRCLE);
        return this;
    }

    void showNotification(final String title,
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
        palettePanel.getElement().getStyle().setTop(paletteInitialTop + sessionContainer.getElement().getScrollTop(),
                                                    Style.Unit.PX);
        palettePanel.getElement().getStyle().setLeft(paletteInitialLeft + sessionContainer.getElement().getScrollLeft(),
                                                     Style.Unit.PX);
        canvasPanel.onResize();
    }

    protected void setWidgetForPanel(final Panel panel,
                                     final IsWidget widget) {
        panel.clear();
        panel.add(widget);
    }

    public void destroy() {
        handlerRegistration.removeHandler();
        handlerRegistration = null;
        loadingPanel.removeFromParent();
        toolbarPanel.clear();
        toolbarPanel.removeFromParent();
        canvasPanel.clear();
        canvasPanel.removeFromParent();
        palettePanel.clear();
        palettePanel.removeFromParent();
        sessionContainer.clear();
        sessionContainer.removeFromParent();
        this.removeFromParent();
    }

    NotifySettings getSettings() {
        return settings;
    }

    TranslationService getTranslationService() {
        return translationService;
    }

    private String translate(final String translationKey) {
        return getTranslationService().getTranslation(translationKey);
    }

    private static String buildHtmlEscapedText(final String message) {
        return new SafeHtmlBuilder().appendEscapedLines(message).toSafeHtml().asString();
    }

    private String kieNotificationCssClass(final NotifyType notifyType) {
        return notifyType.getCssName() + " kie-session-notification";
    }
}
