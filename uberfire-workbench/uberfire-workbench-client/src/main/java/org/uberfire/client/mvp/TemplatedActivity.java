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

package org.uberfire.client.mvp;

import org.uberfire.workbench.model.NamedPosition;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;


public interface TemplatedActivity {

    /**
     * Returns the widget that contains the child WorkbenchPanelView at the given position.
     * 
     * @return the widget that contains the child at the given position, or null if the given position does not exist
     *         within this activity's view.
     */
    HasWidgets resolvePosition( NamedPosition p );

    /**
     * Returns the widget that is the root panel of this activity.
     */
    IsWidget getRootWidget();

}
