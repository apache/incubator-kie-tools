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
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.cells.ActionSetFieldCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.cells.ActionSetFieldPatternCell;
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
public class ActionSetFieldsPageViewImplTest {

    @Mock
    ActionSetFieldPatternCell actionSetFieldPatternCell;

    @Mock
    ActionSetFieldCell actionSetFieldCell;

    @InjectMocks
    ActionSetFieldsPageViewImpl view;

    @Captor
    ArgumentCaptor<MinimumWidthCellList<ActionSetFieldCol52>> chosenFieldsWidgetCaptor;

    MultiSelectionModel<ActionSetFieldCol52> selectionModel;

    ActionSetFieldCol52 setFieldCol52;

    @Before
    public void setUp() throws Exception {
        view.setup();
        view.setValidator(mock(Validator.class));
        ActionSetFieldsPageView.Presenter presenter = mock(ActionSetFieldsPageView.Presenter.class);
        when(presenter.getTableFormat()).thenReturn(GuidedDecisionTable52.TableFormat.LIMITED_ENTRY);
        view.init(presenter);
    }

    @Test
    public void testSetFieldSelectionModel() throws Exception {
        verify(view.chosenFieldsContainer).add(chosenFieldsWidgetCaptor.capture());
        selectionModel = (MultiSelectionModel<ActionSetFieldCol52>) chosenFieldsWidgetCaptor.getValue().getSelectionModel();

        setFieldCol52 = new ActionSetFieldCol52();
        setFieldCol52.setType("Person");
        setFieldCol52.setFactField("name");
        setFieldCol52.setUpdate(true);
        selectionModel.setSelected(setFieldCol52, true);
        assertTrue(selectionModel.isSelected(setFieldCol52));

        setFieldCol52.setValueList("a,b,c");
        assertTrue(selectionModel.isSelected(setFieldCol52));
    }


}
