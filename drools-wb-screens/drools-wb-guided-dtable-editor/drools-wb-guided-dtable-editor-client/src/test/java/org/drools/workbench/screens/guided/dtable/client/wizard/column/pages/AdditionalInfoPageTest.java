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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants.ThatColumnNameIsAlreadyInUsePleasePickAnother;
import static org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants.YouMustEnterAColumnHeaderValueDescription;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdditionalInfoPageTest {

    private static final String ENTER_COLUMN_DESCRIPTION = "EnterColumnDescription";

    private static final String PICK_ANOTHER = "PickAnother";

    @Mock
    NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private ConditionCol52 editingCol;

    @Mock
    private AdditionalInfoPage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private TranslationService translationService;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    private GuidedDecisionTable52 model;

    @InjectMocks
    private AdditionalInfoPage<ConditionColumnPlugin> page = spy(new AdditionalInfoPage<>(view,
                                                                                          translationService));

    @BeforeClass
    public static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};
        ApplicationPreferences.setUp(preferences);

        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(page.plugin()).thenReturn(plugin);
        model = new GuidedDecisionTable52();

        when(plugin.getPresenter()).thenReturn(presenter);

        when(translationService.format(YouMustEnterAColumnHeaderValueDescription))
                .thenReturn(ENTER_COLUMN_DESCRIPTION);
        when(translationService.format(ThatColumnNameIsAlreadyInUsePleasePickAnother))
                .thenReturn(PICK_ANOTHER);
    }

    private void createEmptyModel() {
        when(presenter.getModel()).thenReturn(model);
        when(wizard.getPresenter()).thenReturn(presenter);

        doCallRealMethod().when(page).init(any());
        doNothing().when(page).initialise();

        page.init(wizard);
    }

    @Test
    public void testIsCompleteWhenHeaderIsNotEnabled() throws Exception {
        createEmptyModel();
        page.isComplete(Assert::assertFalse);
        verify(view).showWarning(ENTER_COLUMN_DESCRIPTION);
    }

    @Test
    public void testIsCompleteWhenHeaderIsEnabledButNotCompleted() throws Exception {
        createEmptyModel();

        when(plugin.editingCol()).thenReturn(mock(ConditionCol52.class));

        page.enableHeader();

        page.isComplete(Assert::assertFalse);
        verify(view).showWarning(ENTER_COLUMN_DESCRIPTION);
    }

    @Test
    public void testIsCompleteWhenHeaderIsEnabledAndCompleted() throws Exception {
        createEmptyModel();
        when(plugin.getHeader()).thenReturn("header");

        page.enableHeader();

        page.isComplete(Assert::assertTrue);
        verify(view,
               never()).showWarning(ENTER_COLUMN_DESCRIPTION);
        verify(view,
               never()).showWarning(PICK_ANOTHER);
        verify(view).hideWarning();
    }

    @Test
    public void testNonUniqueColumn() throws Exception {
        createEmptyModel();
        Pattern52 parentCondition = new Pattern52();
        ConditionCol52 condition = new ConditionCol52();
        condition.setHeader("header");
        parentCondition.getChildColumns().add(condition);
        model.getConditions().add(parentCondition);

        when(plugin.getHeader()).thenReturn("header");
        when(plugin.isNewColumn()).thenReturn(true);
        doCallRealMethod().when(plugin).getAlreadyUsedColumnHeaders();

        page.enableHeader();

        page.isComplete(Assert::assertFalse);
        verify(view,
               never()).showWarning(ENTER_COLUMN_DESCRIPTION);
        verify(view).showWarning(PICK_ANOTHER);
    }

    @Test
    public void testGetHeader() throws Exception {
        page.getHeader();

        verify(plugin).getHeader();
    }

    @Test
    public void testSetHeader() throws Exception {
        page.setHeader("header");

        verify(plugin).setHeader(eq("header"));
    }

    @Test
    public void testNewHideColumnCheckBox() throws Exception {
        when(plugin.editingCol()).thenReturn(editingCol);
        when(editingCol.isHideColumn()).thenReturn(true);

        final CheckBox checkBox = page.newHideColumnCheckBox();

        verify(editingCol).isHideColumn();

        assertNotNull(checkBox);
    }

    @Test
    public void testSetupHeaderWhenItIsEnabled() throws Exception {
        page.enableHeader();

        page.setupHeader();

        verify(view).showHeader();
    }

    @Test
    public void testSetupHeaderWhenItIsNotEnabled() throws Exception {
        page.setupHeader();

        verify(view,
               never()).showHeader();
    }

    @Test
    public void testSetupHideColumnWhenItIsEnabled() throws Exception {
        when(plugin.editingCol()).thenReturn(editingCol);

        page.enableHideColumn();
        page.setupHideColumn();

        verify(view).showHideColumn(any(CheckBox.class));
    }

    @Test
    public void testSetupHideColumnWhenItIsNotEnabled() throws Exception {
        page.setupHideColumn();

        verify(view,
               never()).showHideColumn(any(CheckBox.class));
    }

    @Test
    public void testSetupLogicallyInsertWhenItIsEnabledAndItShouldBeShown() throws Exception {
        when(plugin.showLogicallyInsert()).thenReturn(true);

        page.enableLogicallyInsert();
        page.setupLogicallyInsert();

        verify(view).showLogicallyInsert(false);
    }

    @Test
    public void testSetupLogicallyInsertWhenItIsEnabledAndItShouldNotBeShown() throws Exception {
        when(plugin.showLogicallyInsert()).thenReturn(false);

        page.enableLogicallyInsert();
        page.setupLogicallyInsert();

        verify(view,
               never()).showLogicallyInsert(false);
    }

    @Test
    public void testSetupLogicallyInsertWhenItIsNotEnabled() throws Exception {
        page.setupLogicallyInsert();

        verify(view,
               never()).showLogicallyInsert(false);
    }

    @Test
    public void testSetupUpdateEngineWithChangesWhenItIsEnabledAndItShouldBeShown() throws Exception {
        when(plugin.showUpdateEngineWithChanges()).thenReturn(true);

        page.enableUpdateEngineWithChanges();
        page.setupUpdateEngineWithChanges();

        verify(view).showUpdateEngineWithChanges(false);
    }

    @Test
    public void testSetupUpdateEngineWithChangesWhenItIsEnabledAndItShouldNotBeShown() throws Exception {
        when(plugin.showUpdateEngineWithChanges()).thenReturn(false);

        page.enableUpdateEngineWithChanges();
        page.setupUpdateEngineWithChanges();

        verify(view,
               never()).showUpdateEngineWithChanges(false);
    }

    @Test
    public void testSetupUpdateEngineWithChangesWhenItIsNotEnabled() throws Exception {
        page.setupUpdateEngineWithChanges();

        verify(view,
               never()).showUpdateEngineWithChanges(false);
    }

    @Test
    public void testSetInsertLogical() throws Exception {
        final Boolean value = Boolean.TRUE;

        page.setInsertLogical(value);

        verify(plugin).setInsertLogical(value);
    }

    @Test
    public void testUpdate() throws Exception {
        final Boolean value = Boolean.TRUE;

        page.setUpdate(value);

        verify(plugin).setUpdate(value);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.AdditionalInfoPage_AdditionalInfo;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).init(page);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }

    @Test
    public void testIsHeaderUniqueWhenColumnIsNewAndHeaderIsUnique() {
        final HashSet<String> headers = set("header1",
                                            "header2");

        when(plugin.getOriginalColumnConfig52()).thenReturn(null);
        when(plugin.isNewColumn()).thenReturn(true);
        when(plugin.getAlreadyUsedColumnHeaders()).thenReturn(headers);
        when(page.getHeader()).thenReturn("header3");

        assertTrue(page.isHeaderUnique());
    }

    @Test
    public void testIsHeaderUniqueWhenColumnIsNewAndHeaderIsNotUnique() {
        final HashSet<String> headers = set("header1",
                                            "header2");

        when(plugin.getOriginalColumnConfig52()).thenReturn(null);
        when(plugin.isNewColumn()).thenReturn(true);
        when(plugin.getAlreadyUsedColumnHeaders()).thenReturn(headers);
        when(page.getHeader()).thenReturn("header2");

        assertFalse(page.isHeaderUnique());
    }

    @Test
    public void testIsHeaderUniqueWhenColumnIsNotNewAndHeaderIsUnique() {
        final HashSet<String> headers = set("header1",
                                            "header2",
                                            "header3");

        when(plugin.getOriginalColumnConfig52()).thenReturn(column("header3"));
        when(plugin.isNewColumn()).thenReturn(false);
        when(plugin.getAlreadyUsedColumnHeaders()).thenReturn(headers);
        when(page.getHeader()).thenReturn("header3");

        assertTrue(page.isHeaderUnique());
    }

    @Test
    public void testIsHeaderUniqueWhenColumnIsNotNewAndHeaderIsNotUnique() {
        final HashSet<String> headers = set("header1",
                                            "header2",
                                            "header3");

        when(plugin.getOriginalColumnConfig52()).thenReturn(column("header3"));
        when(plugin.isNewColumn()).thenReturn(false);
        when(plugin.getAlreadyUsedColumnHeaders()).thenReturn(headers);
        when(page.getHeader()).thenReturn("header2");

        assertFalse(page.isHeaderUnique());
    }

    private DTColumnConfig52 column(final String header) {
        final DTColumnConfig52 dtColumnConfig52 = new DTColumnConfig52();

        dtColumnConfig52.setHeader(header);

        return dtColumnConfig52;
    }

    private HashSet<String> set(String... items) {
        return new HashSet<String>() {{
            for (final String item : items) {
                add(item);
            }
        }};
    }
}
