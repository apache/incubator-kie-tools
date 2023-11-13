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


package org.kie.workbench.common.stunner.bpmn.client.forms.changeHandlers.businessRuleTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.util.DmnResourceContentFetcher;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BusinessRuleTaskChangeHandlerTest {

    private BusinessRuleTaskChangeHandler tested;
    private BusinessRuleTask businessRuleTask;
    @Mock
    private DmnResourceContentFetcher dmnResourceContentFetcher;

    @Before
    public void setUp() {
        tested = new BusinessRuleTaskChangeHandler(dmnResourceContentFetcher);
        businessRuleTask = new BusinessRuleTask();
        tested.init(businessRuleTask);
    }

    @Test
    public void onFieldChangeOtherFieldTest() {
        tested.onFieldChange("someField", "file1.dmn");
        verify(dmnResourceContentFetcher, never()).fetchFile(anyString(), any());
    }

    @Test
    public void onFieldChangeFilenameInFilenamesFieldTest() {
        Map<String, String> map = Collections.singletonMap("file1.dmn", "file1.dmn");
        when(dmnResourceContentFetcher.getFileNames()).thenReturn(map);
        tested.onFieldChange(BusinessRuleTaskChangeHandler.FILE_NAME_FIELD, "file1.dmn");
        verify(dmnResourceContentFetcher, times(1)).fetchFile(anyString(), any());
    }

    @Test
    public void onFieldChangeFilenameNotInFilenamesFieldTest() {
        businessRuleTask.getExecutionSet().getNamespace().setValue("Some Namespace");
        businessRuleTask.getExecutionSet().getDmnModelName().setValue("Some Model Name");
        dmnResourceContentFetcher.setDecisions(Arrays.asList("Decision-1", "Decision-2", "Decision-3"));
        Map<String, String> map = Collections.singletonMap("file1.dmn", "file1.dmn");
        when(dmnResourceContentFetcher.getFileNames()).thenReturn(map);
        tested.onFieldChange(BusinessRuleTaskChangeHandler.FILE_NAME_FIELD, "file2.dmn");
        assertTrue(businessRuleTask.getExecutionSet().getNamespace().getValue().isEmpty());
        assertTrue(businessRuleTask.getExecutionSet().getDmnModelName().getValue().isEmpty());
        assertTrue(dmnResourceContentFetcher.getDecisions().size() == 0);
        verify(dmnResourceContentFetcher, times(1)).refreshForms();
    }
}