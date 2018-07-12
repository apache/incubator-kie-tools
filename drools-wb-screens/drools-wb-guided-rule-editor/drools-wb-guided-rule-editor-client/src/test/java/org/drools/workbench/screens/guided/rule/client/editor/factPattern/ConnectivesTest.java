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

package org.drools.workbench.screens.guided.rule.client.editor.factPattern;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.screens.guided.rule.client.OperatorsBaseTest;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@WithClassesToStub({GuidedRuleEditorImages508.class, CEPOperatorsDropdown.class})
@RunWith(GwtMockitoTestRunner.class)
public class ConnectivesTest extends OperatorsBaseTest {

    @Test
    public void testConnectiveOperatorsString() throws Exception {
        doReturn("org.Address").when(connectiveConstraint).getFactType();
        doReturn("street").when(connectiveConstraint).getFieldName();

        connectives.connectives(singleFieldConstraint);
        verify(connectives).getDropdown(OperatorsOracle.STRING_CONNECTIVES,
                                        connectiveConstraint);
    }

    @Test
    public void testConnectiveOperatorsInteger() throws Exception {
        doReturn("org.Address").when(connectiveConstraint).getFactType();
        doReturn("number").when(connectiveConstraint).getFieldName();

        connectives.connectives(singleFieldConstraint);
        verify(connectives).getDropdown(OperatorsOracle.COMPARABLE_CONNECTIVES,
                                        connectiveConstraint);
    }

    @Test
    public void testConstraintValueEditorInitialization() {
        final ConstraintValueEditor editor = mock(ConstraintValueEditor.class);
        doReturn(editor).when(connectives).connectiveValueEditor(connectiveConstraint);

        connectives.connectives(singleFieldConstraint);
        verify(connectives).connectiveOperatorDropDown(eq(connectiveConstraint),
                                                       isA(Callback.class));

        verify(editor).init();
    }
}
