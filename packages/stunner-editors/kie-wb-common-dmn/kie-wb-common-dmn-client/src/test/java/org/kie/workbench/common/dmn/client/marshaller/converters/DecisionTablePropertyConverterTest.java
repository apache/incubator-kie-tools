/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITOutputClause;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@PrepareForTest(OutputClauseUnaryTestsPropertyConverter.class)
public class DecisionTablePropertyConverterTest {

    @GwtMock
    private JSITDecisionTable jsitDecisionTable;

    @GwtMock
    private List<JSITOutputClause> jsitOutput;

    @GwtMock
    private JSITOutputClause jsitOutputItem;

    @Test
    public void testConverterWhenJSITOutputTypeRefIsNullThenDecisionTableOutputTypeRefIsUndefined() {
        when(jsitDecisionTable.getOutput()).thenReturn(jsitOutput);
        when(jsitOutput.size()).thenReturn(1);
        when(jsitOutput.get(0)).thenReturn(jsitOutputItem);

        final DecisionTable decisionTable = DecisionTablePropertyConverter.wbFromDMN(jsitDecisionTable);
        assertNotNull(decisionTable);
        assertNotNull(decisionTable.getOutput());
        assertEquals(1, decisionTable.getOutput().size());
        assertEquals(BuiltInType.UNDEFINED.asQName(), decisionTable.getOutput().get(0).getTypeRef());
    }
}
