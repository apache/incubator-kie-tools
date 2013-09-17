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
package org.uberfire.client.workbench.events;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.mvp.PlaceRequest;

/**
 * An event to change the Title Widget of a WorkbenchPart container, e.g. a Tab in a TabPanel
 */
public class ChangeTitleWidgetEvent {

    private final PlaceRequest place;
    private final String       title;
    private final IsWidget     titleDecoration;

    public ChangeTitleWidgetEvent( final PlaceRequest place,
                                   final String title,
                                   final IsWidget titleDecoration ) {
        this.place = place;
        this.title = title;
        this.titleDecoration = titleDecoration;
    }

    public PlaceRequest getPlaceRequest() {
        return place;
    }

    public String getTitle() {
        return title;
    }

    public IsWidget getTitleDecoration() {
        return titleDecoration;
    }
}
