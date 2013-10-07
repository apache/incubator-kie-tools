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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.FactAssignmentField;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;

public class FieldSelectorWidget
        implements IsWidget,
                   ClickHandler {

    private final Field field;
    private final FieldConstraintHelper helper;
    private final ScenarioParentWidget parent;
    private final Image clickMe;

    public FieldSelectorWidget( final Field field,
                                final FieldConstraintHelper helper,
                                final ScenarioParentWidget parent ) {
        this.field = field;
        this.helper = helper;
        this.parent = parent;
        this.clickMe = CommonAltedImages.INSTANCE.Edit();
        this.clickMe.addClickHandler( this );
    }

    @Override
    public Widget asWidget() {
        return clickMe;
    }

    @Override
    public void onClick( final ClickEvent event ) {
        TypeChoiceFormPopup typeChoiceForm = new TypeChoiceFormPopup( helper );
        typeChoiceForm.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> selectionEvent ) {
                helper.replaceFieldWith( createField( selectionEvent ) );

                parent.renderEditor();
            }
        } );

        typeChoiceForm.show();
    }

    private Field createField( final SelectionEvent<Integer> selectionEvent ) {
        if ( selectionEvent.getSelectedItem() == FieldData.TYPE_FACT ) {
            return new FactAssignmentField( field.getName(), helper.getFieldType() );
        } else {
            if ( selectionEvent.getSelectedItem() == FieldData.TYPE_COLLECTION ) {
                CollectionFieldData collectionFieldData = new CollectionFieldData();
                collectionFieldData.setName( field.getName() );
                return collectionFieldData;
            } else {
                FieldData fieldData = new FieldData( field.getName(), "" );
                fieldData.setNature( selectionEvent.getSelectedItem() );
                return fieldData;
            }
        }
    }

}
