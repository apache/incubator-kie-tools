/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Mockito.mock;

@RunWith(GwtMockitoTestRunner.class)
public class SearchableElementFactoryTest {

    @Mock
    private GuidedDecisionTableGridHighlightHelper highlightHelper;

    private SearchableElementFactory factory;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(new Maps.Builder<String, String>().put(DATE_FORMAT, "dd/mm/yy").build());
        factory = new SearchableElementFactory(highlightHelper);
    }

    @Test
    public void testMakeSearchableElement() {

        final int row = 0;
        final int column = 0;
        final String value = "value";
        final DTCellValue52 cellValue52 = new DTCellValue52(value);
        final GuidedDecisionTableModellerView.Presenter modeller = mock(GuidedDecisionTableModellerView.Presenter.class);
        final GuidedDecisionTable52 model = mock(GuidedDecisionTable52.class);

        final GuidedDecisionTableSearchableElement element = factory.makeSearchableElement(row, column, cellValue52, null, model, modeller);

        assertEquals(row, element.getRow());
        assertEquals(column, element.getColumn());
        assertEquals(value, element.getValue());
        assertEquals(modeller, element.getModeller());
        assertEquals(highlightHelper, element.getHighlightHelper());
        assertEquals(model, element.getModel());
    }
}
