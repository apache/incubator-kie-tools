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

package org.uberfire.ext.properties.editor.client.fields;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class ComboField extends AbstractField {

    @Inject
    Event<PropertyEditorChangeEvent> propertyEditorChangeEventEvent;

    @Override
    public Widget widget( final PropertyEditorFieldInfo property ) {
        final PropertyEditorComboBox listBox = GWT.create( PropertyEditorComboBox.class );
        int index = 0;
        int selected = -1;
        for ( String value : property.getComboValues() ) {
            listBox.addItem( value );
            selected = searchSelectItem( property, index, selected, value );
            index++;
        }
        ifSelectedSelectItem( listBox, index, selected );

        addChangeHandler( property, listBox );

        return listBox;
    }

    private void ifSelectedSelectItem( PropertyEditorComboBox listBox,
                                       int index,
                                       int selected ) {
        if ( selectAnyItem( index ) ) {
            listBox.setSelectedIndex( selected );
        }
    }

    private void addChangeHandler( final PropertyEditorFieldInfo property,
                                   final PropertyEditorComboBox listBox ) {
        listBox.addChangeHandler( new ChangeHandler() {
                                      @Override
                                      public void onChange( ChangeEvent event ) {
                                          int selectedIndex = listBox.getSelectedIndex();
                                          if ( validate( property, listBox.getItemText( selectedIndex ) ) ) {
                                              listBox.clearOldValidationErrors();
                                              property.setCurrentStringValue( listBox.getItemText( selectedIndex ) );
                                              propertyEditorChangeEventEvent.fire( new PropertyEditorChangeEvent( property, listBox.getItemText( selectedIndex ) ) );
                                          } else {
                                              listBox.setValidationError( getValidatorErrorMessage( property, listBox.getItemText( selectedIndex ) ) );
                                              listBox.setSelectItemByText( property.getCurrentStringValue() );
                                          }
                                      }
                                  }

                                );
    }

    private int searchSelectItem( PropertyEditorFieldInfo property,
                                  int index,
                                  int selected,
                                  String value ) {
        if ( value.equalsIgnoreCase( property.getCurrentStringValue() ) ) {
            selected = index;
        }
        return selected;
    }

    private boolean selectAnyItem( int index ) {
        return index >= 0;
    }

}
