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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters;

import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingMessageDecorator;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.BpmnNode;

public class BPMNElementDecorators {

    public static <T extends FlowElement> MarshallingMessageDecorator<T> flowElementDecorator() {
        return MarshallingMessageDecorator.of(o -> Optional.ofNullable(o.getName())
                                                      .orElseGet(o::getId),
                                              g -> g.getClass().getName());
    }

    public static <T extends BaseElement> MarshallingMessageDecorator<T> baseElementDecorator() {
        return MarshallingMessageDecorator.of(BaseElement::getId,
                                              g -> g.getClass().getName());
    }

    public static <T extends BpmnNode> MarshallingMessageDecorator<T> bpmnNodeDecorator() {
        return MarshallingMessageDecorator.of(o ->
                                                      Optional.ofNullable(o.value()
                                                                                  .getContent()
                                                                                  .getDefinition()
                                                                                  .getGeneral()
                                                                                  .getName()
                                                                                  .getValue())
                                                              .orElseGet(() -> o.value().getUUID()),
                                              bpmnNode -> bpmnNode.value()
                                                      .getContent()
                                                      .getDefinition()
                                                      .getClass()
                                                      .getName());
    }

    public static <T> MarshallingMessageDecorator<Result<T>> resultBpmnDecorator() {
        return MarshallingMessageDecorator.of(r ->
                                              {
                                                  BpmnNode o = (BpmnNode) r.value();
                                                  return Optional.ofNullable(o.value()
                                                                                     .getContent()
                                                                                     .getDefinition()
                                                                                     .getGeneral()
                                                                                     .getName()
                                                                                     .getValue())
                                                          .orElseGet(() -> o.value().getUUID());
                                              },
                                              r -> {
                                                  BpmnNode o1 = (BpmnNode) r.value();
                                                  return o1.value()
                                                          .getContent()
                                                          .getDefinition()
                                                          .getClass()
                                                          .getName();
                                              });
    }
}
