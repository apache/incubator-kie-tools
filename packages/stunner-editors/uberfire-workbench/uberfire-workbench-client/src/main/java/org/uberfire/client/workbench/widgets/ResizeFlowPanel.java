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

package org.uberfire.client.workbench.widgets;

import javax.enterprise.context.Dependent;

import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.LayoutPanel;
import org.gwtproject.user.client.ui.ProvidesResize;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.Widget;

/**
 * A FlowPanel that can exist in a hierarchy of {@link LayoutPanel}s. Behaves exactly like FlowPanel, but also
 * propagates <tt>onResize</tt> events to the child widgets.
 */
@Dependent
public class ResizeFlowPanel extends FlowPanel implements RequiresResize,
                                                          ProvidesResize {

    @Override
    public void onResize() {
        for (Widget child : this) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }
    }
}