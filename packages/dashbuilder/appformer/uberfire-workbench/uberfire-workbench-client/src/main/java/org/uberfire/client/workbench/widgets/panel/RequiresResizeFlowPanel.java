/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench.widgets.panel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class RequiresResizeFlowPanel
        extends FlowPanel
        implements RequiresResize {

    @Override
    public void onResize() {
        for (int i = 0; i < getWidgetCount(); i++) {
            final Widget activeWidget = getWidget(i);
            if (activeWidget instanceof RequiresResize) {
                ((RequiresResize) activeWidget).onResize();
            }
        }
    }
}
