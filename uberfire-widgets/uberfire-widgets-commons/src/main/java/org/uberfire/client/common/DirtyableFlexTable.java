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
import org.kie.commons.data.Pair;

public class DirtyableFlexTable
        extends FlexTable
        implements DirtyableContainer {

    private int length;
    private List<Pair<Integer, Integer>> list = new ArrayList<Pair<Integer, Integer>>();

    public boolean hasDirty() {
        for ( final Pair<Integer, Integer> coord : list ) {
            final Widget element = getWidget( coord.getK1(), coord.getK2() );
            if ( ( element instanceof DirtyableWidget && ( (DirtyableWidget) element ).isDirty() ) ||
                    ( element instanceof DirtyableContainer && ( (DirtyableContainer) element ).hasDirty() ) ) {
                return true;
            }
        }
        return false;
    }

    public void setWidget( int row,
                           int column,
                           Widget arg2 ) {
        super.setWidget( row, column, arg2 );

        if ( arg2 instanceof IDirtyable ) {
            list.add( length++, Pair.newPair( row, column ) );
        }
    }

    public void setHorizontalAlignmentForFlexCellFormatter( int row,
                                                            int column,
                                                            HorizontalAlignmentConstant horizontalAlignmentConstant ) {
        getFlexCellFormatter().setHorizontalAlignment( row, column, horizontalAlignmentConstant );
    }
}
