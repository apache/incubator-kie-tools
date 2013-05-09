package org.kie.guvnor.testscenario.client;

import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;

public class FieldNameWidget implements FieldNameWidgetView.Presenter {

    private final FieldNameWidgetView view;


    public FieldNameWidget(String fieldName,
                           PackageDataModelOracle dmo,
                           FieldNameWidgetView view) {
        this.view = view;
        this.view.setPresenter(this);
        this.view.setTitle("fieldName");
    }

    @Override
    public void onClick() {
        view.openNewFieldSelector();
    }
}
