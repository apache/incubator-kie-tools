package org.kie.workbench.common.widgets.client.ruleselector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class RuleSelector
        extends Composite
        implements HasValueChangeHandlers<String> {

    private final HorizontalPanel panel = new HorizontalPanel();
    private final InlineLabel ruleNamePanel = new InlineLabel();
    private final static String NONE_SELECTED = CommonConstants.INSTANCE.NoneSelected();


    public RuleSelector(Collection<String> ruleNames, String exclude) {
        this(exclude(ruleNames, exclude));
    }

    public RuleSelector(Collection<String> ruleNames) {

        ruleNamePanel.setText(NONE_SELECTED);

        RuleSelectorDropdown ruleSelectorDropdown = new RuleSelectorDropdown(ruleNames);
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

    private static Collection<String> exclude(Collection<String> ruleNames, String exclude) {
        Collection<String> result = new ArrayList<String>();

        for (String ruleName : ruleNames) {
            if (!ruleName.equals(exclude)) {
                result.add(ruleName);
            }
        }

        return result;
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
