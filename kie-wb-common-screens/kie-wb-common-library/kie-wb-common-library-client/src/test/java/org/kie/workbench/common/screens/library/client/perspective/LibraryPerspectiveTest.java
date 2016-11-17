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
package org.kie.workbench.common.screens.library.client.perspective;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryDocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDocks;

import static org.mockito.Mockito.verify;

@RunWith( GwtMockitoTestRunner.class )
public class LibraryPerspectiveTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private UberfireDocks uberfireDocks;

    @Mock
    private LibraryDocks libraryDocks;

    private LibraryPerspective perspective;

    @Test
    public void testSetupDocks() throws Exception {

        perspective = new LibraryPerspective( placeManager, uberfireDocks, libraryDocks );

        perspective.setupDocks();

        verify( libraryDocks ).start();

    }
}