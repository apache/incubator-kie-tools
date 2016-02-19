/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.wizard.config.process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.client.widget.config.process.ProcessConfigPresenter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessConfigPagePresenterTest {

    @Mock
    ProcessConfigPresenter processConfigPresenter;

    @InjectMocks
    ProcessConfigPagePresenter presenter;

    @Test
    public void testClear() {
        presenter.clear();

        verify(processConfigPresenter).clear();
    }

    @Test
    public void testBuildProcessConfig() {
        presenter.buildProcessConfig();

        verify(processConfigPresenter).buildProcessConfig();
    }

    @Test
    public void testIsComplete() {
        final Callback callback = mock(Callback.class);

        presenter.isComplete(callback);

        verify(callback).callback(true);
    }

}