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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.Arrays;
import java.util.HashSet;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.screens.guided.rule.service.EnumDropdownService;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.util.ConstraintValueHelper;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.DropDownValueChanged;
import org.uberfire.client.common.IDirtyable;

/**
 * A drop down for enumerated values
 */
public class EnumDropDown
        extends ListBox
        implements IDirtyable {

    private final DropDownValueChanged valueChangedCommand;

    public EnumDropDown( final String currentValue,
                         final DropDownValueChanged valueChanged,
                         final DropDownData dropData ) {
        this( currentValue,
              valueChanged,
              dropData,
              false );

    }

    public EnumDropDown( final String currentValue,
                         final DropDownValueChanged valueChanged,
                         final DropDownData dropData,
                         boolean multipleSelect ) {
        super( multipleSelect );
        this.valueChangedCommand = valueChanged;

        addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                valueChangedCommand.valueChanged( encodeSelectedItems(),
                                                  encodeSelectedItems() );
            }
        } );

        setDropDownData( currentValue,
                         dropData );
    }

    //Build a comma separated list of values form a multi-select drop-down.
    //org.drools.ide.common.server.util.BRDRLPersistence is blissfully unaware that 
    //the "in" and "not in" operators require a list of values hence it is constructed here.
    String encodeSelectedItems() {
        if ( getItemCount() == 0 ) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        if ( isMultipleSelect() ) {
            boolean first = true;
            buffer.append( "( " );
            for ( int i = 0; i < getItemCount(); i++ ) {
                if ( isItemSelected( i ) ) {
                    if ( !first ) {
                        buffer.append( "," );
                    }
                    first = false;
                    buffer.append( "\"" );
                    buffer.append( getValue( i ) );
                    buffer.append( "\"" );
                }
            }
            buffer.append( " )" );
        } else {
            buffer.append( getValue( getSelectedIndex() ) );
        }
        return buffer.toString();
    }

    public void setDropDownData( final String currentValue,
                                 final DropDownData dropData ) {

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

                            fillDropDown( currentValue, response );
                        }
                    }, EnumDropdownService.class ).loadDropDownExpression( dropData.getValuePairs(),
                                                                               dropData.getQueryExpression() );
                }
            } );

        } else {
            //otherwise its just a normal one...
            fillDropDown( currentValue,
                          dropData );
        }

    }

    private void fillDropDown( final String currentValue,
                               final DropDownData dropData ) {
        if ( dropData == null ) {
            fillDropDown( currentValue,
                          new String[ 0 ] );
        } else {
            fillDropDown( currentValue,
                          dropData.getFixedList() );
        }
    }

    private void fillDropDown( final String currentValue,
                               final String[] enumeratedValues ) {
        clear();

        boolean selected = false;
        HashSet<String> currentValues = new HashSet<String>();
        String trimmedCurrentValue = currentValue;
        if ( isMultipleSelect() && trimmedCurrentValue != null ) {
            trimmedCurrentValue = currentValue.replace( "\"",
                                                        "" );
            trimmedCurrentValue = trimmedCurrentValue.replace( "(",
                                                               "" );
            trimmedCurrentValue = trimmedCurrentValue.replace( ")",
                                                               "" );
            trimmedCurrentValue = trimmedCurrentValue.trim();
            if ( trimmedCurrentValue.indexOf( "," ) > 0 ) {
                currentValues.addAll( Arrays.asList( trimmedCurrentValue.split( "," ) ) );
            }
        } else {
            currentValues.add( currentValue );
        }

        for ( int i = 0; i < enumeratedValues.length; i++ ) {
            String v = enumeratedValues[ i ];
            String val;
            if ( v.indexOf( '=' ) > 0 ) {
                //using a mapping
                String[] splut = ConstraintValueHelper.splitValue(v);
                String realValue = splut[ 0 ];
                String display = splut[ 1 ];
                val = realValue;
                addItem( display,
                         realValue );
            } else {
                addItem( v );
                val = v;
            }
            if ( currentValue != null && currentValues.contains( val ) ) {
                setItemSelected( i,
                                 true );
                selected = true;
            }
        }

        if ( !selected ) {
            final int itemCount = getItemCount();
            setEnabled( itemCount > 0 );
            if ( itemCount > 0 ) {
                setSelectedIndex( 0 );

                //Schedule notification after GWT has finished tying everything together as not all 
                //Event Handlers have been set-up by consumers of this class at Construction time
                Scheduler.get().scheduleFinally( new ScheduledCommand() {

                    @Override
                    public void execute() {
                        valueChangedCommand.valueChanged( getItemText( 0 ),
                                                          getValue( 0 ) );
                    }

                } );

            }
        }
    }
}
