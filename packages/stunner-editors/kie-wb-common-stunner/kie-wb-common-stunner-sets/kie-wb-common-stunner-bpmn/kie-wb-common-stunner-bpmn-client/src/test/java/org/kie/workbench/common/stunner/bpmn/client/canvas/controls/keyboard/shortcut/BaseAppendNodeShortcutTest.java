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

import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.AbstractAppendNodeShortcut;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.mockito.Mock;

public abstract class BaseAppendNodeShortcutTest {

    @Mock
    protected ToolboxDomainLookups toolboxDomainLookups;

    @Mock
    protected DefinitionsCacheRegistry definitionsCacheRegistry;

    @Mock
    protected GeneralCreateNodeAction generalCreateNodeAction;

    private AbstractAppendNodeShortcut testedShortcut;

    @Before
    public void setUp() throws Exception {
        testedShortcut = getTestedShortcut();
    }

    public abstract AbstractAppendNodeShortcut getTestedShortcut();

    public abstract List<KeyboardEvent.Key> getAcceptableKeys();

    public abstract Object getAcceptableTargetDefinition();

    public abstract List<Object> getNotAcceptableTargetDefinitions();

    public abstract List<Element> getAcceptableSelectedElements();

    public abstract List<Element> getNotAcceptableSelectedElements();

    @Test
    public void testMatchesKeys() {
        final SoftAssertions softly = new SoftAssertions();

        Stream.of(KeyboardEvent.Key.values())
                .filter(key -> !getAcceptableKeys().contains(key))
                .forEach(key -> softly.assertThat(testedShortcut.matchesPressedKeys(key))
                        .as("It shouldn't react on key: " + key.name())
                        .isFalse());

        getAcceptableKeys()
                .forEach(key -> softly.assertThat(testedShortcut.matchesPressedKeys(key))
                        .as("It should react on key: " + key.name())
                        .isTrue());

        softly.assertAll();
    }

    @Test
    public void testCanAppendNodeOfDefinition() {
        final SoftAssertions softly = new SoftAssertions();

        getNotAcceptableTargetDefinitions()
                .forEach(definition -> softly.assertThat(testedShortcut.canAppendNodeOfDefinition(definition))
                        .as("It shouldn't be possible to add definition of a class: " + definition.getClass().getName())
                        .isFalse());

        softly.assertThat(testedShortcut.canAppendNodeOfDefinition(getAcceptableTargetDefinition()))
                .as("It should be possible to add definition of a class: " + getAcceptableTargetDefinition().getClass().getName())
                .isTrue();

        softly.assertAll();
    }

    @Test
    public void testMatchesSelectedElement() {
        final SoftAssertions softly = new SoftAssertions();

        getNotAcceptableSelectedElements()
                .forEach(element -> softly.assertThat(testedShortcut.matchesSelectedElement(element))
                        .as("It shouldn't react on selected element: " + ((Definition) element.getContent()).getDefinition())
                        .isFalse());

        getAcceptableSelectedElements()
                .forEach(element -> softly.assertThat(testedShortcut.matchesSelectedElement(element))
                        .as("It should react on selected element: " + ((Definition) element.getContent()).getDefinition())
                        .isTrue());

        softly.assertAll();
    }
}
