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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@WithClassesToStub(DateTimeFormat.class)
@RunWith(GwtMockitoTestRunner.class)
public class DSLDropDownTest {

    private DSLDropDown testedDropDown;

    @Test
    public void testGetDropDown() throws Exception {
        final String fact = "Fact";
        final String field = "field";
        final AsyncPackageDataModelOracle oracle = mock(AsyncPackageDataModelOracle.class);
        final RuleModeller ruleModeller = mock(RuleModeller.class);
        doReturn(oracle).when(ruleModeller).getDataModelOracle();
        final String variableDefinition = "varName:type:" + fact + "." + field;
        final DSLSentence dslSentence = mock(DSLSentence.class);
        final DSLVariableValue dslVariableValue = mock(DSLVariableValue.class);
        final boolean isMultipleSelect = false;
        final Callback<DSLDropDown> updateEnumsCallback = mock(Callback.class);
        testedDropDown = new DSLDropDown(ruleModeller,
                                         variableDefinition,
                                         dslSentence,
                                         dslVariableValue,
                                         isMultipleSelect,
                                         updateEnumsCallback);

        // reset oracle due to calls in DSLDropDown constructor
        reset(oracle);
        testedDropDown.getDropDownData();

        verify(oracle).getEnums(eq(fact), eq(field), anyMap());
    }
}
