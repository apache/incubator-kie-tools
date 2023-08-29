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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Select.class, Option.class})
public class RuleSelectorTest {

    @GwtMock
    private Select ruleNameSelector;

    @Captor
    private ArgumentCaptor<Option> optionArgumentCaptor;

    private RuleSelector ruleSelector;

    private List<String> ruleNames = new ArrayList<>();

    private class Assertion {

        String expectedText = "";
        String expectedValue = "";

        Assertion(final String expectedText,
                  final String expectedValue) {
            this.expectedText = expectedText;
            this.expectedValue = expectedValue;
        }
    }

    @Before
    public void setup() {
        ruleSelector = spy(new RuleSelector());
    }

    @Test
    public void checkSetup() {
        verify(ruleNameSelector,
               times(1)).setEnabled(eq(false));
        //See implementation for details. It's nice to ensure we keep this "fix"
        verify(ruleNameSelector,
               times(1)).removeStyleName(eq(Styles.FORM_CONTROL));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""));
    }

    @Test
    public void checkRuleNameSelectorWithRules() {
        ruleNames.add("rule1");
        ruleNames.add("rule2");

        ruleSelector.setRuleNames(ruleNames);

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(true));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""),
                               new Assertion("rule1",
                                             "rule1"),
                               new Assertion("rule2",
                                             "rule2"));
    }

    @Test
    public void checkRuleNameSelectorWithFullyQualifiedRules() {
        ruleNames.add("org.kie.rule1");
        ruleNames.add("org.kie.rule2");

        ruleSelector.setRuleNames(ruleNames);

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(true));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""),
                               new Assertion("rule1",
                                             "rule1"),
                               new Assertion("rule2",
                                             "rule2"));
    }

    @Test
    public void checkRuleNameSelectorWithRulesWithExclusion() {
        ruleNames.add("rule1");
        ruleNames.add("rule2");

        ruleSelector.setRuleNames(ruleNames,
                                  "rule1");

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(true));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""),
                               new Assertion("rule2",
                                             "rule2"));
    }

    @Test
    public void checkRuleNameSelectorWithExclusionsWithFullyQualifiedRules() {
        ruleNames.add("org.kie.rule1");
        ruleNames.add("org.kie.rule2");

        ruleSelector.setRuleNames(ruleNames,
                                  "rule1");

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(true));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""),
                               new Assertion("rule2",
                                             "rule2"));
    }

    @Test
    public void checkRuleNameSelectorWithNoRules() {
        ruleSelector.setRuleNames(ruleNames);

        verify(ruleNameSelector,
               atLeast(1)).setEnabled(eq(false));
        verify(ruleNameSelector,
               never()).setEnabled(eq(true));

        verifyRulesAddedToList(new Assertion(RuleSelector.NONE_SELECTED,
                                             ""));
    }

    @Test
    public void checkSetRuleNameWhenEmpty() {
        ruleSelector.setRuleName("");

        verify(ruleNameSelector,
               never()).setValue(anyString());
    }

    @Test
    public void checkSetRuleNameWhenNotEmpty() {
        ruleNames.add("rule1");
        ruleNames.add("rule2");
        ruleSelector.setRuleName("rule1");
        ruleSelector.setRuleNames(ruleNames);

        verify(ruleNameSelector,
               times(1)).setValue(eq("rule1"));
    }

    @Test
    public void checkGetRuleNameWhenNoneSelected() {
        when(ruleNameSelector.getValue()).thenReturn(RuleSelector.NONE_SELECTED);

        assertEquals("",
                     ruleSelector.getRuleName());
    }

    @Test
    public void checkGetRuleNameWhenEmpty() {
        when(ruleNameSelector.getValue()).thenReturn("");

        assertEquals("",
                     ruleSelector.getRuleName());
    }

    @Test
    public void checkGetRuleNameWhenNotEmpty() {
        when(ruleNameSelector.getValue()).thenReturn("rule1");

        assertEquals("rule1",
                     ruleSelector.getRuleName());
    }

    private void verifyRulesAddedToList(final Assertion... assertions) {
        verify(ruleNameSelector,
               times(assertions.length)).add(optionArgumentCaptor.capture());

        final List<Option> options = optionArgumentCaptor.getAllValues();
        assertEquals(assertions.length,
                     options.size());

        final Map<Option, Assertion> checks = new HashMap<>();
        for (int i = 0; i < assertions.length; i++) {
            checks.put(options.get(i),
                       assertions[i]);
        }
        checks.entrySet().forEach((c) -> {
            verify(c.getKey(),
                   times(1)).setText(eq(c.getValue().expectedText));
            verify(c.getKey(),
                   times(1)).setValue(eq(c.getValue().expectedValue));
        });
    }
}
