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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.ParsedAssignmentsInfo;

import static org.junit.Assert.assertEquals;

public class ParsedAssignmentsInfoTest {

    @Test
    public void fromString() {
        String original =
                "|input1:String,input2:String||output1:String,output2:String|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv2,[dout]output2->pv2";
        assertParseUnparse(original);
    }

    @Test
    public void fromString2() {
        String original =
                "||IntermediateMessageEventCatchingOutputVar1:String||[dout]IntermediateMessageEventCatchingOutputVar1->var1";
        assertParseUnparse(original);
    }

    private void assertParseUnparse(String original) {
        ParsedAssignmentsInfo assignmentsInfo =
                ParsedAssignmentsInfo.fromString(original);

        String s1 = assignmentsInfo.toString();
        String s2 = ParsedAssignmentsInfo.fromString(s1).toString();

        assertEquals(original, s2);
    }
}