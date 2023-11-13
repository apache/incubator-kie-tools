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


package org.kie.workbench.common.stunner.client.widgets.views.session;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.widgets.ResizeFlowPanel;

@Dependent
public class ScreenPanelViewImpl implements ScreenPanelView {

    static class SizedResizeFlowPanel extends ResizeFlowPanel {

        @Override
        public void onResize() {
            final Widget parent = getParent();
            if (parent != null) {
                final int w = parent.getOffsetWidth();
                final int h = parent.getOffsetHeight();
                setPixelSize(w, h);
            }
            doSuperOnResize();
        }

        void doSuperOnResize() {
            super.onResize();
        }
    }

    private final ResizeFlowPanel panel;

    public ScreenPanelViewImpl() {
        this(new SizedResizeFlowPanel());
    }

    ScreenPanelViewImpl(final ResizeFlowPanel panel) {
        this.panel = panel;
    }

    @Override
    public ScreenPanelView setWidget(final IsWidget widget) {
        clear();
        panel.add(widget);
        return this;
    }

    @Override
    public ScreenPanelView clear() {
        panel.clear();
        return this;
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
