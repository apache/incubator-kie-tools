/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.docks.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.client.resources.WebAppResource;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.ParameterizedCommand;

public class DockResizeBar extends FlowPanel {

    private static WebAppResource CSS = GWT.create(WebAppResource.class);

    private Element glassElem = null;

    private boolean mouseDown;

    private double offset;

    private ParameterizedCommand<Double> resizeCommand;

    private DocksBar docksBar;

    public DockResizeBar(final DocksBar docksBar) {
        super();
        this.docksBar = docksBar;
        sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONDBLCLICK);
        getElement().addClassName(CSS.CSS().resizableBar());
        setupGlassElement();
        setupMouseHandlers(docksBar);
    }

    public void setup(ParameterizedCommand<Double> resizeCommand) {
        this.resizeCommand = resizeCommand;
    }

    private void setupMouseHandlers(final DocksBar docksBar) {
        addDomHandler(new MouseMoveHandler() {
            @Override
            public void onMouseMove(MouseMoveEvent event) {
                if (mouseDown) {
                    setupMoveIcon();
                    resizeCommand.execute(calculateDockSize(event, docksBar));
                    event.preventDefault();
                }
            }
        }, MouseMoveEvent.getType());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                setupMoveIcon();
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                setupMoveIcon();

                mouseDown = true;
                int width = Math.max(Window.getClientWidth(), Document.get().getScrollWidth());
                int height = Math.max(Window.getClientHeight(), Document.get().getScrollHeight());
                glassElem.getStyle().setHeight(height,
                        Style.Unit.PX);
                glassElem.getStyle().setWidth(width,
                        Style.Unit.PX);
                Document.get().getBody().appendChild(glassElem);

                buildOffset(event);
                Event.setCapture(getElement());
                event.preventDefault();
            }
        }, MouseDownEvent.getType());

        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                getElement().getStyle().setProperty("cursor", "default");
                mouseDown = false;

                glassElem.removeFromParent();

                Event.releaseCapture(getElement());
            }
        }, MouseUpEvent.getType());
    }

    private void setupGlassElement() {
        //This is a special div to prevent select elements in parent widgets during resize
        glassElem = Document.get().createDivElement();
        glassElem.getStyle().setPosition(Style.Position.ABSOLUTE);
        glassElem.getStyle().setTop(0,
                Style.Unit.PX);
        glassElem.getStyle().setLeft(0,
                Style.Unit.PX);
        glassElem.getStyle().setMargin(0,
                Style.Unit.PX);
        glassElem.getStyle().setPadding(0,
                Style.Unit.PX);
        glassElem.getStyle().setBorderWidth(0,
                Style.Unit.PX);
        glassElem.getStyle().setProperty("background",
                "white");
        glassElem.getStyle().setProperty("backgroundColor",
                "red");
        glassElem.getStyle().setOpacity(0.0);
    }

    private void setupMoveIcon() {
        UberfireDockPosition position = docksBar.getPosition();
        if (position == UberfireDockPosition.SOUTH) {
            getElement().getStyle().setProperty("cursor", "ns-resize");
        } else {
            getElement().getStyle().setProperty("cursor", "ew-resize");
        }
    }

    private double calculateDockSize(MouseMoveEvent event, DocksBar docksBar) {
        UberfireDockPosition position = docksBar.getPosition();
        if (position == UberfireDockPosition.WEST) {
            return docksBar.getExpandedBarSize() + (event.getClientX() - getAbsoluteLeft());
        }
        if (position == UberfireDockPosition.EAST) {
            return docksBar.getExpandedBarSize() + (getAbsoluteLeft() - event.getClientX());
        }
        if (position == UberfireDockPosition.SOUTH) {
            return docksBar.getExpandedBarSize() + (getAbsoluteTop() - event.getClientY());
        }
        return docksBar.getExpandedBarSize();
    }

    private void buildOffset(MouseDownEvent event) {
        offset = event.getClientX() - getAbsoluteLeft();
    }


}
