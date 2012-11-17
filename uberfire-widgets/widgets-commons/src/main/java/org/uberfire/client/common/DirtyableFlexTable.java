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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

public class DirtyableFlexTable
        extends FlexTable
        implements DirtyableContainer {
    
    private int length;
    private List<Pair> list = new ArrayList<Pair>();

    public boolean hasDirty() {
        for (Pair coord : list) {
            Widget element = getWidget( coord.getRow(), coord.getColumn() );
            if ((element instanceof DirtyableWidget && ((DirtyableWidget) element).isDirty()) ||
                    (element instanceof DirtyableContainer && ((DirtyableContainer) element).hasDirty()))
                return true;
        }
        return false;
    }
    
    public void setWidget(int row, int column , Widget arg2) {
        super.setWidget( row, column, arg2 );
        
        if (arg2 instanceof IDirtyable) {
            list.add(length++, new Pair(row, column));
        }
    }
    
    public void setHorizontalAlignmentForFlexCellFormatter(int row, int column, HorizontalAlignmentConstant horizontalAlignmentConstant ){
        getFlexCellFormatter().setHorizontalAlignment(row, column, horizontalAlignmentConstant);
    }
}

class Pair {
    private int row;
    private int column;
    
    public Pair(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
}
