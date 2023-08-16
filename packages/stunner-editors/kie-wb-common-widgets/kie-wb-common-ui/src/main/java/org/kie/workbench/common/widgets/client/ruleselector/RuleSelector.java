/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.widgets.client.ruleselector;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public class RuleSelector
        extends Composite
        implements HasValueChangeHandlers<String>,
                   HasEnabled {

    public final static String NONE_SELECTED = CommonConstants.INSTANCE.LineNoneLine();

    // It's possible for the rule name to be set before the asynchronous call to retrieve all available rule names
    // has completed and the selector's list of rule names set. Therefore, if the rule name is set before the complete
    // list, record a pending "set" and invoke after the list has been set.
    private boolean ruleNamesLoaded = false;
    private Optional<Command> onRulesLoadedCommand = Optional.empty();

    private Select ruleNameSelector = GWT.create(Select.class);

    public RuleSelector() {
        ruleNameSelector.setEnabled(false);
        ruleNameSelector.setLiveSearch(true);
        ruleNameSelector.setLiveSearchPlaceholder(CommonConstants.INSTANCE.Choose());
        //The form-control CSS is added by the SelectBase constructor but interferes with PatternFly's CSS.. so removing!
        ruleNameSelector.removeStyleName(Styles.FORM_CONTROL);

        ruleNameSelector.add(makeRuleNameOption(NONE_SELECTED,
                                                ""));

        initWidget(ruleNameSelector);
    }

    public void setRuleNames(final Collection<String> ruleNames,
                             final String exclude) {
        setRuleNames(exclude(ruleNames,
                             exclude));
    }

    public void setRuleNames(final Collection<String> ruleNames) {
        if (!(ruleNames == null || ruleNames.isEmpty())) {
            ruleNamesLoaded = true;
            ruleNameSelector.setEnabled(true);
            ruleNames.forEach((r) -> ruleNameSelector.add(makeRuleNameOption(getSimpleRuleName(r))));
            onRulesLoadedCommand.ifPresent(Command::execute);
        } else {
            ruleNameSelector.setEnabled(false);
        }
        ruleNameSelector.refresh();
    }

    Option makeRuleNameOption(final String ruleName) {
        return makeRuleNameOption(ruleName,
                                  ruleName);
    }

    Option makeRuleNameOption(final String ruleName,
                              final String value) {
        final Option o = GWT.create(Option.class);
        final String simpleRuleName = getSimpleRuleName(ruleName);
        o.setText(simpleRuleName);
        o.setValue(value);
        return o;
    }

    String getSimpleRuleName(final String ruleName) {
        if (ruleName.contains(".")) {
            return ruleName.substring(ruleName.lastIndexOf(".") + 1);
        }
        return ruleName;
    }

    private Collection<String> exclude(final Collection<String> ruleNames,
                                       final String exclude) {
        return ruleNames
                .stream()
                .map(this::getSimpleRuleName)
                .filter((r) -> !r.equals(exclude)).collect(Collectors.toList());
    }

    public String getRuleName() {
        final String ruleName = ruleNameSelector.getValue();
        if (!(ruleName == null || ruleName.equals(NONE_SELECTED))) {
            return ruleName;
        } else {
            return "";
        }
    }

    public void setRuleName(final String ruleName) {
        if (ruleName != null && !ruleName.isEmpty()) {
            if (ruleNamesLoaded) {
                ruleNameSelector.setValue(ruleName);
            } else {
                onRulesLoadedCommand = Optional.of(() -> setRuleName(ruleName));
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return ruleNameSelector.addValueChangeHandler(handler);
    }

    @Override
    public boolean isEnabled() {
        return ruleNameSelector.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        ruleNameSelector.setEnabled(enabled);
    }
}
