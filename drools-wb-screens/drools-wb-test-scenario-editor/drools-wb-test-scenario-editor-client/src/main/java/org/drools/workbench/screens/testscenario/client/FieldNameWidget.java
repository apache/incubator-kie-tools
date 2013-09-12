package org.drools.workbench.screens.testscenario.client;

import org.drools.workbench.models.commons.shared.oracle.PackageDataModelOracle;

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
