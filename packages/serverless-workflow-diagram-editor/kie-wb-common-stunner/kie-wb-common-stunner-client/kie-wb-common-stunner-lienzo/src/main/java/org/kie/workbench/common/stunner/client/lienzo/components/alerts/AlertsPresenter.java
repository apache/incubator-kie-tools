/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.client.lienzo.components.alerts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.impl.ScrollablePanel;
import com.ait.lienzo.tools.client.event.EventType;
import com.ait.lienzo.tools.client.event.MouseEventUtil;
import elemental2.dom.Element;
import elemental2.dom.EventListener;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.animate.client.ui.constants.Animation;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.gwtbootstrap3.extras.notify.client.ui.NotifySettings;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.timer.client.Timer;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoPanel;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

@Dependent
public class AlertsPresenter {

    private static final double MARGIN = 25d;
    private static final String ON_MOUSE_OVER = EventType.MOUSE_OVER.getType();
    private static final int ALERT_SHORT_DELAY = 250;
    private static final int ALERT_LONG_DELAY = 1000;
    private static final int NOTIFY_DELAY = 3000;

    private final FloatingView<IsElement> floatingView;
    private final Alerts alerts;
    private final Element alertsElement;
    private Supplier<LienzoCanvas> canvas;
    private EventListener panelResizeEventListener;
    private EventListener alertsMouseOverEventListener;
    private final ClientTranslationService clientTranslationService;
    private final NotifySettings settings = NotifySettings.newSettings();

    private Timer hideTimer;
    private boolean alertsInit = true;
    private boolean notificationOpen = true;

    private List<String> infoList;
    private List<String> warningsList;
    private List<String> errorsList;

    @Inject
    public AlertsPresenter(final FloatingView<IsElement> floatingView,
                           final Alerts alerts,
                           final ClientTranslationService clientTranslationService) {
        this(floatingView, alerts, alerts.getElement(), clientTranslationService);
    }

    AlertsPresenter(final FloatingView<IsElement> floatingView,
                    final Alerts alerts,
                    final Element alertsElement,
                    final ClientTranslationService clientTranslationService) {
        this.floatingView = floatingView;
        this.alerts = alerts;
        this.alertsElement = alertsElement;
        this.clientTranslationService = clientTranslationService;
    }

    public AlertsPresenter init(final Supplier<LienzoCanvas> canvas) {
        infoList = new ArrayList<>();
        warningsList = new ArrayList<>();
        errorsList = new ArrayList<>();

        floatingView
                .clearTimeOut()
                .setOffsetX(0)
                .setOffsetY(0)
                .hide();

        hideTimer = new Timer() {
            @Override
            public void run() {
                hide();
            }
        };

        this.canvas = canvas;
        final LienzoPanel panel = getPanel();
        alerts.onShowInfos(this::showInfos);
        alerts.onShowWarnings(this::showWarnings);
        alerts.onShowErrors(this::showErrors);
        floatingView.add(alerts);

        settings.setShowProgressbar(false);
        settings.setPauseOnMouseOver(true);
        settings.setNewestOnTop(true);
        settings.setAllowDismiss(true);
        settings.setDelay(NOTIFY_DELAY);
        settings.setAnimation(Animation.NO_ANIMATION, Animation.FADE_OUT);
        settings.setShownHandler(() -> notificationOpen = false);
        settings.setClosedHandler(() -> notificationOpen = true);

        if (panel.getView() instanceof ScrollablePanel) {
            final ScrollablePanel scrollablePanel = (ScrollablePanel) panel.getView();
            panelResizeEventListener =
                    scrollablePanel.addResizeEventListener(evt -> onPanelResize(scrollablePanel.getWidePx(),
                                                                                scrollablePanel.getHighPx()));
        }

        updateAlerts();

        alertsMouseOverEventListener = mouseOverEvent -> cancelHide();
        alertsElement.addEventListener(ON_MOUSE_OVER, alertsMouseOverEventListener);

        return this;
    }

    public AlertsPresenter addInfo(final String info) {
        infoList.add(info);
        updateAlerts();
        return this;
    }

    public AlertsPresenter addWarning(final String warning) {
        warningsList.add(warning);
        updateAlerts();
        return this;
    }

    public AlertsPresenter addError(final String error) {
        errorsList.add(error);
        updateAlerts();
        return this;
    }

    public AlertsPresenter clear() {
        infoList.clear();
        warningsList.clear();
        errorsList.clear();
        updateAlerts();
        return this;
    }

    public AlertsPresenter at(final double x, final double y) {
        floatingView.setX(x).setY(y);
        return this;
    }

    public AlertsPresenter show() {
        if (alertsInit) {
            alertsInit = false;
        } else {
            if (notificationOpen) {
                cancelHide();
                floatingView.show();
            }
        }
        return this;
    }

    public AlertsPresenter hide() {
        floatingView.hide();
        return this;
    }

    void cancelHide() {
        hideTimer.cancel();
    }

    void scheduleHide() {
        scheduleHide(ALERT_SHORT_DELAY);
    }

    public void destroy() {
        cancelHide();
        if (null != panelResizeEventListener) {
            ((ScrollablePanel) getPanel().getView()).removeResizeEventListener(panelResizeEventListener);
            panelResizeEventListener = null;
        }
        if (null != alertsMouseOverEventListener) {
            alertsElement.removeEventListener(ON_MOUSE_OVER, alertsMouseOverEventListener);
            alertsMouseOverEventListener = null;
        }
        floatingView.destroy();
        canvas = null;
        hideTimer = null;
    }

    private void reposition() {
        final LienzoPanel panel = getPanel();
        onPanelResize(panel.getView().getWidePx(),
                      panel.getView().getHighPx());
    }

    private AlertsPresenter onPanelResize(final double width, final double height) {
        final LienzoPanel panel = getPanel();
        final int absoluteLeft = MouseEventUtil.getAbsoluteLeft(panel.getView().getElement());
        final int absoluteTop = MouseEventUtil.getAbsoluteTop(panel.getView().getElement());
        final double x = absoluteLeft + width - alerts.getElement().clientWidth - MARGIN;
        final double y = absoluteTop + MARGIN;
        return at(x, y);
    }

    private void scheduleHide(int delay) {
        cancelHide();
        hideTimer.schedule(delay);
    }

    private void updateAlerts() {
        alerts.setInfo(infoList.size());
        alerts.setWarnings(warningsList.size());
        alerts.setErrors(errorsList.size());
        show();
        scheduleHide(ALERT_LONG_DELAY);
        reposition();
    }

    private void showInfos() {
        if (notificationOpen) {
            hide();
            settings.setType(kieNotificationCssClass(NotifyType.SUCCESS));
            Notify.notify(clientTranslationService.getNotNullValue(CoreTranslationMessages.INFORMATION),
                          buildHtmlEscapedText(infoList),
                          IconType.INFO_CIRCLE,
                          settings);
        }
    }

    private void showWarnings() {
        if (notificationOpen) {
            hide();

            settings.setType(kieNotificationCssClass(NotifyType.WARNING));
            Notify.notify(clientTranslationService.getNotNullValue(CoreTranslationMessages.WARNING),
                          buildHtmlEscapedText(warningsList),
                          IconType.EXCLAMATION_TRIANGLE,
                          settings);
        }
    }

    private void showErrors() {
        if (notificationOpen) {
            hide();
            settings.setType(kieNotificationCssClass(NotifyType.DANGER));
            Notify.notify(clientTranslationService.getNotNullValue(CoreTranslationMessages.ERROR),
                          buildHtmlEscapedText(errorsList),
                          IconType.EXCLAMATION_CIRCLE,
                          settings);
        }
    }

    private LienzoPanel getPanel() {
        return (LienzoPanel) canvas.get().getView().getPanel();
    }

    private static String buildHtmlEscapedText(final List<String> messageList) {
        String text = "";
        for (String message : messageList) {
            if (!text.isEmpty()) {
                text += "\r\n";
            }
            text += message;
        }
        return new SafeHtmlBuilder().appendEscapedLines(text).toSafeHtml().asString();
    }

    private static String kieNotificationCssClass(final NotifyType notifyType) {
        final String cssName = StunnerTheme.getTheme().isDarkTheme() ? notifyType.getCssName() + "-dark" : notifyType.getCssName();
        return cssName + " kie-alert-notification";
    }
}
