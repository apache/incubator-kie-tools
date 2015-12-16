/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.widget.EnumDropDownUtilities;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;

/**
 * A drop down for enumerated values
 */
public class EnumDropDown
        extends SimplePanel {

    private final DropDownValueChanged valueChangedCommand;
    private final Path resource;

    private final ListBox listBox;

    private EnumDropDownUtilities utilities = new EnumDropDownUtilities() {
        @Override
        protected int addItems( final ListBox listBox ) {
            return 0;
        }

        @Override
        protected void selectItem( final ListBox listBox ) {
            final int itemCount = listBox.getItemCount();
            listBox.setEnabled( itemCount > 0 );
            if ( itemCount > 0 ) {
                listBox.setSelectedIndex( 0 );

                //Schedule notification after GWT has finished tying everything together as not all
                //Event Handlers have been set-up by consumers of this class at Construction time
                Scheduler.get().scheduleFinally( new ScheduledCommand() {

                    @Override
                    public void execute() {
                        valueChangedCommand.valueChanged( encodeSelectedItems(),
                                                          encodeSelectedItems() );
                    }

                } );
            }
        }
    };
    private TextBox textBox = new TextBox();

    public EnumDropDown( final String currentValue,
                         final DropDownValueChanged valueChanged,
                         final DropDownData dropData,
                         final Path resource ) {
        this( currentValue,
              valueChanged,
              dropData,
              false,
              resource );
    }

    public EnumDropDown( final String currentValue,
                         final DropDownValueChanged valueChanged,
                         final DropDownData dropData,
                         final boolean multipleSelect,
                         final Path resource ) {
        listBox = new ListBox( multipleSelect );
        this.valueChangedCommand = valueChanged;
        this.resource = resource;

        listBox.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                valueChangedCommand.valueChanged( encodeSelectedItems(),
                                                  encodeSelectedItems() );
            }
        } );

        textBox.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                valueChanged.valueChanged( textBox.getValue(),
                                           textBox.getValue() );
            }
        } );
        setDropDownData( currentValue,
                         dropData );
    }

    //Build a comma separated list of values form a multi-select drop-down.
    //org.drools.ide.common.server.util.BRDRLPersistence is blissfully unaware that 
    //the "in" and "not in" operators require a list of values hence it is constructed here.
    String encodeSelectedItems() {
        if ( listBox.getItemCount() == 0 ) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        if ( listBox.isMultipleSelect() ) {
            boolean first = true;
            buffer.append( "( " );
            for ( int i = 0; i < listBox.getItemCount(); i++ ) {
                if ( listBox.isItemSelected( i ) ) {
                    if ( !first ) {
                        buffer.append( "," );
                    }
                    first = false;
                    buffer.append( "\"" );
                    buffer.append( listBox.getValue( i ) );
                    buffer.append( "\"" );
                }
            }
            buffer.append( " )" );
        } else {
            buffer.append( listBox.getValue( listBox.getSelectedIndex() ) );
        }
        return buffer.toString();
    }

    public void setDropDownData( final String currentValue,
                                 final DropDownData dropData ) {
        if ( dropData == null ) {
            textBox.setText( currentValue );
            setEditorWidget( textBox );
        } else {
            utilities.setDropDownData( currentValue,
                                       dropData,
                                       listBox.isMultipleSelect(),
                                       resource,
                                       listBox );
            setEditorWidget( listBox );
        }
    }

    private void setEditorWidget( Widget widget ) {
        clear();
        add( widget );
    }

    public String getValue() {
        if ( getWidget().equals( listBox ) ) {
            if ( listBox.getSelectedIndex() != -1 ) {
                return listBox.getValue( listBox.getSelectedIndex() );
            } else {
                return "";
            }
        } else {
            return textBox.getValue();
        }
    }
}
