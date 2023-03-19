/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ActivityNotFoundPresenterTest {

    @Mock
    private ActivityNotFoundView view;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PlaceRequest placeRequest;

    private ActivityNotFoundPresenter presenter;

    @Before
    public void setUp() throws Exception {
        presenter = new ActivityNotFoundPresenter(view, placeManager);
        presenter.onStartup(placeRequest);
    }

    @Test
    public void testOnCloseNullPlaceIdentifier() {
        presenter.onClose();

        verifyZeroInteractions(placeManager);
    }

    @Test
    public void testOnCloseNonNullPlaceIdentifier() {
        final String placeIdentifier = "screen-id";
        when(placeRequest.getParameter("requestedPlaceIdentifier", null)).thenReturn(placeIdentifier);
        presenter.onClose();

        verify(placeManager).forceClosePlace(placeIdentifier);
    }
}
