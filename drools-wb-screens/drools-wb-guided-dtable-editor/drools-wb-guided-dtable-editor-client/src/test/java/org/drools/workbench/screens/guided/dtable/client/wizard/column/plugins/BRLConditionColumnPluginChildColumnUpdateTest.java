/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.DefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;

import static org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class BRLConditionColumnPluginChildColumnUpdateTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    private BRLConditionColumnPlugin plugin;

    @Before
    public void setup() {

        plugin = spy(new BRLConditionColumnPlugin(mock(RuleModellerPage.class),
                                                  mock(DefaultValuesPage.class),
                                                  mock(AdditionalInfoPage.class),
                                                  mock(EventSourceMock.class),
                                                  mock(TranslationService.class)));
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(model).when(presenter).getModel();
    }

    @Test
    public void testChildColumnDefaultWhenNoTemplateKeys() throws Exception {
        initPlugin(getBrlConditionColumnWithNoTemplateValues());

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        assertNotNull(plugin.editingCol().getChildColumns().get(0).getDefaultValue());
    }

    @Test
    public void testChildColumnDefaultValuesValuesAdded() throws Exception {
        initPlugin(getBrlConditionColumnWithTemplateValues());

        plugin.editingCol().getChildColumns().get(0).setDefaultValue(new DTCellValue52("Hello"));
        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        assertEquals("Hello", plugin.editingCol().getChildColumns().get(0).getDefaultValue().getStringValue());
    }

    private void initPlugin(final BRLConditionColumn brlConditionColumn) {
        plugin.setOriginalColumnConfig52(brlConditionColumn);
        final NewGuidedDecisionTableColumnWizard wizard = new NewGuidedDecisionTableColumnWizard(mock(WizardView.class),
                                                                                                 mock(SummaryPage.class),
                                                                                                 mock(TranslationService.class),
                                                                                                 mock(DecisionTablePopoverUtils.class));

        doReturn(EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(mock(EventBus.class)).when(presenter).getEventBus();
        wizard.init(presenter);
        plugin.init(wizard);
    }

    private BRLConditionColumn getBrlConditionColumnWithNoTemplateValues() {
        final BRLConditionColumn brlConditionColumn = new BRLConditionColumn();

        final FactPattern factPattern = new FactPattern();
        final SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setValue("value");
        constraint.setFieldType("fieldType");
        constraint.setFactType("factType");
        constraint.setFieldName("fieldName");
        constraint.setOperator("in");
        constraint.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        factPattern.addConstraint(constraint);
        brlConditionColumn.getDefinition().add(factPattern);
        final BRLConditionVariableColumn variableColumn = new BRLConditionVariableColumn("", BRLActionVariableColumn.FIELD_VAR_NAME);
        variableColumn.setDefaultValue(new DTCellValue52("test"));
        brlConditionColumn.getChildColumns().add(variableColumn);
        return brlConditionColumn;
    }

    private BRLConditionColumn getBrlConditionColumnWithTemplateValues() {
        final BRLConditionColumn brlConditionColumn = new BRLConditionColumn();

        final FactPattern factPattern = new FactPattern();
        final SingleFieldConstraint constraint = new SingleFieldConstraint();
        constraint.setValue("$default");
        constraint.setFieldType("fieldType");
        constraint.setFactType("factType");
        constraint.setFieldName("fieldName");
        constraint.setOperator("in");
        constraint.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        factPattern.addConstraint(constraint);
        brlConditionColumn.getDefinition().add(factPattern);
        brlConditionColumn.getChildColumns().add(new BRLConditionVariableColumn("$default", BRLActionVariableColumn.FIELD_VAR_NAME));
        return brlConditionColumn;
    }
}
