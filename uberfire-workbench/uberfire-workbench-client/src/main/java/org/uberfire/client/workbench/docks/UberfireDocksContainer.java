/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.docks;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.mvp.Command;

/**
 * An abstraction for DockLayoutPanel used by Uberfire Docks.
 */
@ApplicationScoped
public class UberfireDocksContainer {

    @Inject
    private Event<UberfireDockContainerReadyEvent> event;

    private DockLayoutPanel rootContainer;

    private Command resizeCommand;

    public void setup(DockLayoutPanel rootContainer,
                      Command resizeCommand) {
        this.rootContainer = rootContainer;
        this.resizeCommand = resizeCommand;
        event.fire(new UberfireDockContainerReadyEvent());
    }

    public void add(UberfireDockPosition position,
                    Widget widget,
                    Double size) {
        if (position == UberfireDockPosition.SOUTH) {
            rootContainer.addSouth(widget,
                                   size);
        } else if (position == UberfireDockPosition.EAST) {
            rootContainer.addEast(widget,
                                  size);
        } else if (position == UberfireDockPosition.WEST) {
            rootContainer.addWest(widget,
                                  size);
        }
    }

    public void addBreadcrumbs(IsElement isElement,
                               Double size) {
        rootContainer.addNorth(ElementWrapperWidget.getWidget(isElement.getElement()),
                               size);
    }

    /**
     * @deprecated You should use Errai UI version of
     * this method {@link #show(IsElement isElement)}
     */
    @Deprecated
    public void show(Widget widget) {
        rootContainer.setWidgetHidden(widget,
                                      false);
    }

    public void show(IsElement isElement) {
        rootContainer.setWidgetHidden(ElementWrapperWidget.getWidget(isElement.getElement()),
                                      false);
    }

    /**
     * @deprecated You should use Errai UI version of
     * this method {@link #hide(IsElement isElement)}
     */
    @Deprecated
    public void hide(Widget widget) {
        rootContainer.setWidgetHidden(widget,
                                      true);
    }

    public void hide(IsElement isElement) {
        rootContainer.setWidgetHidden(ElementWrapperWidget.getWidget(isElement.getElement()),
                                      true);
    }

    public void setWidgetSize(Widget widget,
                              double size) {
        rootContainer.setWidgetSize(widget,
                                    size);
    }

    public void resize() {
        resizeCommand.execute();
    }

    public int getOffsetHeight() {
        return rootContainer.getOffsetHeight();
    }

    public int getOffsetWidth() {
        return rootContainer.getOffsetWidth();
    }

    public int getClientWidth() {
        return rootContainer.getElement().getClientWidth();
    }

    public boolean isReady() {
        return rootContainer != null;
    }
}
