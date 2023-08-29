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


package org.kie.workbench.common.stunner.bpmn.definition;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextAnnotationTest {

    private TextAnnotation tested = new TextAnnotation();

    @Test
    public void getLabels() {
        assertEquals(3, tested.getLabels().size());
        assertTrue(tested.getLabels().contains("all"));
        assertTrue(tested.getLabels().contains("lane_child"));
        assertTrue(tested.getLabels().contains("text_annotation"));
    }

    @Test
    public void getGeneral() {
        assertNotNull(tested.getGeneral());
    }

    @Test
    public void setGeneral() {
        BPMNGeneralSet general = new BPMNGeneralSet();
        tested.setGeneral(general);
        assertEquals(general, tested.getGeneral());
    }

    @Test
    public void setName() {
        Name name = new Name(this.getClass().getSimpleName());
        tested.getGeneral().setName(name);
        assertEquals(name, tested.getGeneral().getName());
    }

    @Test
    public void setDocumentation() {
        Documentation documentation = new Documentation(this.getClass().getSimpleName());
        tested.getGeneral().setDocumentation(documentation);
        assertEquals(documentation, tested.getGeneral().getDocumentation());
    }

    @Test
    public void testHashCode() {
        assertEquals(new TextAnnotation().hashCode(), tested.hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(new TextAnnotation(), tested);
        assertNotEquals(new TextAnnotation(), new Object());
    }
}
