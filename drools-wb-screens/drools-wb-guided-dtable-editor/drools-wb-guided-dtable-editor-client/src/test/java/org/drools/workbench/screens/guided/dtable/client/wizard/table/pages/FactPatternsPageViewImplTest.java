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
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.table.pages.cells.DefaultPatternCell;
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

@WithClassesToStub(Text.class)
@RunWith(GwtMockitoTestRunner.class)
public class FactPatternsPageViewImplTest {

    @Mock
    DefaultPatternCell patternCell;

    @InjectMocks
    FactPatternsPageViewImpl view;

    @Captor
    ArgumentCaptor<MinimumWidthCellList<Pattern52>> chosenPatternWidgetCaptor;

    MultiSelectionModel<Pattern52> selectionModel;

    Pattern52 person;

    @Before
    public void setUp() throws Exception {
        view.setup();
        view.setValidator(mock(Validator.class));
        view.init(mock(FactPatternsPageView.Presenter.class));
    }

    @Test
    public void testPatternsSelectionModel() throws Exception {
        verify(view.chosenPatternsContainer).add(chosenPatternWidgetCaptor.capture());
        selectionModel = (MultiSelectionModel<Pattern52>) chosenPatternWidgetCaptor.getValue().getSelectionModel();

        person = new Pattern52();
        person.setFactType("Person");
        selectionModel.setSelected(person, true);
        assertTrue(selectionModel.isSelected(person));

        person.setBoundName("p");
        assertTrue(selectionModel.isSelected(person));
    }
}
