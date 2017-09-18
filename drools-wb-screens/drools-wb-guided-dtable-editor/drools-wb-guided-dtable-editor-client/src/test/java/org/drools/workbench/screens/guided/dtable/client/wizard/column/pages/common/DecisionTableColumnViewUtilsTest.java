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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionRetractFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionTableColumnViewUtilsTest {

    private static final String SECOND_OPTION = "second option";
    private static final String FIRST_OPTION = "first option";
    private static final String PLEASE_CHOOSE = "please choose";

    private static final String FACT_TYPE_APPLICANT = "Applicant";
    private static final String APPLICANT_BOUND_NAME = "$a";
    private static final String COLUMN_HEADER = "column header";

    @Mock
    ListBox listBox;

    @Before
    public void setUp() throws Exception {
        when(listBox.getItemCount()).thenReturn(3);
        when(listBox.getValue(0)).thenReturn(PLEASE_CHOOSE);
        when(listBox.getValue(1)).thenReturn(FIRST_OPTION);
        when(listBox.getValue(2)).thenReturn(SECOND_OPTION);
    }

    @Test
    public void testGetIndexWithoutDefaultSelectNull() throws Exception {
        assertEquals(-1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(null,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectEmpty() throws Exception {
        assertEquals(-1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect("",
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectPlaceholder() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(PLEASE_CHOOSE,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectFirstOption() throws Exception {
        assertEquals(1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(FIRST_OPTION,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithoutDefaultSelectSecondOption() throws Exception {
        assertEquals(2,
                     DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect(SECOND_OPTION,
                                                                                              listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectNull() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(null,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectEmpty() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList("",
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectPlaceholder() throws Exception {
        assertEquals(0,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(PLEASE_CHOOSE,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectFirstOption() throws Exception {
        assertEquals(1,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(FIRST_OPTION,
                                                                          listBox));
    }

    @Test
    public void testGetIndexWithDefaultSelectSecondOption() throws Exception {
        assertEquals(2,
                     DecisionTableColumnViewUtils.getCurrentIndexFromList(SECOND_OPTION,
                                                                          listBox));
    }

    @Test
    public void testColumnManagementGroupTitleInsertFact() throws Exception {
        final ActionInsertFactCol52 column = mock(ActionInsertFactCol52.class);
        doReturn(FACT_TYPE_APPLICANT).when(column).getFactType();
        doReturn(APPLICANT_BOUND_NAME).when(column).getBoundName();

        assertEquals(FACT_TYPE_APPLICANT + " [" + APPLICANT_BOUND_NAME + "]",
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleInsertFactEmpty() throws Exception {
        final ActionInsertFactCol52 column = mock(ActionInsertFactCol52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleSetField() throws Exception {
        final ActionSetFieldCol52 column = mock(ActionSetFieldCol52.class);
        doReturn(APPLICANT_BOUND_NAME).when(column).getBoundName();

        assertEquals("[" + APPLICANT_BOUND_NAME + "]",
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleSetFieldEmpty() throws Exception {
        final ActionSetFieldCol52 column = mock(ActionSetFieldCol52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleWorkItemInsertFact() throws Exception {
        final ActionWorkItemInsertFactCol52 column = mock(ActionWorkItemInsertFactCol52.class);
        doReturn(FACT_TYPE_APPLICANT).when(column).getFactType();
        doReturn(APPLICANT_BOUND_NAME).when(column).getBoundName();

        assertEquals(FACT_TYPE_APPLICANT + " [" + APPLICANT_BOUND_NAME + "]",
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleWorkItemInsertFactEmpty() throws Exception {
        final ActionWorkItemInsertFactCol52 column = mock(ActionWorkItemInsertFactCol52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleWorkItemSetField() throws Exception {
        final ActionWorkItemSetFieldCol52 column = mock(ActionWorkItemSetFieldCol52.class);
        doReturn(APPLICANT_BOUND_NAME).when(column).getBoundName();

        assertEquals("[" + APPLICANT_BOUND_NAME + "]",
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleWorkItemSetFieldEmpty() throws Exception {
        final ActionWorkItemSetFieldCol52 column = mock(ActionWorkItemSetFieldCol52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitlePattern() throws Exception {
        final Pattern52 column = mock(Pattern52.class);
        doReturn(FACT_TYPE_APPLICANT).when(column).getFactType();
        doReturn(APPLICANT_BOUND_NAME).when(column).getBoundName();

        assertEquals(FACT_TYPE_APPLICANT + " [" + APPLICANT_BOUND_NAME + "]",
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitlePatternNegated() throws Exception {
        final Pattern52 column = mock(Pattern52.class);
        doReturn(FACT_TYPE_APPLICANT).when(column).getFactType();
        doReturn(true).when(column).isNegated();

        assertEquals(GuidedDecisionTableConstants.INSTANCE.negatedPattern() + " " + FACT_TYPE_APPLICANT,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitlePatternEmpty() throws Exception {
        final Pattern52 column = mock(Pattern52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleRetractAction() throws Exception {
        final ActionRetractFactCol52 column = mock(ActionRetractFactCol52.class);

        assertEquals(GuidedDecisionTableConstants.INSTANCE.RetractActions(),
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleWorkItemAction() throws Exception {
        final ActionWorkItemCol52 column = mock(ActionWorkItemCol52.class);

        assertEquals(GuidedDecisionTableConstants.INSTANCE.ExecuteWorkItemActions(),
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleBrlAction() throws Exception {
        final BRLActionColumn column = mock(BRLActionColumn.class);

        assertEquals(GuidedDecisionTableConstants.INSTANCE.BrlActions(),
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitleBrlCondition() throws Exception {
        final BRLConditionColumn column = mock(BRLConditionColumn.class);

        assertEquals(GuidedDecisionTableConstants.INSTANCE.BrlConditions(),
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }

    @Test
    public void testColumnManagementGroupTitle() throws Exception {
        final ActionCol52 column = mock(ActionCol52.class);
        doReturn(COLUMN_HEADER).when(column).getHeader();

        assertEquals(COLUMN_HEADER,
                     DecisionTableColumnViewUtils.getColumnManagementGroupTitle(column));
    }
}
