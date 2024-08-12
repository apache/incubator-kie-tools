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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@Ignore("https://github.com/apache/incubator-kie-issues/issues/1431")
@RunWith(MockitoJUnitRunner.Silent.class)
public class AssignmentDataTest extends AssignmentBaseTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }



    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testSetAssignments() {
        AssignmentData assignmentData = new AssignmentData();
        String assignment1 = "function.getValue(par1,par1)";
        String assignment2 = "function.getValue(par2,par2)";
        String assignment3 = "function.getValue(par3.Cpar3)";
        String assignment4 = "function.getValue(par4,Cpar4)";
        String sAssignments = "[din]DIA1=" + assignment1 + "," +
                              "[din]DIA2=" + assignment2+ "," +
                              "[dout]" + assignment3 + "=DOA1," +
                              "[dout]" + assignment4 + "=DOA2";
        assignmentData.setAssignments(sAssignments);
        List<Assignment> result = assignmentData.getAssignments();
        assertEquals(4, result.stream().count());
        assertEquals(assignment1, result.get(0).getExpression());
        assertEquals(assignment2, result.get(1).getExpression());
        assertEquals(assignment3, result.get(2).getExpression());
        assertEquals(assignment4, result.get(3).getExpression());

    }

    @Test
    public void testSetVariableCountsString1() {
        // Single input only
        AssignmentData assignmentData = new AssignmentData("inStr:String",
                                                           null,
                                                           "str1:String",
                                                           "[din]str1->inStr",
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               true,
                                               false,
                                               false);
        assertEquals("1 Data_Input",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString2() {
        // Single input absent
        AssignmentData assignmentData = new AssignmentData("",
                                                           null,
                                                           "str1:String",
                                                           null,
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               true,
                                               false,
                                               false);
        assertEquals("No_Data_Input",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString3() {
        // Single output only
        AssignmentData assignmentData = new AssignmentData(null,
                                                           "outStr1:String",
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           "[dout]outStr1->str1",
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(false,
                                               false,
                                               true,
                                               true);
        assertEquals("1 Data_Output",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString4() {
        // Single output absent
        AssignmentData assignmentData = new AssignmentData(null,
                                                           null,
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           null,
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(false,
                                               false,
                                               true,
                                               true);
        assertEquals("No_Data_Output",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString5() {
        // No inputs & no outputs
        AssignmentData assignmentData = new AssignmentData(null,
                                                           null,
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           null,
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               false,
                                               true,
                                               false);
        assertEquals("0 Data_Inputs, 0 Data_Outputs",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString6() {
        // Single inputs & output
        AssignmentData assignmentData = new AssignmentData("inStr:String,Skippable",
                                                           "outStr1:String",
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           "[din]str1->inStr,[dout]outStr1->str1",
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               false,
                                               true,
                                               false);
        assertEquals("1 Data_Input, 1 Data_Output",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString10() {
        // Several inputs & outputs
        AssignmentData assignmentData = new AssignmentData("inStr:String,inInt1:Integer,inCustom1:org.jdl.Custom,inStrConst:String,Skippable",
                                                           "outStr1:String,outInt1:Integer,outCustom1:org.jdl.Custom",
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           "[din]str1->inStr,[din]int1->inInt1,[din]custom1->inCustom1,[din]inStrConst=TheString,[dout]outStr1->str1,[dout]outInt1->int1,[dout]outCustom1->custom1",
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               false,
                                               true,
                                               false);
        assertEquals("4 Data_Inputs, 3 Data_Outputs",
                     assignmentData.getVariableCountsString());
    }

    @Test
    public void testSetVariableCountsString11() {
        // No inputs& 3 outputs assignments with same source
        AssignmentData assignmentData = new AssignmentData(null,
                                                           "outStr1:String",
                                                           "str1:String,int1:Integer,custom1:org.jdl.Custom",
                                                           "[dout]outStr1->str1,[dout]outStr1->int1,[dout]outStr1->custom1",
                                                           "String:String, Integer:Integer, Boolean:Boolean, Float:Float, Object:Object",
                                                           "GroupId,Skippable,Comment,Description,Priority,Content,TaskName,Locale,CreatedBy,NotCompletedReassign,NotStartedReassign,NotCompletedNotify,NotStartedNotify");
        assignmentData.setVariableCountsString(true,
                                               false,
                                               true,
                                               false);
        assertEquals("0 Data_Inputs, 3 Data_Outputs",
                     assignmentData.getVariableCountsString());
    }
}
