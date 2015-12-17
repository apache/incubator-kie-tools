/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.widgets.client.ruleselector;

import java.util.ArrayList;
import java.util.Collection;

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
    private final RuleSelectorDropdown ruleSelectorDropdown = new RuleSelectorDropdown();

    private final static String NONE_SELECTED = CommonConstants.INSTANCE.NoneSelected();

    public RuleSelector() {
        ruleNamePanel.setText( NONE_SELECTED );

        ruleSelectorDropdown.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                String ruleName = event.getValue();
                if ( ruleName.isEmpty() ) {
                    ruleNamePanel.setText( NONE_SELECTED );
                } else {
                    ruleNamePanel.setText( ruleName );
                }

                ValueChangeEvent.fire( RuleSelector.this,
                                       ruleName );
            }
        } );

        panel.add( ruleNamePanel );
        panel.add( ruleSelectorDropdown );

        initWidget( panel );
        getElement().setAttribute( "data-uf-lock", "true" );
    }

    public void setRuleNames( final Collection<String> ruleNames,
                              final String exclude ) {
        ruleSelectorDropdown.setRuleNames( exclude( ruleNames, exclude ) );
    }

    public void setRuleNames( final Collection<String> ruleNames ) {
        ruleSelectorDropdown.setRuleNames( ruleNames );
    }

    private static Collection<String> exclude( final Collection<String> ruleNames,
                                               final String exclude ) {
        final Collection<String> result = new ArrayList<String>();
        for ( String ruleName : ruleNames ) {
            if ( !ruleName.equals( exclude ) ) {
                result.add( ruleName );
            }
        }
        return result;
    }

    public String getRuleName() {
        if ( ruleNamePanel.getText() != null && !ruleNamePanel.getText().equals( NONE_SELECTED ) ) {
            return "";
        } else {
            return ruleNamePanel.getText();
        }
    }

    public void setRuleName( final String ruleName ) {
        if ( ruleName != null && !ruleName.isEmpty() ) {
            this.ruleNamePanel.setText( ruleName );
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }
}
