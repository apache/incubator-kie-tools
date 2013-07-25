package org.drools.workbench.screens.guided.rule.client.widget;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

public class RuleSelectorDropdown
        implements IsWidget {

    private PackageDataModelOracle dataModel;

    public RuleSelectorDropdown(PackageDataModelOracle dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public Widget asWidget() {
        DropdownButton widgets = new DropdownButton();

        for (String ruleName: dataModel.getRuleNames()){
            widgets.add(new Label(ruleName));
        }

        return widgets;
    }
}
