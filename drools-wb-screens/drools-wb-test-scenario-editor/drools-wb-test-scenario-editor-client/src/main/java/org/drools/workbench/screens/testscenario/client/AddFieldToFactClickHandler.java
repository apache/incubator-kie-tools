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

import com.google.gwt.event.logical.shared.SelectionEvent;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.Fact;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;

public class AddFieldToFactClickHandler
        extends AddFieldClickHandler {


    private final Fact fact;

    public AddFieldToFactClickHandler(Fact fact,
                                      PackageDataModelOracle dmo,
                                      ScenarioParentWidget parent) {
        super(dmo, parent);
        this.fact = fact;
    }

    @Override
    public void onSelection(SelectionEvent<String> stringSelectionEvent) {
        fact.getFieldData().add(new FieldPlaceHolder(stringSelectionEvent.getSelectedItem()));
        parent.renderEditor();
    }

    protected FactFieldSelector createFactFieldSelector() {
        FactFieldSelector factFieldSelector = new FactFieldSelector();
        for (String fieldName : dmo.getFieldCompletions(fact.getType())) {
            if (!fact.isFieldNameInUse(fieldName)) {
                factFieldSelector.addField(fieldName);
            }
        }
        return factFieldSelector;
    }
}
