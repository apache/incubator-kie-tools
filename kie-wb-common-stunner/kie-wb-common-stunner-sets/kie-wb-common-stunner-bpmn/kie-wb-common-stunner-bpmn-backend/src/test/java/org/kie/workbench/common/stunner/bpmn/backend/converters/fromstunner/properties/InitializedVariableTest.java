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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.FormalExpression;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.InitializedVariable.InputConstant;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.VariableDeclaration;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class InitializedVariableTest {

    @Test
    public void urlDecodeConstants() throws UnsupportedEncodingException {
        String expected = "<<<#!!!#>>>";
        String encoded = URLEncoder.encode(expected, "UTF-8");

        VariableDeclaration variable = new VariableDeclaration("ID", "Object");
        InputConstant c = new InputConstant("PARENT", variable, encoded);

        Assignment assignment = c.getDataInputAssociation().getAssignment().get(0);

        FormalExpression to = (FormalExpression) assignment.getTo();
        assertEquals(Ids.dataInput("PARENT", "ID"), to.getBody());

        FormalExpression from = (FormalExpression) assignment.getFrom();
        assertEquals(asCData(expected), from.getBody());
    }
}