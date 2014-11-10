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

import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

public class DirtyableFlexTable
        extends FlexTable
        implements DirtyableContainer {

    public boolean hasDirty() {
        Iterator<Widget> itr = iterator();
        while ( itr.hasNext() ) {
            Widget w = itr.next();
            if ( ( w instanceof DirtyableWidget && ( (DirtyableWidget) w ).isDirty() ) || ( w instanceof DirtyableContainer && ( (DirtyableContainer) w ).hasDirty() ) ) {
                return true;
            }
        }
        return false;
    }

    public void setHorizontalAlignmentForFlexCellFormatter( int row,
                                                            int column,
                                                            HorizontalAlignmentConstant horizontalAlignmentConstant ) {
        getFlexCellFormatter().setHorizontalAlignment( row, column, horizontalAlignmentConstant );
    }
}
