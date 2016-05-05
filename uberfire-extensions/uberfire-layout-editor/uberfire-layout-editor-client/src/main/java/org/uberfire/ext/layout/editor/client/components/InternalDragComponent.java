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
package org.uberfire.ext.layout.editor.client.components;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Super class of the layout editor's internal components
 */
public abstract class InternalDragComponent implements LayoutDragComponent {

    public static final String INTERNAL_DRAG_COMPONENT = "INTERNAL_DRAG_COMPONENT";

    @Override
    public IsWidget getPreviewWidget(RenderingContext container) {
        return new FlowPanel();
    }

    @Override
    public IsWidget getShowWidget(RenderingContext container) {
        // Internal components does not have show widgets as
        // they are only intended for layout control & behaviour.
        return null;
    }
}
