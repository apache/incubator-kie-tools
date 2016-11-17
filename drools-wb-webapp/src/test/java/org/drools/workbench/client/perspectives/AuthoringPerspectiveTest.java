/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.client.perspectives;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.client.wizard.ExamplesWizard;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class AuthoringPerspectiveTest {

    @Mock
    private ExamplesWizard wizard;

    @InjectMocks
    AuthoringPerspective perspective;

    @Test
    public void testOnLibraryContextSwitchEventWithProjectFromExample() throws Exception {

        LibraryContextSwitchEvent event = new LibraryContextSwitchEvent(
                LibraryContextSwitchEvent.EventType.PROJECT_FROM_EXAMPLE );

        perspective.onLibraryContextSwitchEvent( event );

        verify( wizard ).start();

    }

    @Test
    public void testOnLibraryContextSwitchEventWithOtherEvent() throws Exception {

        LibraryContextSwitchEvent event = new LibraryContextSwitchEvent(
                LibraryContextSwitchEvent.EventType.PROJECT_SELECTED );

        perspective.onLibraryContextSwitchEvent( event );

        verify( wizard, never() ).start();

    }
}