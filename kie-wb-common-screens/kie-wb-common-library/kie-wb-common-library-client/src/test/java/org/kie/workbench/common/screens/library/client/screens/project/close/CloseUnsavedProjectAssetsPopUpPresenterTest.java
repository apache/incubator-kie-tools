/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.close;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloseUnsavedProjectAssetsPopUpPresenterTest {

    @Mock
    private CloseUnsavedProjectAssetsPopUpPresenter.View view;

    @Mock
    private ManagedInstance<CloseUnsavedProjectAssetsPopUpListItemPresenter> closeUnsavedProjectAssetsPopUpListItemPresenters;

    @Mock
    private CloseUnsavedProjectAssetsPopUpListItemPresenter closeUnsavedProjectAssetsPopUpListItemPresenter;

    @InjectMocks
    private CloseUnsavedProjectAssetsPopUpPresenter presenter;

    @Before
    public void setup() {
        doReturn(closeUnsavedProjectAssetsPopUpListItemPresenter).when(closeUnsavedProjectAssetsPopUpListItemPresenters).get();
    }

    @Test
    public void showTest() {
        final Command proceedCallback = mock(Command.class);
        final Command cancelCallback = mock(Command.class);

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(createPath("default://project/")).when(project).getRootPath();
        doReturn("project").when(project).getName();

        final PathPlaceRequest pathPlaceRequest = mock(PathPlaceRequest.class);
        doReturn(createPath("default://project/package/Asset.java")).when(pathPlaceRequest).getPath();

        final List<PlaceRequest> uncloseablePlaces = new ArrayList<>();
        uncloseablePlaces.add(new DefaultPlaceRequest("screen"));
        uncloseablePlaces.add(pathPlaceRequest);

        presenter.show(project,
                       uncloseablePlaces,
                       Optional.of(proceedCallback),
                       Optional.of(cancelCallback));

        verify(view).clearPlaces();
        verify(closeUnsavedProjectAssetsPopUpListItemPresenter).setup("screen");
        verify(closeUnsavedProjectAssetsPopUpListItemPresenter).setup("package/Asset.java");
        verify(view, times(2)).addPlace(any());
        verify(view).show("project");
    }

    @Test
    public void proceedTest() {
        presenter.proceedCallback = Optional.of(mock(Command.class));

        presenter.proceed();

        verify(view).hide();
        verify(presenter.proceedCallback.get()).execute();
    }

    @Test
    public void cancelTest() {
        presenter.cancelCallback = Optional.of(mock(Command.class));

        presenter.cancel();

        verify(view).hide();
        verify(presenter.cancelCallback.get()).execute();
    }

    private ObservablePath createPath(final String uri) {
        final ObservablePath path = mock(ObservablePath.class);
        doReturn(uri).when(path).toURI();

        return path;
    }
}
