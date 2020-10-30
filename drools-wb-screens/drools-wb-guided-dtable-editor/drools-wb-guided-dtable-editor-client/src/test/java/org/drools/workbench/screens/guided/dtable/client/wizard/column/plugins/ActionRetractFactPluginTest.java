/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionRetractFactCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternToDeletePage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ActionRetractFactPluginTest {

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private PatternToDeletePage patternToDeletePage;

    @Mock
    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    private GuidedDecisionTable52 model;

    @Mock
    private TranslationService translationService;

    @Mock
    private ActionRetractFactCol52 editingCol;

    @Mock
    private LimitedEntryActionRetractFactCol52 limitedEntryActionRetractFactCol52;

    @InjectMocks
    private ActionRetractFactPlugin plugin = spy(new ActionRetractFactPlugin(patternToDeletePage,
                                                                             additionalInfoPage,
                                                                             changeEvent,
                                                                             translationService));

    @Before
    public void setUp() throws Exception {
        model = new GuidedDecisionTable52();
    }

    @Test
    public void testInit() throws Exception {
        model.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(wizard).getPresenter();

        plugin.init(wizard);

        verify(plugin).setupDefaultValues();
    }

    @Test
    public void testGetPagesWhenTableFormatIsLimitedEntry() throws Exception {
        model.setTableFormat(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        doReturn(model).when(presenter).getModel();

        final List<WizardPage> pages = plugin.getPages();
        final boolean hasPatternToDeletePage = pages.stream().anyMatch(a -> a instanceof PatternToDeletePage);

        assertTrue(hasPatternToDeletePage);
        assertEquals(2,
                     pages.size());
    }

    @Test
    public void testGetPagesWhenTableFormatIsNotLimitedEntry() throws Exception {
        model.setTableFormat(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        doReturn(model).when(presenter).getModel();

        final List<WizardPage> pages = plugin.getPages();
        final boolean hasPatternToDeletePage = pages.stream().anyMatch(a -> a instanceof PatternToDeletePage);

        assertFalse(hasPatternToDeletePage);
        assertEquals(1,
                     pages.size());
    }

    @Test
    public void testGenerateColumnWhenColumnIsNew() throws Exception {
        final ActionRetractFactCol52 expectedColumn = mock(ActionRetractFactCol52.class);

        doReturn(true).when(plugin).isNewColumn();
        doReturn(expectedColumn).when(plugin).editingCol();

        assertTrue(plugin.generateColumn());

        verify(presenter).appendColumn(expectedColumn);
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNew() throws Exception {
        final ActionRetractFactCol52 editingCol = mock(ActionRetractFactCol52.class);
        final ActionRetractFactCol52 originalCol = mock(ActionRetractFactCol52.class);

        doReturn(false).when(plugin).isNewColumn();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(originalCol).when(plugin).originalCol();

        assertTrue(plugin.generateColumn());

        verify(presenter).updateColumn(originalCol,
                                       editingCol);
    }

    @Test
    public void testGenerateColumnWhenColumnIsNotNewAndVetoed() throws Exception {
        doReturn(false).when(plugin).isNewColumn();
        doThrow(VetoException.class).when(presenter).updateColumn(Mockito.<ActionCol52>any(),
                                                                  Mockito.<ActionCol52>any());

        assertFalse(plugin.generateColumn());

        verify(wizard).showGenericVetoError();
    }

    @Test
    public void testSetHeader() throws Exception {
        final String header = "Header";
        doReturn(editingCol).when(plugin).editingCol();

        plugin.setHeader(header);

        verify(editingCol).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGetEditingColStringValue() throws Exception {
        final DTCellValue52 cellValue52 = mock(DTCellValue52.class);

        doReturn(cellValue52).when(limitedEntryActionRetractFactCol52).getValue();
        doReturn(limitedEntryActionRetractFactCol52).when(plugin).editingCol();

        plugin.getEditingColStringValue();

        verify(cellValue52).getStringValue();
    }

    @Test
    public void testSetEditingColStringValue() throws Exception {
        final DTCellValue52 cellValue52 = mock(DTCellValue52.class);
        final String pattern = "pattern";

        doReturn(cellValue52).when(limitedEntryActionRetractFactCol52).getValue();
        doReturn(limitedEntryActionRetractFactCol52).when(plugin).editingCol();

        plugin.setEditingColStringValue(pattern);

        verify(cellValue52).setStringValue(pattern);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsCompleted() throws Exception {
        doReturn(true).when(plugin).isPatternToDeletePageCompleted();

        plugin.setPatternToDeletePageAsCompleted();

        verify(plugin,
               never()).setPatternToDeletePageCompleted(Boolean.TRUE);
        verify(plugin,
               never()).fireChangeEvent(patternToDeletePage);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsNotCompleted() throws Exception {
        doReturn(false).when(plugin).isPatternToDeletePageCompleted();

        plugin.setPatternToDeletePageAsCompleted();

        verify(plugin).setPatternToDeletePageCompleted(Boolean.TRUE);
        verify(plugin).fireChangeEvent(patternToDeletePage);
    }

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.ActionRetractFactPlugin_DeleteAnExistingFact;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testGetHeader() {
        plugin.getHeader();

        verify(editingCol).getHeader();
    }

    @Test
    public void testGetAlreadyUsedColumnNames() throws Exception {
        model.getActionCols().add(actionCol52("a"));
        model.getActionCols().add(actionCol52("b"));
        when(presenter.getModel()).thenReturn(model);

        assertEquals(2,
                     plugin.getAlreadyUsedColumnHeaders().size());
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("a"));
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("b"));
    }

    @Test
    public void testSetupDefaultValuesColumnIsNew() throws Exception {
        final ActionRetractFactCol52 newColumn = mock(ActionRetractFactCol52.class);

        doReturn(true).when(plugin).isNewColumn();
        doReturn(newColumn).when(plugin).newColumn();
        doReturn(newColumn).when(plugin).clone(newColumn);

        plugin.setupDefaultValues();

        assertEquals(plugin.editingCol(),
                     newColumn);
    }

    @Test
    public void testSetupDefaultValuesColumnIsNotNew() throws Exception {
        final ActionRetractFactCol52 originalCol = mock(ActionRetractFactCol52.class);

        doReturn(false).when(plugin).isNewColumn();
        doReturn(originalCol).when(plugin).originalCol();
        doReturn(originalCol).when(plugin).clone(originalCol);

        plugin.setupDefaultValues();

        assertEquals(plugin.editingCol(),
                     originalCol);
    }

    private ActionCol52 actionCol52(final String header) {
        return new ActionCol52() {{
            setHeader(header);
        }};
    }
}
