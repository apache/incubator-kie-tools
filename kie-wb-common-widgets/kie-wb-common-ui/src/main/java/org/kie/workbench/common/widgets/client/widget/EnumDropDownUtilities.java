/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.client.widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.util.ConstraintValueHelper;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

/**
 * Utilities to populate a ListBox with DropDownData
 */
public class EnumDropDownUtilities {

    /**
     * Populate ListBox with values from DropDownData
     * @param value
     * @param dropData
     * @param isMultipleSelect
     * @param resource
     * @param listBox
     */
    public void setDropDownData( final String value,
                                 final DropDownData dropData,
                                 final boolean isMultipleSelect,
                                 final Path resource,
                                 final ListBox listBox ) {

        //if we have to do it lazy, we will hit up the server when the widget gets focus
        if ( dropData != null && dropData.getFixedList() == null && dropData.getQueryExpression() != null ) {
            Scheduler.get().scheduleDeferred( new Command() {
                public void execute() {
                    BusyPopup.showMessage( CommonConstants.INSTANCE.RefreshingList() );

                    MessageBuilder.createCall( new RemoteCallback<String[]>() {
                                                   public void callback( String[] response ) {
                                                       BusyPopup.close();

                                                       if ( response.length == 0 ) {
                                                           response = new String[]{ CommonConstants.INSTANCE.UnableToLoadList() };
                                                       }

                                                       fillDropDown( value,
                                                                     response,
                                                                     isMultipleSelect,
                                                                     listBox );
                                                   }
                                               },
                                               new BusErrorCallback() {
                                                   @Override
                                                   public boolean error( Message message,
                                                                         Throwable throwable ) {
                                                       BusyPopup.close();
                                                       return false;
                                                   }
                                               },
                                               EnumDropdownService.class
                                             ).loadDropDownExpression( resource,
                                                                       dropData.getValuePairs(),
                                                                       dropData.getQueryExpression() );
                }
            } );

        } else {
            //otherwise its just a normal one...
            fillDropDown( value,
                          dropData,
                          isMultipleSelect,
                          listBox );
        }

    }

    private void fillDropDown( final String value,
                               final DropDownData dropData,
                               final boolean isMultipleSelect,
                               final ListBox listBox ) {
        if ( dropData == null ) {
            fillDropDown( value,
                          new String[ 0 ],
                          isMultipleSelect,
                          listBox );
        } else {
            fillDropDown( value,
                          dropData.getFixedList(),
                          isMultipleSelect,
                          listBox );
        }
    }

    private void fillDropDown( final String value,
                               final String[] enumeratedValues,
                               final boolean isMultipleSelect,
                               final ListBox listBox ) {

        listBox.clear();

        int selectedIndexOffset = addItems( listBox );

        boolean selected = false;
        Set<String> currentValues = new HashSet<String>();
        String currentValue = value;
        String trimmedCurrentValue = currentValue;
        if ( isMultipleSelect && trimmedCurrentValue != null ) {
            trimmedCurrentValue = currentValue.replace( "\"",
                                                        "" );
            trimmedCurrentValue = trimmedCurrentValue.replace( "(",
                                                               "" );
            trimmedCurrentValue = trimmedCurrentValue.replace( ")",
                                                               "" );
            for ( String val : Arrays.asList( trimmedCurrentValue.split( "," ) ) ) {
                currentValues.add( val.trim() );
            }
        } else {
            currentValues.add( currentValue );
        }

        for ( int i = 0; i < enumeratedValues.length; i++ ) {
            String v = enumeratedValues[ i ];
            String val;
            if ( v.indexOf( '=' ) > 0 ) {
                //using a mapping
                String[] splut = ConstraintValueHelper.splitValue( v );
                String realValue = splut[ 0 ];
                String display = splut[ 1 ];
                val = realValue;
                listBox.addItem( display,
                                 realValue );
            } else {
                listBox.addItem( v );
                val = v;
            }
            if ( currentValue != null && currentValues.contains( val ) ) {
                listBox.setItemSelected( i + selectedIndexOffset,
                                         true );
                selected = true;
            }
        }

        if ( !selected ) {
            selectItem( listBox );
        }
    }

    protected int addItems( final ListBox listBox ) {
        //We don't add any supplementary items to the list by default, other than those in the DropDownData
        return 0;
    }

    protected void selectItem( final ListBox listBox ) {
        //We don't support the selection of items by default
    }

}
