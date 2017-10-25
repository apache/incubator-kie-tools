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

package org.guvnor.ala.ui.client.widget.pipeline.stage;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.model.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class StagePresenterTest {

    private static final String STAGE_NAME = "STAGE_NAME";

    @Mock
    private StagePresenter.View view;

    private StagePresenter presenter;

    @Mock
    private Stage stage;

    @Before
    public void setUp() {
        presenter = new StagePresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetup() {
        when(stage.getName()).thenReturn(STAGE_NAME);

        presenter.setup(stage);
        verify(view,
               times(1)).setName(STAGE_NAME);
        verify(view,
               times(1)).setExecutingState(State.EXECUTING.name());
    }

    @Test
    public void testSetStateExecuting() {
        presenter.setState(State.EXECUTING);
        verify(view,
               times(1)).setExecutingState(State.EXECUTING.name());
    }

    @Test
    public void testSetStateDone() {
        presenter.setState(State.DONE);
        verify(view,
               times(1)).setDoneState(State.DONE.name());
    }

    @Test
    public void testSetStateError() {
        presenter.setState(State.ERROR);
        verify(view,
               times(1)).setErrorState(State.ERROR.name());
    }

    @Test
    public void testSetStateStopped() {
        presenter.setState(State.STOPPED);
        verify(view,
               times(1)).setStoppedState(State.STOPPED.name());
    }
}
