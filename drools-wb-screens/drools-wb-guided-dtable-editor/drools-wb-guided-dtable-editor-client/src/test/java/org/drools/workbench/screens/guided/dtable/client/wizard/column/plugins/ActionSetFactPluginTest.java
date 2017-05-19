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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionInsertFactWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionSetFactWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DefaultWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.LimitedWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({DefaultWidgetFactory.class, LimitedWidgetFactory.class})
public class ActionSetFactPluginTest {

    @Mock
    private PatternPage patternPage;

    @Mock
    private FieldPage fieldPage;

    @Mock
    private ValueOptionsPage<ActionSetFactPlugin> valueOptionsPage;

    @Mock
    private AdditionalInfoPage<ActionSetFactPlugin> additionalInfoPage;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private NewGuidedDecisionTableColumnWizard wizard;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @InjectMocks
    private ActionSetFactPlugin plugin = spy(new ActionSetFactPlugin(patternPage,
                                                                     fieldPage,
                                                                     valueOptionsPage,
                                                                     additionalInfoPage,
                                                                     changeEvent,
                                                                     translationService));

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.ActionInsertFactPlugin_SetTheValueOfAField;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testGetPages() {
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(plugin).tableFormat();

        final List<WizardPage> pages = plugin.getPages();

        assertEquals(4,
                     pages.size());
    }

    @Test
    public void testGenerateColumn() {
        final ActionCol52 actionCol52 = mock(ActionCol52.class);
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionCol52).when(actionWrapper).getActionCol52();
        doReturn(actionWrapper).when(plugin).editingWrapper();

        final Boolean success = plugin.generateColumn();

        assertTrue(success);
        verify(presenter).appendColumn(actionCol52);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsCompleted() throws Exception {
        doReturn(true).when(plugin).isValueOptionsPageCompleted();

        plugin.setValueOptionsPageAsCompleted();

        verify(plugin,
               never()).setValueOptionsPageCompleted();
        verify(plugin,
               never()).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testSetValueOptionsPageAsCompletedWhenItIsNotCompleted() throws Exception {
        doReturn(false).when(plugin).isValueOptionsPageCompleted();

        plugin.setValueOptionsPageAsCompleted();

        verify(plugin).setValueOptionsPageCompleted();
        verify(plugin).fireChangeEvent(valueOptionsPage);
    }

    @Test
    public void testSetEditingPattern() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        when(plugin.editingWrapper()).thenReturn(actionWrapper);
        when(patternWrapper.getFactType()).thenReturn("factType");
        when(patternWrapper.getBoundName()).thenReturn("boundName");

        plugin.setEditingPattern(patternWrapper);

        verify(actionWrapper).setFactField("");
        verify(actionWrapper).setFactType("factType");
        verify(actionWrapper).setBoundName("boundName");
        verify(actionWrapper).setType("");

        verify(plugin).fireChangeEvent(patternPage);
        verify(plugin).fireChangeEvent(fieldPage);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testGetPatterns() {
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final ArrayList<Pattern52> fakePatterns = new ArrayList<Pattern52>() {{
            add(fakePattern("factType",
                            "boundName",
                            true));
        }};

        when(model.getPatterns()).thenReturn(fakePatterns);
        when(presenter.getModel()).thenReturn(model);

        final List<PatternWrapper> patterns = plugin.getPatterns();
        final PatternWrapper firstPattern = patterns.get(0);

        assertNotNull(firstPattern);
        assertEquals("factType",
                     firstPattern.getFactType());
        assertEquals("boundName",
                     firstPattern.getBoundName());
        assertEquals(true,
                     firstPattern.isNegated());
        assertEquals(1,
                     patterns.size());
    }

    @Test
    public void testConstraintValue() {
        final int expectedConstraintValue = BaseSingleFieldConstraint.TYPE_UNDEFINED;
        final int actualConstraintValue = plugin.constraintValue();

        assertEquals(expectedConstraintValue,
                     actualConstraintValue);
    }

    @Test
    public void testGetFactType() {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final String expectedFactType = "factType";

        when(patternWrapper.getFactType()).thenReturn(expectedFactType);
        when(plugin.patternWrapper()).thenReturn(patternWrapper);

        final String actualFactType = plugin.getFactType();

        verify(patternWrapper).getFactType();
        assertEquals(expectedFactType,
                     actualFactType);
    }

    @Test
    public void testGetAccessor() {
        final FieldAccessorsAndMutators expectedAccessor = FieldAccessorsAndMutators.MUTATOR;
        final FieldAccessorsAndMutators actualAccessor = plugin.getAccessor();

        assertEquals(expectedAccessor,
                     actualAccessor);
    }

    @Test
    public void testFilterEnumFields() {
        assertFalse(plugin.filterEnumFields());
    }

    @Test
    public void testGetFactField() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String expectedFactField = "factField";

        when(actionWrapper.getFactField()).thenReturn(expectedFactField);
        when(plugin.editingWrapper()).thenReturn(actionWrapper);

        final String actualFactField = plugin.getFactField();

        verify(actionWrapper).getFactField();
        assertEquals(expectedFactField,
                     actualFactField);
    }

    @Test
    public void testSetFactFieldWhenFactPatternIsNew() {
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final ActionInsertFactWrapper actionWrapper = mock(ActionInsertFactWrapper.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(true).when(plugin).isNewFactPattern();
        doReturn(actionWrapper).when(plugin).newActionInsertFactWrapper();

        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn("type").when(oracle).getFieldType(any(),
                                                   any());

        doReturn("factType").when(patternWrapper).getFactType();
        doReturn("boundName").when(patternWrapper).getBoundName();
        doReturn(patternWrapper).when(plugin).patternWrapper();

        plugin.setFactField("selectedValue");

        verify(actionWrapper).setFactField(eq("selectedValue"));
        verify(actionWrapper).setFactType(eq("factType"));
        verify(actionWrapper).setBoundName(eq("boundName"));
        verify(actionWrapper).setType(eq("type"));
        verify(plugin).fireChangeEvent(fieldPage);
    }

    @Test
    public void testSetFactFieldWhenFactPatternIsNotNew() {
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);
        final Pattern52 pattern = mock(Pattern52.class);

        doReturn(oracle).when(presenter).getDataModelOracle();
        doReturn(presenter).when(plugin).getPresenter();
        doReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY).when(model).getTableFormat();
        doReturn(model).when(presenter).getModel();
        doReturn(pattern).when(model).getConditionPattern(eq("boundName"));
        doReturn("factType").when(pattern).getFactType();
        doReturn("type").when(oracle).getFieldType(eq("factType"),
                                                   eq("selectedValue"));

        final ActionSetFactWrapper actionWrapper = spy(new ActionSetFactWrapper(plugin));
        doReturn(false).when(plugin).isNewFactPattern();
        doReturn(actionWrapper).when(plugin).newActionSetFactWrapper();

        doReturn("factType").when(patternWrapper).getFactType();
        doReturn("boundName").when(patternWrapper).getBoundName();
        doReturn(patternWrapper).when(plugin).patternWrapper();

        plugin.setFactField("selectedValue");

        verify(actionWrapper).setFactField(eq("selectedValue"));
        verify(actionWrapper).setFactType(eq("factType"));
        verify(actionWrapper).setBoundName(eq("boundName"));
        verify(actionWrapper).setType(eq("type"));
        verify(plugin).fireChangeEvent(fieldPage);
    }

    @Test
    public void testEditingPattern() {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(patternWrapper).when(plugin).patternWrapper();

        when(patternWrapper.getFactType()).thenReturn("factType");
        when(patternWrapper.getBoundName()).thenReturn("boundName");
        when(patternWrapper.isNegated()).thenReturn(false);
        when(patternWrapper.getEntryPointName()).thenReturn("entryPoint");

        final Pattern52 pattern52 = plugin.editingPattern();

        assertEquals("factType",
                     pattern52.getFactType());
        assertEquals("boundName",
                     pattern52.getBoundName());
        assertEquals(false,
                     pattern52.isNegated());
        assertEquals("entryPoint",
                     pattern52.getEntryPointName());
    }

    @Test
    public void testEditingCol() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.editingCol();

        verify(actionWrapper).getActionCol52();
    }

    @Test
    public void testGetHeader() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.getHeader();

        verify(actionWrapper).getHeader();
    }

    @Test
    public void testSetHeader() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String header = "header";

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setHeader(header);

        verify(actionWrapper).setHeader(header);
        verify(plugin).fireChangeEvent(additionalInfoPage);
    }

    @Test
    public void testSetInsertLogical() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final boolean insertLogical = false;

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setInsertLogical(insertLogical);

        verify(actionWrapper).setInsertLogical(insertLogical);
    }

    @Test
    public void testSetUpdate() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final boolean update = false;

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setUpdate(update);

        verify(actionWrapper).setUpdate(update);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNew() {
        doReturn(true).when(plugin).isNewFactPattern();

        final boolean showUpdateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertEquals(false,
                     showUpdateEngineWithChanges);
    }

    @Test
    public void testShowUpdateEngineWithChangesWhenFactPatternIsNotNew() {
        doReturn(false).when(plugin).isNewFactPattern();

        final boolean showUpdateEngineWithChanges = plugin.showUpdateEngineWithChanges();

        assertEquals(true,
                     showUpdateEngineWithChanges);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNew() {
        doReturn(true).when(plugin).isNewFactPattern();

        final boolean showLogicallyInsert = plugin.showLogicallyInsert();

        assertEquals(true,
                     showLogicallyInsert);
    }

    @Test
    public void testShowLogicallyInsertWhenFactPatternIsNotNew() {
        doReturn(false).when(plugin).isNewFactPattern();

        final boolean showLogicallyInsert = plugin.showLogicallyInsert();

        assertEquals(false,
                     showLogicallyInsert);
    }

    @Test
    public void testGetValueList() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.getValueList();

        verify(actionWrapper).getValueList();
    }

    @Test
    public void testSetValueList() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String valueList = "valueList";

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setValueList(valueList);

        verify(actionWrapper).setValueList(valueList);
    }

    @Test
    public void testGetBinding() {
        final PatternWrapper patternWrapper = mock(PatternWrapper.class);

        doReturn(patternWrapper).when(plugin).patternWrapper();

        plugin.getBinding();

        verify(patternWrapper).getBoundName();
    }

    @Test
    public void testSetBinding() {
        final ActionWrapper actionWrapper = mock(ActionWrapper.class);
        final String binding = "binding";

        doReturn(actionWrapper).when(plugin).editingWrapper();

        plugin.setBinding(binding);

        verify(actionWrapper).setBoundName(binding);
    }

    @Test
    public void testTableFormat() {
        final GuidedDecisionTable52.TableFormat expectedTableFormat = GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        when(model.getTableFormat()).thenReturn(expectedTableFormat);
        when(presenter.getModel()).thenReturn(model);

        final GuidedDecisionTable52.TableFormat actualTableFormat = plugin.tableFormat();

        assertEquals(expectedTableFormat,
                     actualTableFormat);
    }

    @Test
    public void testDefaultValueWidget() {
        final IsWidget defaultWidget = plugin.defaultValueWidget();

        assertNotNull(defaultWidget);
    }

    @Test
    public void testLimitedValueWidget() {
        final IsWidget limitedValueWidget = plugin.limitedValueWidget();

        assertNotNull(limitedValueWidget);
    }

    @Test
    public void testInitializedPatternPage() {
        plugin.initializedPatternPage();

        verify(patternPage).disableEntryPoint();
    }

    @Test
    public void testInitializedAdditionalInfoPage() throws Exception {
        plugin.initializedAdditionalInfoPage();

        verify(additionalInfoPage).setPlugin(plugin);
        verify(additionalInfoPage).enableHeader();
        verify(additionalInfoPage).enableHideColumn();
        verify(additionalInfoPage).enableLogicallyInsert();
    }

    @Test
    public void testInitializedValueOptionsPageWhenTableIsALimitedEntry() throws Exception {
        doReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY).when(plugin).tableFormat();

        plugin.initializedValueOptionsPage();

        verify(valueOptionsPage).setPlugin(plugin);
        verify(valueOptionsPage).enableLimitedValue();
    }

    @Test
    public void testGetAlreadyUsedColumnNames() throws Exception {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.getActionCols().add(new ActionCol52() {{
            setHeader("a");
        }});
        model.getActionCols().add(new ActionCol52() {{
            setHeader("b");
        }});
        when(presenter.getModel()).thenReturn(model);

        assertEquals(2,
                     plugin.getAlreadyUsedColumnHeaders().size());
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("a"));
        assertTrue(plugin.getAlreadyUsedColumnHeaders().contains("b"));
    }

    private Pattern52 fakePattern(final String factType,
                                  final String boundName,
                                  final boolean negated) {
        return new Pattern52() {{
            setFactType(factType);
            setBoundName(boundName);
            setNegated(negated);
        }};
    }
}
