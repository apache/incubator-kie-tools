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

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ValueOptionsPageTest {

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private PatternWrapper patternWrapper;

    @Mock
    private Pattern52 pattern52;

    @Mock
    private ConditionCol52 editingCol;

    @Mock
    private GuidedDecisionTable52 model;

    @Mock
    private DTCellValue52 defaultValue;

    @Mock
    private ValueOptionsPage.View view;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private ValueOptionsPage<ConditionColumnPlugin> page = spy(new ValueOptionsPage<ConditionColumnPlugin>(view,
                                                                                                           translationService));

    @BeforeClass
    public static void staticSetup() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();

        ApplicationPreferences.setUp(new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd-MM-yyyy");
        }});
    }

    @Before
    public void setup() {
        when(defaultValue.getDataType()).thenReturn(DataType.DataTypes.STRING);
        when(editingCol.getDefaultValue()).thenReturn(defaultValue);
        when(presenter.getModel()).thenReturn(model);
        when(presenter.getDataModelOracle()).thenReturn(oracle);
        when(plugin.patternWrapper()).thenReturn(patternWrapper);
        when(plugin.editingCol()).thenReturn(editingCol);
        when(plugin.getPresenter()).thenReturn(presenter);
        when(plugin.editingPattern()).thenReturn(pattern52);
        when(page.plugin()).thenReturn(plugin);
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);

        doCallRealMethod().when(plugin).defaultValueWidget();
    }

    @Test
    public void testNewDefaultValueWidget() throws Exception {
        when(editingCol.getValueList()).thenReturn("a,b,c");
        assertTrue(page.newDefaultValueWidget() instanceof ListBox);

        verify(plugin).defaultValueWidget();
    }

    @Test
    public void testNewLimitedValueWidget() throws Exception {
        page.newLimitedValueWidget();

        verify(plugin).limitedValueWidget();
    }

    @Test
    public void testCanSetupCepOperatorsWhenItIsNotEnabled() throws Exception {
        assertFalse(page.canSetupCepOperators());
    }

    @Test
    public void testCanSetupCepOperatorsWhenItIsEnabledAndEditingPatternIsNotNull() throws Exception {
        when(plugin.editingPattern()).thenReturn(pattern52);
        when(pattern52.getFactType()).thenReturn("factType");

        page.enableCepOperators();

        assertTrue(page.canSetupCepOperators());
    }

    @Test
    public void testCanSetupCepOperatorsWhenItIsEnabledAndEditingPatternIsNull() throws Exception {
        when(plugin.editingPattern()).thenReturn(null);

        page.enableCepOperators();

        assertFalse(page.canSetupCepOperators());
    }

    @Test
    public void testCanSetupDefaultValueWhenEditingColIsNull() throws Exception {
        when(plugin.editingCol()).thenReturn(null);

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenEditingPatternIsNull() throws Exception {
        when(plugin.patternWrapper()).thenReturn(null);

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenEditingColHasAnEmptyFactType() throws Exception {
        when(editingCol.getFactField()).thenReturn("");

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenOperatorNeedsAValue() throws Exception {
        when(editingCol.getFactField()).thenReturn("factField");
        when(plugin.doesOperatorNeedValue()).thenReturn(false);

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenTableFormatIsNotExtendedEntry() throws Exception {
        when(editingCol.getFactField()).thenReturn("factField");
        when(plugin.doesOperatorNeedValue()).thenReturn(true);
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenItIsNotEnabled() throws Exception {
        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.getFactType()).thenReturn("factType");
        when(plugin.doesOperatorNeedValue()).thenReturn(true);
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);

        assertFalse(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupDefaultValueWhenCanSetup() throws Exception {
        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.getFactType()).thenReturn("factType");
        when(plugin.doesOperatorNeedValue()).thenReturn(true);
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);

        page.enableDefaultValue();

        assertTrue(page.canSetupDefaultValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenEditingColIsNull() throws Exception {
        when(plugin.editingCol()).thenReturn(null);

        assertFalse(page.canSetupLimitedValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenEditingPatternIsNull() throws Exception {
        when(plugin.patternWrapper()).thenReturn(null);

        assertFalse(page.canSetupLimitedValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenEditingColIsNotAnInstanceOfLimitedEntryConditionCol52() throws Exception {
        when(plugin.editingCol()).thenReturn(new ConditionCol52());

        assertFalse(page.canSetupLimitedValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenOperatorNeedsAValue() throws Exception {
        when(plugin.editingCol()).thenReturn(new LimitedEntryConditionCol52());
        when(plugin.doesOperatorNeedValue()).thenReturn(false);

        assertFalse(page.canSetupLimitedValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenTableFormatIsNotLimitedEntry() throws Exception {
        when(plugin.editingCol()).thenReturn(new LimitedEntryConditionCol52());
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
        when(plugin.doesOperatorNeedValue()).thenReturn(true);

        assertFalse(page.canSetupLimitedValue());
    }

    @Test
    public void testCanSetupLimitedValueWhenCanSetup() throws Exception {
        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.getFactType()).thenReturn("factType");
        when(model.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        when(plugin.doesOperatorNeedValue()).thenReturn(true);

        page.enableLimitedValue();

        assertTrue(page.canSetupLimitedValue());
    }

    @Test
    public void testIsFactTypeAnEventWhenCepOperatorsIsEnabled() throws Exception {
        page.enableCepOperators();

        when(plugin.editingPattern()).thenReturn(pattern52);
        when(pattern52.getFactType()).thenReturn("factType");

        page.isFactTypeAnEvent(Assert::assertTrue);

        verify(oracle).isFactTypeAnEvent(eq("factType"),
                                         any());
    }

    @Test
    public void testIsFactTypeAnEventWhenCepOperatorsIsNotEnabled() throws Exception {
        when(patternWrapper.getFactType()).thenReturn("factType");

        page.isFactTypeAnEvent(Assert::assertFalse);
    }

    @Test
    public void testCanSetupBindingWhenBindingIsNotEnabled() throws Exception {
        assertFalse(page.canSetupBinding());
    }

    @Test
    public void testCanSetupBindingWhenBindingIsEnabledAndIsNotBindable() throws Exception {
        page.enableBinding();

        when(plugin.isBindable()).thenReturn(false);

        assertFalse(page.canSetupBinding());
    }

    @Test
    public void testCanSetupBindingWhenBindingIsEnabledAndIsBindable() throws Exception {
        page.enableBinding();

        when(plugin.isBindable()).thenReturn(true);

        assertTrue(page.canSetupBinding());
    }

    @Test
    public void testValueListDisabledWhenEnumsPresent() throws Exception {
        page.enableValueList();
        when(pattern52.getFactType()).thenReturn("factType");
        when(plugin.getFactType()).thenReturn("factType");
        when(plugin.getFactField()).thenReturn("factField");
        when(plugin.doesOperatorAcceptValueList()).thenReturn(true);
        when(oracle.hasEnums("factType",
                             "factField")).thenReturn(true);

        page.prepareView();

        verify(view,
               never()).hideValueList();
        verify(view).disableValueList();
        verify(oracle).hasEnums("factType",
                                "factField");
    }

    @Test
    public void testSetupValueListWhenValueListIsNotEnabled() throws Exception {
        doReturn(false).when(page).isValueListEnabled();

        page.setupValueList();

        verify(view).hideValueList();
    }

    @Test
    public void testSetupValueListWhenValueListCanNotBeSetUp() throws Exception {
        doReturn(true).when(page).isValueListEnabled();
        doReturn(false).when(page).canSetupValueList();

        page.setupValueList();

        verify(view).disableValueList();
        verify(view).setValueListText("");
    }

    @Test
    public void testSetupValueListWhenValueListCanBeSetUp() throws Exception {
        doReturn(true).when(page).isValueListEnabled();
        doReturn(true).when(page).canSetupValueList();

        page.setupValueList();

        verify(view).enableValueList();
        verify(view).setValueListText(any());
    }
}
