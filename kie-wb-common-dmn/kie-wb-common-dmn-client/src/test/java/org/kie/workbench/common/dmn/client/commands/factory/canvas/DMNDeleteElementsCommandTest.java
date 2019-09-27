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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.factory.graph.DMNDeleteElementsGraphCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDeleteElementsCommandTest {

    @Test
    public void testNewGraphCommand() {

        final ArrayList<Element> elements = new ArrayList<>();
        final Element element = mock(Element.class);
        when(element.getUUID()).thenReturn("uuid");
        elements.add(element);
        final DMNDeleteElementsCommand cmd = new DMNDeleteElementsCommand(elements);
        final Command<GraphCommandExecutionContext, RuleViolation> actual = cmd.newGraphCommand(null);
        assertTrue(actual instanceof DMNDeleteElementsGraphCommand);
    }
}