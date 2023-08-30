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


package org.kie.workbench.common.stunner.bpmn.client.canvas.controls.keyboard.shortcut;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.AbstractAppendNodeShortcut;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AppendEmbeddedSubprocessShortcutTest extends BaseAppendNodeShortcutTest {

    @Override
    public AbstractAppendNodeShortcut getTestedShortcut() {
        return new AppendEmbeddedSubprocessShortcut(toolboxDomainLookups,
                                                    definitionsCacheRegistry,
                                                    generalCreateNodeAction);
    }

    @Override
    public List<KeyboardEvent.Key> getAcceptableKeys() {
        return Collections.singletonList(KeyboardEvent.Key.S);
    }

    @Override
    public Object getAcceptableTargetDefinition() {
        return BpmnNode.EMBEDDED_SUBPROCESS.getDefinition();
    }

    @Override
    public List<Object> getNotAcceptableTargetDefinitions() {
        return Stream.of(BpmnNode.values())
                .map(node -> node.getDefinition())
                .filter(definition -> definition != getAcceptableTargetDefinition())
                .collect(Collectors.toList());
    }

    @Override
    public List<Element> getAcceptableSelectedElements() {
        return Stream.of(BpmnNode.values())
                .map(node -> node.getElement())
                .filter(element -> !getNotAcceptableSelectedElements().contains(element))
                .collect(Collectors.toList());
    }

    @Override
    public List<Element> getNotAcceptableSelectedElements() {
        return Collections.singletonList(BpmnNode.NONE_END_EVENT.getElement());
    }
}
