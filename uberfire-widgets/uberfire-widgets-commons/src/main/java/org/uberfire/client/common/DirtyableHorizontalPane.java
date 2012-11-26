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

package org.uberfire.client.common;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirtyableHorizontalPane extends HorizontalPanel implements DirtyableContainer {

    public boolean hasDirty() {
        int widNumber = getWidgetCount();
        Widget element;
        
        for ( int i = 0; i < widNumber; i++ ) {
            element = getWidget(i);
            if ((element instanceof DirtyableWidget && ((DirtyableWidget) element).isDirty()) || 
                    (element instanceof DirtyableContainer && ((DirtyableContainer) element).hasDirty()))
                return true;
        }
        return false;
    }
}
