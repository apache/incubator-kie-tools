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
