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

package org.uberfire.client.workbench.widgets.listbar;

import org.uberfire.client.util.Layouts;

import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A FocusPanel that can exist in a hierarchy of {@link LayoutPanel}s. Behaves exactly like FocusPanel, but also
 * propagates <tt>onResize</tt> events to the child widget.
 */
public class ResizeFocusPanel extends FocusPanel implements RequiresResize, ProvidesResize {

    public ResizeFocusPanel() {
    }

    public ResizeFocusPanel( Widget child ) {
        super( child );
        Layouts.setToFillParent( this );
    }

    @Override
    public void onResize() {
        if ( getWidget() instanceof RequiresResize ) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
