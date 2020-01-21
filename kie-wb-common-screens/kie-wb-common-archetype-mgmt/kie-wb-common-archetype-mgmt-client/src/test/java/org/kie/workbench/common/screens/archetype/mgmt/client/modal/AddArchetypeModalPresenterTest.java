/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.archetype.mgmt.client.modal;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.ArchetypeAlreadyExistsException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.InvalidArchetypeException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.exceptions.MavenExecutionException;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddArchetypeModalPresenterTest {

    private static final String ARTIFACT_ID = "artifact-id";
    private static final String VERSION = "version";
    private static final String GROUP_ID = "group-id";

    private AddArchetypeModalPresenter presenter;

    @Mock
    private AddArchetypeModalPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private ArchetypeService archetypeService;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Before
    public void setup() {
        presenter = new AddArchetypeModalPresenter(view,
                                                   ts,
                                                   new CallerMock<>(archetypeService),
                                                   busyIndicatorView);
    }

    @Test
    public void postConstructTest() {
        presenter.postConstruct();

        verify(view).init(presenter);
    }

    @Test
    public void showTest() {
        presenter.show();

        verify(view).resetAll();
        verify(view).show();
    }

    @Test
    public void hideTest() {
        presenter.hide();

        verify(view).hide();
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }

    @Test
    public void showBusyIndicatorTest() {
        final String msg = "Loading";

        presenter.showBusyIndicator(msg);

        verify(busyIndicatorView).showBusyIndicator(msg);
    }

    @Test
    public void hideBusyIndicatorTest() {
        presenter.hideBusyIndicator();

        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void addWhenInvalidGroupIdTest() {
        doReturn(null).when(view).getArchetypeGroupId();
        doReturn(ARTIFACT_ID).when(view).getArchetypeArtifactId();
        doReturn(VERSION).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).clearErrors();
        verify(view).showArchetypeGroupIdError(anyString());
        verify(archetypeService, never()).add(any(GAV.class));
    }

    @Test
    public void addWhenInvalidArtifactIdTest() {
        doReturn(GROUP_ID).when(view).getArchetypeGroupId();
        doReturn(null).when(view).getArchetypeArtifactId();
        doReturn(VERSION).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).clearErrors();
        verify(view).showArchetypeArtifactIdError(anyString());
        verify(archetypeService, never()).add(any(GAV.class));
    }

    @Test
    public void addWhenInvalidVersionIdTest() {
        doReturn(GROUP_ID).when(view).getArchetypeGroupId();
        doReturn(ARTIFACT_ID).when(view).getArchetypeArtifactId();
        doReturn(null).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).clearErrors();
        verify(view).showArchetypeVersionError(anyString());
        verify(archetypeService, never()).add(any(GAV.class));
    }

    @Test
    public void addWhenAllFieldsInvalidTest() {
        doReturn(null).when(view).getArchetypeGroupId();
        doReturn(null).when(view).getArchetypeArtifactId();
        doReturn(null).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).clearErrors();
        verify(view).showArchetypeGroupIdError(anyString());
        verify(view).showArchetypeArtifactIdError(anyString());
        verify(view).showArchetypeVersionError(anyString());
        verify(archetypeService, never()).add(any(GAV.class));
    }

    @Test
    public void addSuccessTest() {
        final GAV gav = createGav();

        doReturn(GROUP_ID).when(view).getArchetypeGroupId();
        doReturn(ARTIFACT_ID).when(view).getArchetypeArtifactId();
        doReturn(VERSION).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).enableAddButton(false);
        verify(view).enableFields(false);
        verify(busyIndicatorView).showBusyIndicator(anyString());

        verify(archetypeService).add(gav);

        verify(view).enableAddButton(true);
        verify(view).enableFields(true);
        verify(busyIndicatorView).hideBusyIndicator();

        verify(view).hide();

        verify(view, never()).showArchetypeGroupIdError(anyString());
        verify(view, never()).showArchetypeArtifactIdError(anyString());
        verify(view, never()).showArchetypeVersionError(anyString());
    }

    @Test
    public void addSuccessWhenNeedsTrimTest() {
        final GAV gav = createGav();

        final String spaces = "     ";

        doReturn(spaces + GROUP_ID).when(view).getArchetypeGroupId();
        doReturn(ARTIFACT_ID + spaces).when(view).getArchetypeArtifactId();
        doReturn(spaces + VERSION + spaces).when(view).getArchetypeVersion();

        presenter.add();

        verify(view).enableAddButton(false);
        verify(view).enableFields(false);
        verify(busyIndicatorView).showBusyIndicator(anyString());

        verify(archetypeService).add(gav);

        verify(view).enableAddButton(true);
        verify(view).enableFields(true);
        verify(busyIndicatorView).hideBusyIndicator();

        verify(view).hide();

        verify(view, never()).showArchetypeGroupIdError(anyString());
        verify(view, never()).showArchetypeArtifactIdError(anyString());
        verify(view, never()).showArchetypeVersionError(anyString());
    }

    @Test
    public void addWhenServiceThrowsArchetypeAlreadyExistsExceptionTest() {
        addWhenServiceThrowsHandledException(new ArchetypeAlreadyExistsException());
    }

    @Test
    public void addWhenServiceThrowsMavenExecutionExceptionTest() {
        addWhenServiceThrowsHandledException(new MavenExecutionException());
    }

    @Test
    public void addWhenServiceThrowsInvalidArchetypeExceptionTest() {
        addWhenServiceThrowsHandledException(new InvalidArchetypeException());
    }

    private void addWhenServiceThrowsHandledException(final Throwable throwable) {
        doReturn(GROUP_ID).when(view).getArchetypeGroupId();
        doReturn(ARTIFACT_ID).when(view).getArchetypeArtifactId();
        doReturn(VERSION).when(view).getArchetypeVersion();

        doThrow(throwable).when(archetypeService).add(any(GAV.class));

        presenter.add();

        verify(view).showGeneralError(anyString());
    }

    private GAV createGav() {
        return new GAV(GROUP_ID,
                       ARTIFACT_ID,
                       VERSION);
    }
}