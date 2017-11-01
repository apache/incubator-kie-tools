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

package org.drools.workbench.screens.guided.dtable.client.wizard.table.pages;

import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactFieldsPattern;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.cells.ActionInsertFactFieldCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.cells.ActionInsertFactFieldPatternCell;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub(Text.class)
@RunWith(GwtMockitoTestRunner.class)
public class ActionInsertFactFieldsPageViewImplTest {

    @Mock
    ActionInsertFactFieldPatternCell actionInsertFactFieldPatternCell;

    @Mock
    ActionInsertFactFieldCell actionInsertFactFieldCell;

    @InjectMocks
    ActionInsertFactFieldsPageViewImpl view;

    @Captor
    ArgumentCaptor<MinimumWidthCellList<ActionInsertFactFieldsPattern>> chosenPatternsWidgetCaptor;

    MultiSelectionModel<ActionInsertFactFieldsPattern> patternSelectionModel;

    ActionInsertFactFieldsPattern pattern;

    @Captor
    ArgumentCaptor<MinimumWidthCellList<ActionInsertFactCol52>> chosenFieldsWidgetCaptor;

    MultiSelectionModel<ActionInsertFactCol52> fieldSelectionModel;

    ActionInsertFactCol52 insertFactCol52;

    @Before
    public void setUp() throws Exception {
        view.setup();
        view.setValidator(mock(Validator.class));
        ActionInsertFactFieldsPageView.Presenter presenter = mock(ActionInsertFactFieldsPageView.Presenter.class);
        when(presenter.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        view.init(presenter);
    }

    @Test
    public void testSetFieldSelectionModel() throws Exception {
        verify(view.chosenFieldsContainer).add(chosenFieldsWidgetCaptor.capture());
        fieldSelectionModel = (MultiSelectionModel<ActionInsertFactCol52>) chosenFieldsWidgetCaptor.getValue().getSelectionModel();

        insertFactCol52 = new ActionInsertFactCol52();
        insertFactCol52.setType("Applicant");
        insertFactCol52.setFactField("age");
        insertFactCol52.setType("Integer");
        fieldSelectionModel.setSelected(insertFactCol52, true);
        assertTrue(fieldSelectionModel.isSelected(insertFactCol52));

        insertFactCol52.setDefaultValue(new DTCellValue52(18));
        assertTrue(fieldSelectionModel.isSelected(insertFactCol52));
    }

    @Test
    public void testPatternsSelectionModel() throws Exception {
        verify(view.chosenPatternsContainer).add(chosenPatternsWidgetCaptor.capture());
        patternSelectionModel = (MultiSelectionModel<ActionInsertFactFieldsPattern>) chosenPatternsWidgetCaptor.getValue().getSelectionModel();

        pattern = new ActionInsertFactFieldsPattern();
        pattern.setFactType("Message");
        pattern.setBoundName("m");
        patternSelectionModel.setSelected(pattern, true);
        assertTrue(patternSelectionModel.isSelected(pattern));

        pattern.setInsertedLogically(true);
        assertTrue(patternSelectionModel.isSelected(pattern));
    }


}
