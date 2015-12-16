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

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import org.drools.workbench.models.datamodel.oracle.MethodInfo;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;

class AddFieldToFactDataClickHandler
        extends AddFieldClickHandler {

    private final FixtureList definitionList;

    AddFieldToFactDataClickHandler(final FixtureList definitionList,
                                   final AsyncPackageDataModelOracle oracle,
                                   final ScenarioParentWidget parent) {
        super(oracle,
              parent);
        this.definitionList = definitionList;
    }

    @Override
    public void onSelection(final SelectionEvent<String> stringSelectionEvent) {
        for (Fixture fixture : definitionList) {
            if (fixture instanceof FactData) {
                ((FactData) fixture).getFieldData().add(
                        new FieldPlaceHolder(stringSelectionEvent.getSelectedItem()));
            }
        }
    }

    protected FactFieldSelector createFactFieldSelector() {
        final FactFieldSelector factFieldSelector = new FactFieldSelector();

        // Add fields
        oracle.getFieldCompletions(definitionList.getFirstFactData().getType(),
                                   new Callback<ModelField[]>() {
                                       @Override
                                       public void callback(final ModelField[] fields) {
                                           for (ModelField field : fields) {
                                               final String fieldName = field.getName();
                                               if (!definitionList.isFieldNameInUse(fieldName)) {
                                                   factFieldSelector.addField(fieldName);
                                               }
                                           }
                                       }
                                   });

        return factFieldSelector;
    }
}
