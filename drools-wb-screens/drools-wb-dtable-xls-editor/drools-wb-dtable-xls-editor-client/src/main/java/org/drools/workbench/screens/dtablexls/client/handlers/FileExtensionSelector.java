/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.dtablexls.client.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Radio;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class FileExtensionSelector
        extends VerticalPanel {

    private static final String FILE_TYPE = "FILE_TYPE";
    private final Map<String, ResourceTypeDefinition> resourceTypes = new HashMap<String, ResourceTypeDefinition>();
    private final List<Radio> radioButtonList = new ArrayList<Radio>();

    public FileExtensionSelector( final ResourceTypeDefinition... resourceTypes ) {

        setResourceTypes( resourceTypes );
        addRadioButtons();
    }

    private void addRadioButtons() {
        boolean first = true;
        for ( String resourceSuffix : this.resourceTypes.keySet() ) {
            Radio radioButton = new Radio( FILE_TYPE );
            radioButton.setText( resourceSuffix );

            radioButton.setValue( first );
            first = false;

            radioButtonList.add( radioButton );
            add( radioButton );
        }
    }

    private void setResourceTypes( final ResourceTypeDefinition[] resourceTypes ) {
        for ( ResourceTypeDefinition resourceType : resourceTypes ) {
            this.resourceTypes.put( resourceType.getSuffix(),
                                    resourceType );
        }
    }

    public ResourceTypeDefinition getResourceType() {
        for ( Radio radioButton : radioButtonList ) {
            if ( radioButton.getValue() ) {
                return this.resourceTypes.get( radioButton.getText() );
            }
        }
        return null;
    }
}
