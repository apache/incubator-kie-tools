/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.backend.test;

import java.util.ArrayList;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.shared.test.TestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestRunnerServiceImplTest {

    @Mock
    TestService testService1;

    @Mock
    TestService testService2;

    private TestRunnerServiceImpl testRunnerService;

    @Before
    public void setUp() throws Exception {
        final Instance services = mock(Instance.class);

        final ArrayList<Object> list = new ArrayList<>();

        list.add(testService1);
        list.add(testService2);
        doReturn(list.iterator()).when(services).iterator();

        testRunnerService = new TestRunnerServiceImpl(services);
    }

    @Test
    public void runAllTests() throws Exception {
        final Path path = mock(Path.class);
        testRunnerService.runAllTests("id", path);

        verify(testService1).runAllTests("id", path);
        verify(testService2).runAllTests("id", path);
    }

    @Test
    public void runAllTestsCustomTestResultEvent() throws Exception {
        final Path path = mock(Path.class);
        final Event event = mock(Event.class);
        testRunnerService.runAllTests("id", path, event);

        verify(testService1).runAllTests("id", path, event);
        verify(testService2).runAllTests("id", path, event);
    }
}