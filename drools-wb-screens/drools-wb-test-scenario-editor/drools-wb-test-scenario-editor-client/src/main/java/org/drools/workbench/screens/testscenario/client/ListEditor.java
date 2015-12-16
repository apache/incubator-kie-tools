/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.resources.ItemAltedImages;

public class ListEditor extends VerticalPanel {

    public ListEditor( final CollectionFieldData field,
                       final FieldConstraintHelper helper,
                       final ScenarioParentWidget parent ) {
        if ( field.getCollectionFieldList().isEmpty() ) {
            Image image = ItemAltedImages.INSTANCE.NewItem();
            image.setAltText( TestScenarioConstants.INSTANCE.AElementToAddInCollectionList() );
            image.setTitle( TestScenarioConstants.INSTANCE.AElementToAddInCollectionList() );
            image.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent w ) {
                    FieldData fieldData = new FieldData();
                    fieldData.setName( field.getName() );
                    field.getCollectionFieldList().add( fieldData );
                    parent.renderEditor();
                }
            } );

            add( image );
        } else {
            int i = 0;
            for ( final FieldData fieldData : field.getCollectionFieldList() ) {
                add( new ListEditorRow( i, field, fieldData, helper, parent ) );
                i++;
            }
        }
    }

}
