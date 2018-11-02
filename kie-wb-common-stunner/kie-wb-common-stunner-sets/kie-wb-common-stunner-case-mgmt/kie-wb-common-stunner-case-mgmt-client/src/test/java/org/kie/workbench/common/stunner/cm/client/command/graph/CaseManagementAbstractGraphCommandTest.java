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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import org.kie.workbench.common.stunner.cm.client.command.CaseManagementAbstractCommandTest;
import org.kie.workbench.common.stunner.cm.client.command.CommandTestUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class CaseManagementAbstractGraphCommandTest extends CaseManagementAbstractCommandTest {

    protected Node<View<?>, Edge> parent;

    protected Node<View<?>, Edge> candidate;

    @SuppressWarnings("unchecked")
    public void setup() {
        super.setup();

        this.parent = CommandTestUtils.makeNode("uuid1",
                                                "parent",
                                                10.0,
                                                20.0,
                                                50.0,
                                                50.0);
        this.candidate = CommandTestUtils.makeNode("uuid2",
                                                   "candidate",
                                                   10.0,
                                                   20.0,
                                                   50.0,
                                                   50.0);
    }
}
