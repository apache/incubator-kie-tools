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

import java.util.Collections;

import com.google.gwt.event.shared.EventBus;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.MockInstanceImpl;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.DefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mocks.EventSourceMock;

import static org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(BRLRuleModel.class)
public class BRLActionColumnPluginChildColumnUpdateTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private TranslationService translationService;

    private BRLActionColumnPlugin plugin;

    @Before
    public void setUp() throws Exception {
        plugin = new BRLActionColumnPlugin(mock(RuleModellerPage.class),
                                           mock(DefaultValuesPage.class),
                                           new MockInstanceImpl<>(Collections.singletonList(mock(RuleModellerActionPlugin.class))),
                                           mock(AdditionalInfoPage.class),
                                           mock(EventSourceMock.class),
                                           mock(TranslationService.class));
    }

    @Test
    public void testChildColumnDefaultWhenNoTemplateKeys() throws Exception {
        initPlugin(getBrlActionColumnWithNoTemplateValues());

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        assertNotNull(plugin.editingCol().getChildColumns().get(0).getDefaultValue());
    }

    @Test
    public void testChildColumnDefaultValues() throws Exception {
        initPlugin(getBrlActionColumnWithTemplateValues());

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        assertNull(plugin.editingCol().getChildColumns().get(0).getDefaultValue());
        assertNull(plugin.editingCol().getChildColumns().get(1).getDefaultValue());
    }

    @Test
    public void testChildColumnDefaultValuesValuesAdded() throws Exception {
        initPlugin(getBrlActionColumnWithTemplateValues());

        plugin.editingCol().getChildColumns().get(0).setDefaultValue(new DTCellValue52("Hello"));
        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        assertEquals("Hello", plugin.editingCol().getChildColumns().get(0).getDefaultValue().getStringValue());
        assertNull(plugin.editingCol().getChildColumns().get(1).getDefaultValue());
    }

    private void initPlugin(final BRLActionColumn brlActionColumn) {
        plugin.setOriginalColumnConfig52(brlActionColumn);
        final NewGuidedDecisionTableColumnWizard wizard = new NewGuidedDecisionTableColumnWizard(mock(WizardView.class),
                                                                                                 mock(SummaryPage.class),
                                                                                                 translationService,
                                                                                                 mock(DecisionTablePopoverUtils.class));

        doReturn(EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(mock(EventBus.class)).when(presenter).getEventBus();
        wizard.init(presenter);
        plugin.init(wizard);
    }

    private BRLActionColumn getBrlActionColumnWithTemplateValues() {
        final BRLActionColumn brlActionColumn = new BRLActionColumn();

        final ActionInsertFact actionInsertFact = new ActionInsertFact("Person");
        actionInsertFact.setBoundName("$a");
        final ActionFieldValue afv1 = new ActionFieldValue("name",
                                                           "$default",
                                                           DataType.TYPE_STRING);
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        actionInsertFact.addFieldValue(afv1);
        final ActionFieldValue afv2 = new ActionFieldValue("age",
                                                           "$default1",
                                                           DataType.TYPE_STRING);
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        actionInsertFact.addFieldValue(afv2);
        brlActionColumn.getDefinition().add(actionInsertFact);
        brlActionColumn.getChildColumns().add(new BRLActionVariableColumn("$default", BRLActionVariableColumn.FIELD_VAR_NAME));
        brlActionColumn.getChildColumns().add(new BRLActionVariableColumn("$default1", BRLActionVariableColumn.FIELD_VAR_NAME));
        return brlActionColumn;
    }

    private BRLActionColumn getBrlActionColumnWithNoTemplateValues() {
        final BRLActionColumn brlActionColumn = new BRLActionColumn();

        final ActionInsertFact actionInsertFact = new ActionInsertFact("Person");
        actionInsertFact.setBoundName("$a");
        final ActionFieldValue afv1 = new ActionFieldValue("name",
                                                           "Toni",
                                                           DataType.TYPE_STRING);
        afv1.setNature(FieldNatureType.TYPE_LITERAL);
        actionInsertFact.addFieldValue(afv1);
        final ActionFieldValue afv2 = new ActionFieldValue("age",
                                                           "12",
                                                           DataType.TYPE_STRING);
        afv2.setNature(FieldNatureType.TYPE_LITERAL);
        actionInsertFact.addFieldValue(afv2);
        brlActionColumn.getDefinition().add(actionInsertFact);
        final BRLActionVariableColumn variableColumn = new BRLActionVariableColumn("", BRLActionVariableColumn.FIELD_VAR_NAME);
        variableColumn.setDefaultValue(new DTCellValue52("test"));
        brlActionColumn.getChildColumns().add(variableColumn);
        return brlActionColumn;
    }
}
