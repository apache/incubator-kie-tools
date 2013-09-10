package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;

class AddFieldToFactDataClickHandler
        extends AddFieldClickHandler {


    private final FixtureList definitionList;

    AddFieldToFactDataClickHandler(FixtureList definitionList,
                                   PackageDataModelOracle dmo,
                                   ScenarioParentWidget parent) {
        super(dmo, parent);
        this.definitionList = definitionList;
    }


    @Override
    public void onSelection(SelectionEvent<String> stringSelectionEvent) {
        for (Fixture fixture : definitionList) {
            if (fixture instanceof FactData) {
                ((FactData) fixture).getFieldData().add(
                        new FieldPlaceHolder(stringSelectionEvent.getSelectedItem()));
            }
        }
    }

    protected FactFieldSelector createFactFieldSelector() {
        FactFieldSelector factFieldSelector = new FactFieldSelector();
        for (String fieldName : dmo.getFieldCompletions(definitionList.getFirstFactData().getType())) {
            if (!definitionList.isFieldNameInUse(fieldName)) {
                factFieldSelector.addField(fieldName);
            }
        }
        return factFieldSelector;
    }
}
