package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class NewFactWidget implements IsWidget, NewFactWidgetView.Presenter {


    public NewFactWidget(FieldConstraintHelper helper, NewFactWidgetView view) {
        view.setPresenter(this);
        view.setFactName("Address");
    }

    @Override
    public Widget asWidget() {
        return null;  //TODO: -Rikkola-
    }
}
