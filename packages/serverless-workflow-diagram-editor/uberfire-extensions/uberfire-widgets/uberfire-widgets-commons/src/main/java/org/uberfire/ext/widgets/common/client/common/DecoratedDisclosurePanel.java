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
package org.uberfire.ext.widgets.common.client.common;

import java.util.Iterator;

import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;
import org.gwtproject.event.logical.shared.HasCloseHandlers;
import org.gwtproject.event.logical.shared.HasOpenHandlers;
import org.gwtproject.event.logical.shared.OpenEvent;
import org.gwtproject.event.logical.shared.OpenHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.DisclosurePanel;
import org.gwtproject.user.client.ui.HasWidgets;
import org.gwtproject.user.client.ui.Widget;

/**
 * Disclosure panel with rounded corners in header.
 * <p>
 * Using this class we don't need to set the header and event handlers for the header everywhere we use DisclosurePanels.
 */
public class DecoratedDisclosurePanel extends Composite
        implements
        HasWidgets,
        HasOpenHandlers<DisclosurePanel>,
        HasCloseHandlers<DisclosurePanel> {

    private final DisclosurePanel widget = new DisclosurePanel();

    private LazyStackPanelHeader header;

    public DecoratedDisclosurePanel(String headerText,
                                    ImageResource headerIcon) {
        widget.setAnimationEnabled(true);
        widget.setHeader(createHeader(headerText,
                                      headerIcon));
        initWidget(widget);
    }

    public DecoratedDisclosurePanel(String headerText) {
        widget.setAnimationEnabled(true);
        widget.setHeader(createHeader(headerText));
        initWidget(widget);
    }

    private LazyStackPanelHeader createHeader(String headerText) {
        header = new LazyStackPanelHeader(headerText);
        setupEventHandlers();
        return header;
    }

    private LazyStackPanelHeader createHeader(String headerText,
                                              ImageResource headerIcon) {
        header = new LazyStackPanelHeader(headerText,
                                          headerIcon);
        setupEventHandlers();
        return header;
    }

    private void setupEventHandlers() {
        widget.addOpenHandler(new OpenHandler<DisclosurePanel>() {
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                header.expand();
            }
        });
        widget.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            public void onClose(CloseEvent<DisclosurePanel> event) {
                header.collapse();
            }
        });
    }

    public void add(Widget w) {
        widget.add(w);
    }

    public void setContent(Widget content) {
        widget.setContent(content);
    }

    public HandlerRegistration addOpenHandler(OpenHandler<DisclosurePanel> openHandler) {
        return widget.addOpenHandler(openHandler);
    }

    public HandlerRegistration addCloseHandler(CloseHandler<DisclosurePanel> handler) {
        return widget.addCloseHandler(handler);
    }

    public void clear() {
        widget.clear();
    }

    public Iterator<Widget> iterator() {
        return widget.iterator();
    }

    public boolean remove(Widget w) {
        return widget.remove(w);
    }

    public boolean isOpen() {
        return widget.isOpen();
    }

    public void setOpen(boolean b) {
        widget.setOpen(b);
    }
}
