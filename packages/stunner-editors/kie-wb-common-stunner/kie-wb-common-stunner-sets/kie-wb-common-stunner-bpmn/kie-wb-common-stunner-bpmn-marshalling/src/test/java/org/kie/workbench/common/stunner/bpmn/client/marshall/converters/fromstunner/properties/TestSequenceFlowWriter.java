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

import org.eclipse.bpmn2.ScriptTask;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

class TestSequenceFlowWriter {

    final PropertyWriterFactory propertyWriter = new PropertyWriterFactory();

    SequenceFlowPropertyWriter sequenceFlowOf(String id) {
        org.eclipse.bpmn2.SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId(id);
        return propertyWriter.of(sequenceFlow);
    }

    PropertyWriter nodeOf(String id, float x, float y, float width, float height) {
        ScriptTask el = bpmn2.createScriptTask();
        el.setId(id);
        Bounds sb = Bounds.create(x, y, x + width, y + height);
        PropertyWriter p = propertyWriter.of(el);
        p.setBounds(sb);
        return p;
    }
}
