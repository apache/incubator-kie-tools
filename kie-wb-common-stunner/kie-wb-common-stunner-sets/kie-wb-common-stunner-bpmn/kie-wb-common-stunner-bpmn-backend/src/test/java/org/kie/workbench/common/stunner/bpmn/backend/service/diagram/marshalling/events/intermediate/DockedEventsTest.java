/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.BPMNDiagramMarshallerBaseTest;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPositionCommand;
import org.mockito.ArgumentCaptor;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DockedEventsTest extends BPMNDiagramMarshallerBaseTest {

    private static final String JBPM_7645 = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/JBPM_7645.bpmn";

    @Before
    public void before() {
        init();
    }

    @Test
    public void testDockedElementProcessingOrder() throws Exception {
        final String TASK_ID = "_02DDF5FF-E1E4-4DA3-9971-70CFB158A08C";
        final String DOCKED_NODE_ID = "_6A26F0A2-3368-4769-B9E9-A6290530ED8F";
        unmarshall(marshaller, JBPM_7645);
        ArgumentCaptor<Command> cmd = ArgumentCaptor.forClass(Command.class);
        verify(api.commandManager, times(8)).execute(any(), cmd.capture());
        List<Command> commands = cmd.getAllValues();
        List<UpdateElementPositionCommand> posCmds = commands.stream()
                .filter(UpdateElementPositionCommand.class::isInstance)
                .map(UpdateElementPositionCommand.class::cast)
                .collect(toList());
        assertThat(posCmds.get(0).getNode().getUUID()).isEqualTo(TASK_ID);
        assertThat(posCmds.get(1).getNode().getUUID()).isEqualTo(DOCKED_NODE_ID);
    }
}
