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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;

import static org.junit.Assert.assertEquals;

public class BaseSubprocessTaskExecutionSetTest {

    @Test
    public void testGetIsAsync() {
        BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        Assert.assertEquals(new IsAsync(), a.getIsAsync());
    }

    @Test
    public void testSetIsAsync() {
        final IsAsync isAsyncFalse = new IsAsync(false);
        final IsAsync isAsyncTrue = new IsAsync(true);

        final BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        a.setIsAsync(isAsyncFalse);

        final BaseSubprocessTaskExecutionSet b = new BaseSubprocessTaskExecutionSet();
        b.setIsAsync(isAsyncTrue);

        Assert.assertEquals(isAsyncFalse, a.getIsAsync());
        Assert.assertEquals(isAsyncTrue, b.getIsAsync());
    }

    @Test
    public void testGetSlaDueDate() {
        BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        Assert.assertEquals(new SLADueDate(), a.getSlaDueDate());
    }

    @Test
    public void testSetSlaDueDate() {
        final String SLA_DUE_DATE_VALUE = "12/25/1983";
        final SLADueDate slaDueDate = new SLADueDate(SLA_DUE_DATE_VALUE);

        final BaseSubprocessTaskExecutionSet a = new BaseSubprocessTaskExecutionSet();
        a.setSlaDueDate(slaDueDate);

        Assert.assertEquals(slaDueDate, a.getSlaDueDate());
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
