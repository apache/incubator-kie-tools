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
package org.drools.guvnor.client.workbench;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class WorkbenchPart extends Composite
    implements
    RequiresResize,
    ProvidesResize {

    private String      title;

    private Widget      widget;

    private SimplePanel container = new SimplePanel();

    public WorkbenchPart() {
        initWidget( container );
    }

    public WorkbenchPart(final Widget widget,
                         final String title) {
        this();
        this.title = title;
        this.widget = widget;
        container.setWidget( widget );
    }

    public String getPartTitle() {
        return title;
    }

    public Widget getPartWidget() {
        return widget;
    }

    @Override
    public void onResize() {
        final Widget w = container.getWidget();
        if ( w instanceof RequiresResize ) {
            ((RequiresResize) w).onResize();
        }
    }

}
