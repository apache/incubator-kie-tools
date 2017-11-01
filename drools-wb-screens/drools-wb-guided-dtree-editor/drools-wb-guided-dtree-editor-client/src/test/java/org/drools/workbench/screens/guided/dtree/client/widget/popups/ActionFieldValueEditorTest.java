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
package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.HasValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionFieldValueImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ActionFieldValueEditorTest {

    private static final String FACT_TYPE = "MyFact";

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private ParameterizedCommand<ActionFieldValue> onDeleteCallback;

    @Mock
    private ValueEditorFactory valueEditorFactory;

    @Mock
    private Widget widget;

    @GwtMock
    private ListBox fieldListBox;

    @GwtMock
    private FormGroup valueHolder;

    private ActionFieldValue afv = new ActionFieldValueImpl();
    private List<ActionFieldValue> afvs = Collections.emptyList();
    private ActionFieldValueEditor editor;

    @Before
    public void setup() {
        when(valueEditorFactory.getValueEditor(anyString(),
                                               anyString(),
                                               any(HasValue.class),
                                               eq(oracle),
                                               anyBoolean())).thenReturn(widget);
    }

    @Test
    public void checkInitializeFieldNamesWithoutModelFields() {
        initialiseDataModelOracle(new ModelField[]{});

        this.editor = new ActionFieldValueEditorFake(FACT_TYPE,
                                                     afv,
                                                     afvs,
                                                     oracle,
                                                     onDeleteCallback);

        verify(fieldListBox,
               atLeast(1)).setEnabled(eq(false));
        verify(fieldListBox,
               never()).setEnabled(eq(true));
        verify(fieldListBox).addItem(eq(GuidedDecisionTreeConstants.INSTANCE.noFields()));
    }

    @Test
    public void checkInitializeFieldNamesWithModelFields() {
        final ModelField[] modelFields = new ModelField[]{
                new ModelField("field1",
                               String.class.getName(),
                               ModelField.FIELD_CLASS_TYPE.REGULAR_CLASS,
                               ModelField.FIELD_ORIGIN.DECLARED,
                               FieldAccessorsAndMutators.BOTH,
                               DataType.TYPE_STRING)
        };
        initialiseDataModelOracle(modelFields);

        doAnswer((invocation) -> {
            final int index = (int) invocation.getArguments()[0];
            return modelFields[index].getName();
        }).when(fieldListBox).getItemText(anyInt());

        this.editor = new ActionFieldValueEditorFake(FACT_TYPE,
                                                     afv,
                                                     afvs,
                                                     oracle,
                                                     onDeleteCallback);

        verify(fieldListBox).setEnabled(eq(true));
        verify(fieldListBox).setSelectedIndex(eq(0));
        verify(valueHolder).clear();
        verify(valueHolder).add(eq(widget));
    }

    @SuppressWarnings("unchecked")
    private void initialiseDataModelOracle(final ModelField[] modelFields) {
        doAnswer((invocation) -> {
            final Callback<ModelField[]> callback = (Callback) invocation.getArguments()[1];
            callback.callback(modelFields);
            return null;
        }).when(oracle).getFieldCompletions(eq(FACT_TYPE),
                                            any(Callback.class));
    }

    private class ActionFieldValueEditorFake extends ActionFieldValueEditor {

        public ActionFieldValueEditorFake(final String className,
                                          final ActionFieldValue afv,
                                          final List<ActionFieldValue> afvs,
                                          final AsyncPackageDataModelOracle oracle,
                                          final ParameterizedCommand<ActionFieldValue> onDeleteCallback) {
            super(className,
                  afv,
                  afvs,
                  oracle,
                  onDeleteCallback);
        }

        @Override
        ValueEditorFactory valueEditorFactory() {
            return valueEditorFactory;
        }
    }
}
