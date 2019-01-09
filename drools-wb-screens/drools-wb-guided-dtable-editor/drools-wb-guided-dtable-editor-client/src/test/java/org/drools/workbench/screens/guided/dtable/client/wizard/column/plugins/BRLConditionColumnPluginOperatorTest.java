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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class BRLConditionColumnPluginOperatorTest {

    @Captor
    private ArgumentCaptor<List<BRLConditionVariableColumn>> listArgumentCaptor;
    @Mock
    private RuleModellerPage ruleModellerPage;
    @Mock
    private AdditionalInfoPage additionalInfoPage;
    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;
    @Mock
    private GuidedDecisionTableView.Presenter presenter;
    @Mock
    private GuidedDecisionTable52 model;
    @Mock
    private TranslationService translationService;
    @Mock
    private BRLConditionColumn editingCol;

    @InjectMocks
    private BRLConditionColumnPlugin plugin = spy(new BRLConditionColumnPlugin(ruleModellerPage,
                                                                               additionalInfoPage,
                                                                               changeEvent,
                                                                               translationService) {
        @Override
        public RuleModel getRuleModel() {
            final RuleModel ruleModel = new RuleModel();
            ruleModel.lhs = new IPattern[1];
            final FactPattern factPattern = new FactPattern();
            final SingleFieldConstraint constraint = new SingleFieldConstraint();
            constraint.setValue("value");
            constraint.setFieldType("fieldType");
            constraint.setFactType("factType");
            constraint.setFieldName("fieldName");
            constraint.setOperator("in");
            constraint.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);

            factPattern.addConstraint(constraint);
            ruleModel.lhs[0] = factPattern;
            return ruleModel;
        }
    });

    @Before
    public void setup() {
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(model).when(presenter).getModel();
    }

    @Test
    public void getDefinedVariables() {

        doReturn(true).when(plugin).isNewColumn();
        doReturn(model).when(presenter).getModel();
        doReturn("header").when(editingCol).getHeader();

        plugin.generateColumn();

        verify(editingCol).setChildColumns(listArgumentCaptor.capture());
        final List<BRLConditionVariableColumn> value = listArgumentCaptor.getValue();
        assertEquals(1, value.size());

        final BRLConditionVariableColumn brlConditionVariableColumn = value.get(0);

        assertEquals("fieldType", brlConditionVariableColumn.getFieldType());
        assertEquals("value", brlConditionVariableColumn.getVarName());
        assertEquals("fieldName", brlConditionVariableColumn.getFactField());
        assertEquals("in", brlConditionVariableColumn.getOperator());
    }

    @Test
    public void cloneBRLConditionColumn() {
        final BRLConditionColumn original = new BRLConditionColumn();
        original.setHeader("header");
        original.setOperator("==");
        final BRLConditionColumn clone = plugin.clone(original);

        assertEquals("header", clone.getHeader());
        assertEquals("==", clone.getOperator());
    }

    @Test
    public void cloneVariable() {
        final BRLConditionVariableColumn original = new BRLConditionVariableColumn();
        original.setHeader("header");
        original.setOperator("==");
        final BRLConditionVariableColumn clone = plugin.cloneVariable(original);

        assertEquals("header", clone.getHeader());
        assertEquals("==", clone.getOperator());
    }
}
