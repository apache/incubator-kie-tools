/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status.runtime;

import org.guvnor.ala.ui.client.widget.pipeline.stage.State;
import org.guvnor.ala.ui.model.PipelineStatus;
import org.guvnor.ala.ui.model.RuntimeStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuntimePresenterHelperTest {

    @Test
    public void testBuildRuntimeStatus() {
        assertEquals(RuntimeStatus.UNKNOWN,
                     RuntimePresenterHelper.buildRuntimeStatus(null));

        assertEquals(RuntimeStatus.RUNNING,
                     RuntimePresenterHelper.buildRuntimeStatus("RUNNING"));

        assertEquals(RuntimeStatus.STOPPED,
                     RuntimePresenterHelper.buildRuntimeStatus("STOPPED"));

        assertEquals(RuntimeStatus.UNKNOWN,
                     RuntimePresenterHelper.buildRuntimeStatus("UNKNOWN"));

        assertEquals(RuntimeStatus.UNKNOWN,
                     RuntimePresenterHelper.buildRuntimeStatus("whatever value"));
    }

    @Test
    public void testBuildStageState() {
        assertEquals(State.DONE,
                     RuntimePresenterHelper.buildStageState(null));

        assertEquals(State.EXECUTING,
                     RuntimePresenterHelper.buildStageState(PipelineStatus.RUNNING));

        assertEquals(State.ERROR,
                     RuntimePresenterHelper.buildStageState(PipelineStatus.ERROR));

        assertEquals(State.STOPPED,
                     RuntimePresenterHelper.buildStageState(PipelineStatus.STOPPED));
    }
}
