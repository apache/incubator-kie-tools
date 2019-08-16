/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;

import static org.junit.Assert.assertEquals;

public class BaseSubprocessTaskExecutionSetTest {

    @Test
    public void testGetSlaDueDate() {
        BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        Assert.assertEquals(a.getSlaDueDate(), new SLADueDate());
    }

    @Test
    public void testSetSlaDueDate() {
        final String SLA_DUE_DATE_VALUE = "12/25/1983";
        final SLADueDate slaDueDate = new SLADueDate(SLA_DUE_DATE_VALUE);

        final BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        a.setSlaDueDate(slaDueDate);

        Assert.assertEquals(a.getSlaDueDate(), slaDueDate);
    }

    @Test
    public void testHashCode() {
        BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        BaseSubprocessTaskExecutionSet b = new BaseSubprocessTaskExecutionSet();
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testEquals() {
        BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        BaseSubprocessTaskExecutionSet b = new BaseSubprocessTaskExecutionSet();
        Assert.assertEquals(a, b);
    }
}
