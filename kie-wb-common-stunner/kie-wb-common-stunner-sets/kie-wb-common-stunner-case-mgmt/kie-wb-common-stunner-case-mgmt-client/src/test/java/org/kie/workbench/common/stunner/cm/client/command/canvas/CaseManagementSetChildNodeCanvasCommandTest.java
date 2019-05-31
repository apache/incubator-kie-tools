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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementSetChildNodeCanvasCommandTest extends CaseManagementAbstractCanvasCommandTest {

    private OptionalInt index;
    private Optional<Node<View<?>, Edge>> originalParent;
    private OptionalInt originalIndex;

    @Before
    public void setup() {
        super.setup();
        this.index = OptionalInt.of(0);
        this.originalParent = Optional.empty();
        this.originalIndex = OptionalInt.empty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkExecute() {
        setChildNode(parent,
                     candidate,
                     index,
                     originalParent,
                     originalIndex);

        verify(canvasHandler).addChild(eq(parent),
                                       eq(candidate),
                                       eq(0));
    }

    private CaseManagementSetChildNodeCanvasCommand setChildNode(final Node<View<?>, Edge> parent,
                                                                 final Node<View<?>, Edge> candidate,
                                                                 final OptionalInt index,
                                                                 final Optional<Node<View<?>, Edge>> originalParent,
                                                                 final OptionalInt originalIndex) {
        final CaseManagementSetChildNodeCanvasCommand command = new CaseManagementSetChildNodeCanvasCommand(parent,
                                                                                                            candidate,
                                                                                                            index,
                                                                                                            originalParent,
                                                                                                            originalIndex);

        command.execute(canvasHandler);
        return command;
    }

    @Test
    public void checkUndo() {
        //Setup the relationship to undo
        final CaseManagementSetChildNodeCanvasCommand command = setChildNode(parent,
                                                                             candidate,
                                                                             index,
                                                                             originalParent,
                                                                             originalIndex);

        //Perform test
        command.undo(canvasHandler);

        verify(canvasHandler).removeChild(eq(parent),
                                          eq(candidate));
    }
}
