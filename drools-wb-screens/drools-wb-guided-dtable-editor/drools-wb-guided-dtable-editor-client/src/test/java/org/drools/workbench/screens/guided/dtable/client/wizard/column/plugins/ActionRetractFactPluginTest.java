/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionRetractFactCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternToDeletePage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ActionRetractFactPluginTest {

    @Mock
    private PatternToDeletePage patternToDeletePage;

    @Mock
    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
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

    @Test
    public void testInit() throws Exception {
        final NewGuidedDecisionTableColumnWizard wizard = mock(NewGuidedDecisionTableColumnWizard.class);

        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(presenter).when(wizard).getPresenter();

        plugin.init(wizard);

        verify(plugin).setupDefaultValues();
    }

    @Test
    public void testGetPagesWhenTableFormatIsLimitedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();

        final List<WizardPage> pages = plugin.getPages();
        final boolean hasPatternToDeletePage = pages.stream().anyMatch(a -> a instanceof PatternToDeletePage);

        assertTrue(hasPatternToDeletePage);
        assertEquals(2,
                     pages.size());
    }

    @Test
    public void testGetPagesWhenTableFormatIsNotLimitedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();

        final List<WizardPage> pages = plugin.getPages();
        final boolean hasPatternToDeletePage = pages.stream().anyMatch(a -> a instanceof PatternToDeletePage);

        assertFalse(hasPatternToDeletePage);
        assertEquals(1,
                     pages.size());
    }

    @Test
    public void testGenerateColumnWhenItIsNotValid() throws Exception {
        doReturn(false).when(plugin).isValid();

        final Boolean result = plugin.generateColumn();

        assertFalse(result);
        verify(presenter,
               never()).appendColumn(any(ActionCol52.class));
    }

    @Test
    public void testGenerateColumnWhenItIsValid() throws Exception {
        final ActionCol52 expectedColumn = (ActionCol52) plugin.editingCol();

        doReturn(true).when(plugin).isValid();

        final Boolean result = plugin.generateColumn();

        assertTrue(result);
        verify(presenter).appendColumn(expectedColumn);
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
    public void testUniqueWhenItIsUnique() throws Exception {
        final ArrayList<ActionCol52> actionCols = new ArrayList<ActionCol52>() {{
            add(actionCol52("Header1"));
        }};

        doReturn(actionCols).when(model).getActionCols();
        doReturn(model).when(presenter).getModel();

        boolean result = plugin.unique("Header2");

        assertTrue(result);
    }

    @Test
    public void testUniqueWhenItIsNotUnique() throws Exception {
        final ArrayList<ActionCol52> actionCols = new ArrayList<ActionCol52>() {{
            add(actionCol52("Header1"));
        }};

        doReturn(actionCols).when(model).getActionCols();
        doReturn(model).when(presenter).getModel();

        boolean result = plugin.unique("Header1");

        assertFalse(result);
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
    public void testIsValidWhenHeaderIsNull() throws Exception {
        final String errorMessage = "YouMustEnterAColumnHeaderValueDescription";

        doReturn(errorMessage).when(translationService).format(GuidedDecisionTableErraiConstants.ActionRetractFactPlugin_YouMustEnterAColumnHeaderValueDescription);
        doReturn(null).when(editingCol).getHeader();
        doReturn(editingCol).when(plugin).editingCol();

        final boolean success = plugin.isValid();

        assertFalse(success);
        verify(plugin).showError(errorMessage);
    }

    @Test
    public void testIsValidWhenHeaderNotUnique() throws Exception {
        final String errorMessage = "ThatColumnNameIsAlreadyInUsePleasePickAnother";
        final String header = "Header";

        doReturn(errorMessage).when(translationService).format(GuidedDecisionTableErraiConstants.ActionRetractFactPlugin_ThatColumnNameIsAlreadyInUsePleasePickAnother);
        doReturn(header).when(editingCol).getHeader();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(false).when(plugin).unique(header);

        final boolean success = plugin.isValid();

        assertFalse(success);
        verify(plugin).showError(errorMessage);
    }

    @Test
    public void testIsValidWhenItIsValid() throws Exception {
        final String header = "Header";

        doReturn(header).when(editingCol).getHeader();
        doReturn(editingCol).when(plugin).editingCol();
        doReturn(true).when(plugin).unique(header);

        final boolean success = plugin.isValid();

        assertTrue(success);
        verify(plugin,
               never()).showError(any());
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

    private ActionCol52 actionCol52(final String header) {
        return new ActionCol52() {{
            setHeader(header);
        }};
    }
}
