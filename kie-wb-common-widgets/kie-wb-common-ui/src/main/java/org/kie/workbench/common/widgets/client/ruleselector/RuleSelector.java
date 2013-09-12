package org.kie.workbench.common.widgets.client.ruleselector;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.drools.workbench.models.commons.shared.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class RuleSelector
        extends Composite
        implements HasValueChangeHandlers<String> {

    private final HorizontalPanel panel = new HorizontalPanel();
    private final InlineLabel ruleNamePanel = new InlineLabel();
    private final static String NONE_SELECTED = CommonConstants.INSTANCE.NoneSelected();


    public RuleSelector(ProjectDataModelOracle oracle) {

        ruleNamePanel.setText(NONE_SELECTED);

        RuleSelectorDropdown ruleSelectorDropdown = new RuleSelectorDropdown(oracle);
        ruleSelectorDropdown.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String ruleName = event.getValue();
                if (ruleName.isEmpty()) {
                    ruleNamePanel.setText(NONE_SELECTED);
                } else {
                    ruleNamePanel.setText(ruleName);
                }

                ValueChangeEvent.fire(
                        RuleSelector.this,
                        ruleName);
            }
        });


        panel.add(ruleNamePanel);
        panel.add(ruleSelectorDropdown);

        initWidget(panel);
    }

    public String getRuleName() {
        if (ruleNamePanel.getText() != null && !ruleNamePanel.getText().equals(NONE_SELECTED)) {
            return "";
        } else {
            return ruleNamePanel.getText();
        }
    }

    public void setRuleName(String ruleName) {
        if (ruleName != null && !ruleName.isEmpty()) {
            this.ruleNamePanel.setText(ruleName);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
