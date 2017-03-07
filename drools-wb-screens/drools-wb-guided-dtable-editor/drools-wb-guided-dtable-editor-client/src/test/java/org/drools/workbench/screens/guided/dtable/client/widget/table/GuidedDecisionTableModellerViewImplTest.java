/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control.AttributeColumnConfigRowView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.DecoratedDisclosurePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableModellerViewImplTest {

    @Mock
    GuidedDecisionTableModellerView.Presenter presenter;

    @Mock
    GuidedDecisionTableModellerViewImpl view;

    @Mock
    VerticalPanel attributeConfigWidget;

    @Mock
    DecoratedDisclosurePanel disclosurePanelAttributes;

    @Mock
    AttributeCol52 attributeColumn;

    @Mock
    DTCellValue52 defaultValue;

    List<AttributeCol52> columns;

    @Before
    public void setUp() throws Exception {
        doCallRealMethod().when(view).init(presenter);
        doCallRealMethod().when(view).refreshAttributeWidget(anyList());
        view.init(presenter);
        view.attributeConfigWidget = attributeConfigWidget;
        view.disclosurePanelAttributes = disclosurePanelAttributes;

        when(attributeColumn.getAttribute()).thenReturn("salience");
        when(attributeColumn.getDefaultValue()).thenReturn(defaultValue);

        columns = new ArrayList<>();
        columns.add(attributeColumn);

        Map<String, String> preferences = new HashMap<>();
        preferences.put(ApplicationPreferences.DATE_FORMAT, "dd/mm/yy");
        ApplicationPreferences.setUp(preferences);
    }

    @Test
    public void testRefreshAttributeWidgetEmpty() throws Exception {
        columns.clear();
        view.refreshAttributeWidget(columns);

        verify(attributeConfigWidget).clear();
        verify(disclosurePanelAttributes).setOpen(false);
        verify(attributeConfigWidget, never()).add(any(Widget.class));
    }

    @Test
    public void testRefreshAttributeWidget() throws Exception {
        view.refreshAttributeWidget(columns);

        verify(attributeConfigWidget).clear();
        verify(disclosurePanelAttributes, never()).setOpen(anyBoolean());

        verify(attributeConfigWidget).add(any(AttributeColumnConfigRowView.class));
    }
}
