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

package org.guvnor.ala.ui.client.widget.pipeline;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PipelinePresenterTest {

    @Mock
    private PipelinePresenter.View view;

    private PipelinePresenter presenter;

    @Before
    public void setUp() {
        presenter = new PipelinePresenter(view);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testAddStage() {
        IsElement stage = mock(IsElement.class);
        presenter.addStage(stage);
        verify(view,
               times(1)).addStage(stage);
    }

    @Test
    public void testClearStages() {
        presenter.clearStages();
        verify(view,
               times(1)).clearStages();
    }
}
