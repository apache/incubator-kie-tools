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
import com.google.gwt.user.client.ui.Panel;
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
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

import static com.google.gwt.dom.client.Style.Display.BLOCK;
import static com.google.gwt.dom.client.Style.Display.NONE;

// TODO: i18n.
@Dependent
@Templated
public class SessionPresenterView extends Composite
        implements SessionPresenter.View {

    protected static final int DELAY = 3000;
    protected static final int NOTIFICATION_LOCK_TIMEOUT = DELAY + 1000;

    @Inject
    @DataField
    private ResizeFlowPanel sessionHeaderContainer;

    @Inject
    @DataField
    private ResizeFlowPanel toolbarPanel;

    @Inject
    @DataField
    private ResizeFlowPanel canvasPanel;

    @Inject
    @DataField
    private ResizeFlowPanel palettePanel;

    @Inject
    @DataField
    private SessionContainer sessionContainer;

    @Inject
    private TranslationService translationService;

    private ScrollType scrollType = ScrollType.AUTO;
    private double paletteInitialTop;
    private double paletteInitialLeft;
    private double headerInitialTop;
    private double headerInitialLeft;
    private double sessionHeaderHeight;
    private HandlerRegistration handlerRegistration;
    private final AtomicBoolean notifying = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
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

        //getting initial palette position
        paletteInitialTop = palettePanel.getAbsoluteTop();
        paletteInitialLeft = palettePanel.getAbsoluteLeft();

        //getting initial session header section position
        headerInitialTop = sessionHeaderContainer.getAbsoluteTop();
        headerInitialLeft = sessionHeaderContainer.getAbsoluteLeft();
    }

    @EventHandler("sessionContainer")
    protected void onScroll(@ForEvent("scroll") ScrollEvent e) {
        // on the editor scroll recalculate palette and header positions to be fixed on the screen
        palettePanel.getElement().getStyle().setTop(paletteInitialTop + e.getRelativeElement().getScrollTop() + sessionHeaderHeight,
                                                    Style.Unit.PX);
        palettePanel.getElement().getStyle().setLeft(paletteInitialLeft + e.getRelativeElement().getScrollLeft(),
                                                     Style.Unit.PX);

        sessionHeaderContainer.getElement().getStyle().setTop(headerInitialTop + e.getRelativeElement().getScrollTop(),
                                                              Style.Unit.PX);
        sessionHeaderContainer.getElement().getStyle().setLeft(headerInitialLeft + e.getRelativeElement().getScrollLeft(),
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
    public IsWidget getSessionHeaderContainer() {
        return sessionHeaderContainer.getWidget(0);
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
    public SessionPresenterView setSessionHeaderContainer(final IsWidget widget) {
        setWidgetForPanel(sessionHeaderContainer, widget);
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
        return this;
    }

    @Override
    public void showSessionHeaderContainer(final int sessionHeaderHeight) {
        this.sessionHeaderHeight = sessionHeaderHeight;
        sessionHeaderContainer.getElement().getStyle().setDisplay(BLOCK);
        onResize();
    }

    @Override
    public void hideSessionHeaderContainer() {
        this.sessionHeaderHeight = 0;
        sessionHeaderContainer.getElement().getStyle().setDisplay(NONE);
        onResize();
    }

    @Override
    public SessionPresenter.View showWarning(final String message) {
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

            notification.run();
        }
    }

    @Override
    public SessionPresenterView showMessage(final String message) {
        return this;
    }

    @Override
    public void onResize() {
        palettePanel.getElement().getStyle().setTop(paletteInitialTop + sessionContainer.getElement().getScrollTop() + sessionHeaderHeight,
                                                    Style.Unit.PX);
        palettePanel.getElement().getStyle().setLeft(paletteInitialLeft + sessionContainer.getElement().getScrollLeft(),
                                                     Style.Unit.PX);

        sessionHeaderContainer.getElement().getStyle().setTop(headerInitialTop + sessionContainer.getElement().getScrollTop(),
                                                              Style.Unit.PX);
        sessionHeaderContainer.getElement().getStyle().setLeft(headerInitialLeft + sessionContainer.getElement().getScrollLeft(),
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
        toolbarPanel.clear();
        toolbarPanel.removeFromParent();
        canvasPanel.clear();
        canvasPanel.removeFromParent();
        palettePanel.clear();
        palettePanel.removeFromParent();
        sessionHeaderContainer.clear();
        sessionHeaderContainer.removeFromParent();
        sessionContainer.clear();
        sessionContainer.removeFromParent();
        this.removeFromParent();
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

}
