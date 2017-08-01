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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.gwtmockito.fakes.FakeProvider;
import org.drools.workbench.models.datamodel.rule.FromCollectCompositeFactPattern;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub({RootPanel.class, Text.class})
@RunWith(GwtMockitoTestRunner.class)
public class FromCollectCompositeFactPatternWidgetTest {

    private List<String> collectionTypes;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private RuleModeller ruleModeller;

    @Mock
    private EventBus eventBus;

    @Mock
    private FromCollectCompositeFactPattern pattern;

    @Mock
    private ListBox listBox;

    FromCollectCompositeFactPatternWidget fromCollectWidget;

    @Before
    public void setUp() throws Exception {
        fromCollectWidget = new FromCollectCompositeFactPatternWidget(ruleModeller,
                                                                      eventBus,
                                                                      pattern);

        GwtMockito.useProviderForType(ListBox.class,
                                      new ListBoxFakeProvider());

        collectionTypes = new ArrayList<String>() {{
            add("ArrayList");
            add("HashSet");
        }};

        when(ruleModeller.getDataModelOracle()).thenReturn(oracle);
        when(oracle.getAvailableCollectionTypes()).thenReturn(collectionTypes);
    }

    @Test
    public void testShowFactTypeSelector() throws Exception {
        fromCollectWidget.showFactTypeSelector();

        verify(listBox).addItem("ArrayList",
                                "ArrayList");
        verify(listBox).addItem("HashSet",
                                "HashSet");
    }

    private class ListBoxFakeProvider implements FakeProvider<ListBox> {

        @Override
        public ListBox getFake(Class<?> aClass) {
            return listBox;
        }
    }
}
