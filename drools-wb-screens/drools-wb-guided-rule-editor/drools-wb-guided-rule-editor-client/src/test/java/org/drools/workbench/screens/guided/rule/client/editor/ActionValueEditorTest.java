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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@WithClassesToStub(DateTimeFormat.class)
@RunWith(GwtMockitoTestRunner.class)
public class ActionValueEditorTest {

    private ActionValueEditor actionValueEditor;

    @Test
    public void testGetDropDownData() throws Exception {
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final String factType = "Car";
        final String factField = "color";
        final ActionFieldValue value = new ActionFieldValue() {{
            setField(factField);
        }};
        final ActionFieldValue[] values = new ActionFieldValue[0];
        final RuleModeller modeller = mock(RuleModeller.class);
        doReturn(oracle).when(modeller).getDataModelOracle();
        final EventBus eventBus = mock(EventBus.class);
        final String variableType = DataType.TYPE_STRING;
        final boolean readOnly = false;
        actionValueEditor = new ActionValueEditor(factType,
                                                  value,
                                                  values,
                                                  modeller,
                                                  eventBus,
                                                  variableType,
                                                  readOnly);
        // reset oracle due to calls in ActionValueEditor constructor
        reset(oracle);

        actionValueEditor.getDropDownData();

        final Map<String, String> fieldValues = new HashMap<>();
        verify(oracle).getEnums(eq(factType), eq(factField), eq(fieldValues));
    }
}
