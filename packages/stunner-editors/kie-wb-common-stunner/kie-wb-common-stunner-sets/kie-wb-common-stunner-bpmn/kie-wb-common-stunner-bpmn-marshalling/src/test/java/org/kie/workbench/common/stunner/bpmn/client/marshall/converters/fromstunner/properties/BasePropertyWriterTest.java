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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import org.eclipse.bpmn2.BaseElement;

import static org.mockito.Mockito.mock;

/**
 * Intended only for testing the abstract BasePropertyWriter class methods, other property writer tests must extend
 * AbstractBasePropertyWriterTest.
 */
public class BasePropertyWriterTest extends AbstractBasePropertyWriterTest<BasePropertyWriter, BaseElement> {

    @Override
    protected BasePropertyWriter newPropertyWriter(BaseElement baseElement, VariableScope variableScope) {
        return new BasePropertyWriterMock(baseElement, variableScope);
    }

    @Override
    protected BaseElement mockElement() {
        return mock(BaseElement.class);
    }

    private class BasePropertyWriterMock extends BasePropertyWriter {

        BasePropertyWriterMock(BaseElement baseElement, VariableScope variableScope) {
            super(baseElement, variableScope);
        }
    }
}
