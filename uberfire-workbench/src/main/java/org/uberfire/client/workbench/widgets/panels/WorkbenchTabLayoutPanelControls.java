/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.workbench.widgets.panels;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.commons.util.Preconditions;

/**
 * Workbench Tab Layout Panel Controls; i.e. the widgets to minimize\maximize
 */
public class WorkbenchTabLayoutPanelControls extends HorizontalPanel {

    private final FocusPanel maximize = new FocusPanel();

    private final FocusPanel minimize = new FocusPanel();

    public WorkbenchTabLayoutPanelControls() {
        setStyleName( "tabBarControlsContainer" );
        maximize.setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        maximize.setStyleName( "tabBarControlMaximize" );
        minimize.setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
        minimize.setStyleName( "tabBarControlMinimize" );
        add( maximize );
        add( minimize );
    }

    public HandlerRegistration addMinimizeClickHandler( final ClickHandler handler ) {
        Preconditions.checkNotNull( "handler",
                                    handler );
        return minimize.addClickHandler( handler );
    }

    public HandlerRegistration addMaximizeClickHandler( final ClickHandler handler ) {
        Preconditions.checkNotNull( "handler",
                                    handler );
        return maximize.addClickHandler( handler );
    }

}
