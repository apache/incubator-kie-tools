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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import org.eclipse.bpmn2.BaseElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessage;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageKeys;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest.Mode;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.BPMNElementDecorators;
import org.kie.workbench.common.stunner.core.validation.Violation;

public abstract class AbstractConverter {

    private final Mode mode;

    public AbstractConverter(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    protected MarshallingMessage getNotFoundMessage(BaseElement baseElement) {
        return MarshallingMessage.builder()
                .type(Violation.Type.WARNING)
                .messageKey(MarshallingMessageKeys.ignoredUnknownElement)
                .messageArguments(BPMNElementDecorators.baseElementDecorator().getName(baseElement))
                .build();
    }
}