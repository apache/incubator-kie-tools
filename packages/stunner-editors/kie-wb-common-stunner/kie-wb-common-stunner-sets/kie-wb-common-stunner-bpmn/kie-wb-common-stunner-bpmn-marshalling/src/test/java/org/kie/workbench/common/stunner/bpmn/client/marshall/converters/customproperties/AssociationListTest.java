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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssociationListTest {

    private AssociationList tested;
    public static final String VALUE = "[din]var1->input1,[din]var2->input2,[dout]var3->output1," +
            "[dout]var5->output2";

    public static final String VALUE_WITH_COMMA = "[din]var1->input1,[din]var2->input2,input22,input33," +
            "[dout]var3->output1,[dout]var5->output2,output22,ouput23";

    @Before
    public void setUp() {
        tested = new AssociationList();
    }

    @Test
    public void fromString() {
        AssociationList list = tested.fromString(VALUE);
        assertEquals(2, list.getInputs().size());
        assertEquals(2, list.getOutputs().size());
    }

    @Test
    public void fromStringWithComma() {
        AssociationList list = tested.fromString(VALUE_WITH_COMMA);
        assertEquals(2, list.getInputs().size());
        assertEquals(2, list.getOutputs().size());
    }
}