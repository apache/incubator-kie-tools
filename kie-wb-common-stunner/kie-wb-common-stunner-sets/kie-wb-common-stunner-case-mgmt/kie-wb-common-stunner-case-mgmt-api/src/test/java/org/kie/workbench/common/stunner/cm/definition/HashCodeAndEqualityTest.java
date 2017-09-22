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

package org.kie.workbench.common.stunner.cm.definition;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HashCodeAndEqualityTest {

    @Test
    public void testCaseManagementDiagramEquals() {
        CaseManagementDiagram.CaseManagementDiagramBuilder builder = new CaseManagementDiagram.CaseManagementDiagramBuilder();
        CaseManagementDiagram a = builder.build();
        builder = new CaseManagementDiagram.CaseManagementDiagramBuilder();
        CaseManagementDiagram b = builder.build();
        assertEquals(a,
                     b);
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
    }

    @Test
    public void testCaseManagementDiagramHashCode() {
        CaseManagementDiagram.CaseManagementDiagramBuilder builder = new CaseManagementDiagram.CaseManagementDiagramBuilder();
        CaseManagementDiagram a = builder.build();
        builder = new CaseManagementDiagram.CaseManagementDiagramBuilder();
        CaseManagementDiagram b = builder.build();
        assertTrue(a.hashCode() == b.hashCode());
    }

    @Test
    public void testReusableSubprocessEquals() {
        ReusableSubprocess.ReusableSubprocessBuilder builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess a = builder.build();
        builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess b = builder.build();
        assertFalse(a.equals(19));
        assertFalse(a.equals(null));
        assertEquals(a,
                     b);
    }

    @Test
    public void testReusableSubprocessHashCode() {
        ReusableSubprocess.ReusableSubprocessBuilder builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess a = builder.build();
        builder = new ReusableSubprocess.ReusableSubprocessBuilder();
        ReusableSubprocess b = builder.build();
        assertTrue(a.hashCode() - b.hashCode() == 0);
    }
}
