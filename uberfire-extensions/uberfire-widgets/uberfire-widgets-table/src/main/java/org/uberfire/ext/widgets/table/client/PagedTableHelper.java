/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.table.client;

import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants;

public class PagedTableHelper {

    public static void setSelectedValue( final ListBox listbox,
                                         final String value ) {

        for ( int i = 0; i < listbox.getItemCount(); i++ ) {
            if ( listbox.getValue( i ).equals( value ) ) {
                listbox.setSelectedIndex( i );
                return;
            }
        }
    }

    public static void setSelectIndexOnPageSizesSelector( int minPageSize, int maxPageSize, int incPageSize,
                                                          ListBox pageSizesSelector, int pageSize ) {
        for ( int i = minPageSize; i <= maxPageSize; i = i + incPageSize ) {
            pageSizesSelector
                    .addItem( String.valueOf( i ) + " " + CommonConstants.INSTANCE.Items(), String.valueOf( i ) );
            if ( i == pageSize ) {
                for ( int z = 0; z < pageSizesSelector.getItemCount(); z++ ) {
                    if ( pageSizesSelector.getValue( z ).equals( String.valueOf( i ) ) ) {
                        pageSizesSelector.setSelectedIndex( z );
                        break;
                    }
                }
            }
        }
    }
}
