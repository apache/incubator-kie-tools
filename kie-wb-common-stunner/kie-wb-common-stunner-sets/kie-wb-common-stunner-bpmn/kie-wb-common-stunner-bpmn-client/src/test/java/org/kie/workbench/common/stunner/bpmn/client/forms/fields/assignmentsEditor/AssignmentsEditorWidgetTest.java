/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.Map;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentBaseTest;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( PowerMockRunner.class )
@PrepareForTest( StringUtils.class )
public class AssignmentsEditorWidgetTest extends AssignmentBaseTest {

    @GwtMock
    private AssignmentsEditorWidget widget;

    private static final String ASSIGNMENTS_INFO = "|input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable||output1:com.test.Employee,output2:String|[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks( this );
        doCallRealMethod().when( widget ).parseAssignmentsInfo();
        doCallRealMethod().when( widget ).getVariableCountsString( any( String.class ), any( String.class ), any( String.class ),
                any( String.class ), any( String.class ), any( String.class ), any( String.class ) );
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testParseAssignmentsInfo() {
        widget.assignmentsInfo = ASSIGNMENTS_INFO;
        Map<String, String> assignmentsProperties = widget.parseAssignmentsInfo();
        assertEquals( "input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable", assignmentsProperties.get( "datainputset" ) );
        assertEquals( "output1:com.test.Employee,output2:String", assignmentsProperties.get( "dataoutputset" ) );
        assertEquals( "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason", assignmentsProperties.get( "assignments" ) );
    }

    @Test
    public void testGetVariableCountsString() {
        String variableCountsString = widget.getVariableCountsString( null, "input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable",
                null, "output1:com.test.Employee,output2:String",
                "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String",
                "[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason",
                "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify" );
        assertEquals( "4 Data_Inputs, 2 Data_Outputs", variableCountsString );
    }
}
