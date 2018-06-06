/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collection;
import java.util.Collections;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleMetadata;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.widget.attribute.RuleAttributeWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({GuidedRuleEditorImages508.class, FlexTable.class, DateTimeFormat.class})
public class RuleModellerTest {

    private RuleModel model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private ModellerWidgetFactory widgetFactory;

    @Mock
    private EventBus eventBus;

    @Mock
    private RuleSelector ruleSelector;

    @Mock
    private RuleModellerActionSelectorPopup actionSelectorPopup;

    @Mock
    private RuleModellerActionPlugin actionPlugin;

    @Mock
    private RuleModellerConfiguration ruleModellerConfiguration;

    final Collection<RuleModellerActionPlugin> actionPlugins = Collections.singleton(actionPlugin);

    private RuleModeller ruleModeller;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        GwtMockito.useProviderForType(RuleSelector.class, aClass -> ruleSelector);

        ruleModeller = spy(new RuleModeller(model,
                                            actionPlugins,
                                            oracle,
                                            widgetFactory,
                                            ruleModellerConfiguration,
                                            eventBus,
                                            false));
    }

    @Test
    public void testSetRuleNamesForPackage() throws Exception {
        model.name = "rule 1";
        ruleModeller.setRuleNamesForPackage(Collections.singleton("rule 2"));

        verify(ruleSelector).setRuleNames(Collections.singleton("rule 2"), "rule 1");
    }

    @Test
    public void testShowActionSelector() throws Exception {
        final Integer position = 123;
        doReturn(actionSelectorPopup).when(ruleModeller).ruleModellerActionSelectorPopup(eq(position), eq(actionPlugins));

        ruleModeller.showActionSelector(position);
        verify(actionSelectorPopup).show();
    }

    @Test
    public void testLockLHSPositive() {
        model.addMetadata(new RuleMetadata(RuleAttributeWidget.LOCK_LHS, ""));
        assertTrue(ruleModeller.lockLHS());
    }

    @Test
    public void testLockLHSNegative() {
        assertFalse(ruleModeller.lockLHS());
    }

    @Test
    public void testLockRHSPositive() {
        model.addMetadata(new RuleMetadata(RuleAttributeWidget.LOCK_RHS, ""));
        assertTrue(ruleModeller.lockRHS());
    }

    @Test
    public void testLockRHSNegative() {
        assertFalse(ruleModeller.lockRHS());
    }

    @Test
    public void testRefreshWidgetAfterAttributeAdded() {
        ruleModeller.refreshWidget();

        verify(ruleModeller).initWidget();
    }
}
