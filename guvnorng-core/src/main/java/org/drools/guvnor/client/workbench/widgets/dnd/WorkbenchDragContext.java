/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench.widgets.dnd;

import org.drools.guvnor.client.workbench.widgets.panels.tabpanel.WorkbenchTabPanel;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchDragContext {

    private final String            title;

    private final Widget            widget;

    private final WorkbenchTabPanel origin;

    public WorkbenchDragContext(final String title,
                                final Widget widget,
                                final WorkbenchTabPanel origin) {
        this.title = title;
        this.widget = widget;
        this.origin = origin;
    }

    public String getTitle() {
        return title;
    }

    public Widget getWidget() {
        return widget;
    }

    public WorkbenchTabPanel getOrigin() {
        return origin;
    }

}
