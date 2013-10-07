package org.drools.workbench.screens.testscenario.client;

import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class FieldNameWidget implements FieldNameWidgetView.Presenter {

    private final FieldNameWidgetView view;

    public FieldNameWidget( final String fieldName,
                            final AsyncPackageDataModelOracle oracle,
                            final FieldNameWidgetView view ) {
        this.view = view;
        this.view.setPresenter( this );
        this.view.setTitle( "fieldName" );
    }

    @Override
    public void onClick() {
        view.openNewFieldSelector();
    }
}
