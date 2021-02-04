/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.screens.guided.rule.client.editor.ActionValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub({FlexTable.class, DateTimeFormat.class})
@RunWith(GwtMockitoTestRunner.class)
public class ActionInsertFactWidgetTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private RuleModeller modeller;

    @Mock
    private EventBus eventBus;

    @Mock
    private ActionInsertFact action;

    private boolean readOnly = false;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    private ActionInsertFactWidget testedWidget;

    @Before
    public void setUp() throws Exception {
        doReturn(oracle).when(modeller).getDataModelOracle();
        doReturn(new ActionFieldValue[0]).when(action).getFieldValues();

        testedWidget = spy(new ActionInsertFactWidget(modeller, eventBus, action, readOnly));
    }

    @Test
    public void testOnChangeCallbackRegisteredForActionValueEditor() throws Exception {
        final ActionFieldValue value = new ActionFieldValue();
        value.setNature(FieldNatureType.TYPE_LITERAL);
        value.setField("a");
        final ActionValueEditor editor = mock(ActionValueEditor.class);
        doReturn(editor).when(testedWidget).actionValueEditor(any(),
                                                              eq(value),
                                                              any(),
                                                              eq(readOnly));

        final ActionFieldValue valueTwo = new ActionFieldValue();
        valueTwo.setNature(FieldNatureType.TYPE_LITERAL);
        valueTwo.setField("b");
        final ActionValueEditor editorTwo = mock(ActionValueEditor.class);
        doReturn(editorTwo).when(testedWidget).actionValueEditor(any(),
                                                                 eq(valueTwo),
                                                                 any(),
                                                                 eq(readOnly));

        testedWidget.valueEditor(value);
        testedWidget.valueEditor(valueTwo);

        verify(editor).setOnChangeCommand(commandCaptor.capture());
        commandCaptor.getValue().execute();

        verify(testedWidget).setModified(true);
        verify(editorTwo).refresh();
    }
}
