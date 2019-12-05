/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popover;

import java.util.Optional;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.PopoverOptions;

public abstract class AbstractPopoverView implements PopoverView {

    public static final String TOP = "top";
    public static final String LEFT = "left";
    public static final String POSITION = "position";
    public static final String PX = "px";
    public static final String ABSOLUTE = "absolute";
    public static final String TITLE = "title";

    @DataField("popover")
    protected Div popoverElement;

    @DataField("popover-container")
    protected Div popoverContainerElement;

    @DataField("popover-content")
    protected Div popoverContentElement;

    protected JQueryProducer.JQuery<Popover> jQueryPopover;

    protected Popover popover;

    protected ElementWrapperWidget<?> wrappedWidget;

    protected PopoverOptions options = new PopoverOptions();

    public AbstractPopoverView() {
        //CDI proxy
    }

    public AbstractPopoverView(final Div popoverElement,
                               final Div popoverContainerElement,
                               final Div popoverContentElement,
                               final JQueryProducer.JQuery<Popover> jQueryPopover) {
        this.popoverElement = popoverElement;
        this.popoverContainerElement = popoverContainerElement;
        this.popoverContentElement = popoverContentElement;
        this.jQueryPopover = jQueryPopover;
        options.setContent(element -> popoverContentElement);
        options.setAnimation(false);
        options.setHtml(true);
    }

    @Override
    public void setup(final Optional<String> editorTitle, final int mx, final int my, final Position position) {
        if (isShown()) {
            hide();
        }
        final HTMLElement element = this.getElement();
        popover = jQueryPopover.wrap(element);
        wrappedWidget = ElementWrapperWidget.getWidget(element);
        addWidgetToRootPanel();
        editorTitle.ifPresent(t -> popoverElement.setAttribute(TITLE, t));
        popoverElement.getStyle().setProperty(TOP, my + PX);
        popoverElement.getStyle().setProperty(LEFT, mx + PX);
        popoverElement.getStyle().setProperty(POSITION, ABSOLUTE);
        options.setPlacement(position.toString().toLowerCase());
        popover.popover(options);
    }

    @Override
    public void show() {
        scheduleTask();
    }

    @Override
    public boolean isShown() {
        return wrappedWidget != null && RootPanel.get().getWidgetIndex(wrappedWidget) != -1;
    }

    @Override
    public void hide() {
        if (isShown()) {
            removeWidgetFromRootPanel();
            popover.destroy();
            wrappedWidget = null;
        }
    }

    /**
     * Retrieve the actual height of the <code>ErrorReportPopover</code>
     * @return
     */
    public int getActualHeight() {
        return wrappedWidget != null ? wrappedWidget.getElement().getScrollHeight() : 0;
    }

    //indirection for tests
    protected void addWidgetToRootPanel() {
        RootPanel.get().add(wrappedWidget);
    }

    //indirection for tests
    protected void removeWidgetFromRootPanel() {
        RootPanel.get().remove(wrappedWidget);
    }

    //indirection for tests
    protected void scheduleTask() {
        Scheduler.get().scheduleDeferred(() -> popover.show());
    }
}
