package org.drools.workbench.screens.guided.rule.client.widget;

import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

public class RuleSelectorDropdown
        extends Composite
        implements IsWidget, HasValueChangeHandlers<String> {

    private PackageDataModelOracle dataModel;
    private DropdownButton dropdownButton = new DropdownButton();

    public RuleSelectorDropdown(PackageDataModelOracle dataModel) {
        this.dataModel = dataModel;


        for (final String ruleName : dataModel.getRuleNames()) {
            NavLink label = new NavLink(ruleName);

            label.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ValueChangeEvent.fire(
                            RuleSelectorDropdown.this,
                            ruleName);
                }
            });

            dropdownButton.add(label);
        }

        initWidget(dropdownButton);
    }

    @Override
    public Widget asWidget() {
        return dropdownButton;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
