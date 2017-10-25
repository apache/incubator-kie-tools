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

package org.guvnor.ala.ui.client.wizard.container;

import java.util.ArrayList;

import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.validation.ProvisioningClientValidationService;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.widget.artifact.ArtifactSelectorPresenter;
import org.guvnor.ala.ui.service.ProvisioningValidationService;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.SERVICE_CALLER_EXCEPTION_MESSAGE;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.prepareServiceCallerError;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_AllFieldsMustBeCompletedErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_ContainerNameAlreadyInUseErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_InvalidContainerNameErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_LoadGAVErrorMessage;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerConfigPresenterTest {

    private static final String CONTAINER_NAME_VALUE = "CONTAINER_NAME_VALUE";

    private static final String GROUP_ID_VALUE = "GROUP_ID_VALUE";

    private static final String ARTIFACT_ID_VALUE = "ARTIFACT_ID_VALUE";

    private static final String VERSION_VALUE = "VERSION_VALUE";

    private static final String JAR_PATH = "JAR_PATH";

    private static final String ERROR_MESSAGE = "ERROR_MESSAGE";

    @Mock
    private ContainerConfigPresenter.View view;

    @Mock
    private ArtifactSelectorPresenter artifactSelector;

    @Mock
    private ArtifactSelectorPresenter.View artifactSelectorView;

    @Mock
    private TranslationService translationService;

    @Mock
    private PopupHelper popupHelper;

    private CallerMock<M2RepoService> m2RepoServiceCaller;

    @Mock
    private M2RepoService m2RepoService;

    @Mock
    private ProvisioningValidationService provisioningValidationService;

    private Caller<ProvisioningValidationService> provisioningValidationServiceCaller;

    private ProvisioningClientValidationService provisioningClientValidationService;

    private ContainerConfigPresenter presenter;

    @Before
    public void setUp() {
        when(artifactSelector.getView()).thenReturn(artifactSelectorView);

        m2RepoServiceCaller = spy(new CallerMock<>(m2RepoService));

        provisioningValidationServiceCaller = new CallerMock<>(provisioningValidationService);
        provisioningClientValidationService = new ProvisioningClientValidationService(provisioningValidationServiceCaller);

        presenter = spy(new ContainerConfigPresenter(view,
                                                     artifactSelector,
                                                     translationService,
                                                     popupHelper,
                                                     m2RepoServiceCaller,
                                                     provisioningClientValidationService));
        presenter.init();
        verify(view,
               times(1)).init(presenter);
        verify(artifactSelector,
               times(1)).setArtifactSelectHandler(any());
        verify(view,
               times(1)).setArtifactSelectorPresenter(artifactSelectorView);
    }

    @Test
    public void testGetView() {
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testClear() {
        presenter.clear();
        verify(view,
               times(1)).clear();
        verify(artifactSelector,
               times(1)).clear();
    }

    @Test
    public void testValidateForSubmit() {
        when(translationService.getTranslation(ContainerConfigPresenter_AllFieldsMustBeCompletedErrorMessage)).thenReturn(ERROR_MESSAGE);

        presenter.clear();
        //nothing was completed, the form is not valid.
        assertFalse(presenter.validateForSubmit());
        verify(view,
               times(1)).showFormError(ERROR_MESSAGE);
        verify(view,
               never()).clearFormError();

        //emulate the container name was completed.
        when(view.getContainerName()).thenReturn(CONTAINER_NAME_VALUE);
        when(provisioningValidationService.isValidContainerName(CONTAINER_NAME_VALUE)).thenReturn(true);
        presenter.onContainerNameChange();
        assertFalse(presenter.validateForSubmit());
        verify(view,
               times(2)).showFormError(ERROR_MESSAGE);
        verify(view,
               times(1)).clearFormError();

        //emulate the container name and groupId was completed
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        assertFalse(presenter.validateForSubmit());
        verify(view,
               times(3)).showFormError(ERROR_MESSAGE);
        verify(view,
               times(1)).clearFormError();

        //emulate the container name, groupId and artifactId was completed
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        assertFalse(presenter.validateForSubmit());
        verify(view,
               times(4)).showFormError(ERROR_MESSAGE);
        verify(view,
               times(1)).clearFormError();

        //emulate the container name, groupId, artifactId and version was completed
        when(view.getVersion()).thenReturn(VERSION_VALUE);
        assertTrue(presenter.validateForSubmit());
        verify(view,
               times(4)).showFormError(ERROR_MESSAGE);
        verify(view,
               times(2)).clearFormError();
    }

    @Test
    public void testGetContainerConfig() {
        when(view.getContainerName()).thenReturn(CONTAINER_NAME_VALUE);
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        when(view.getVersion()).thenReturn(VERSION_VALUE);
        ContainerConfig containerConfig = presenter.getContainerConfig();
        assertEquals(CONTAINER_NAME_VALUE,
                     containerConfig.getName());
        assertEquals(GROUP_ID_VALUE,
                     containerConfig.getGroupId());
        assertEquals(ARTIFACT_ID_VALUE,
                     containerConfig.getArtifactId());
        assertEquals(VERSION_VALUE,
                     containerConfig.getVersion());
    }

    @Test
    public void testOnArtifactSelectedSuccessful() {
        GAV gav = new GAV(GROUP_ID_VALUE,
                          ARTIFACT_ID_VALUE,
                          VERSION_VALUE);
        when(m2RepoService.loadGAVFromJar(JAR_PATH)).thenReturn(gav);
        //emulate the returned value was properly loaded into the view.
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        when(view.getVersion()).thenReturn(VERSION_VALUE);

        presenter.onArtifactSelected(JAR_PATH);

        verify(m2RepoService,
               times(1)).loadGAVFromJar(JAR_PATH);
        verify(view,
               times(4)).clearFormError();
        verify(view,
               times(1)).setGroupId(GROUP_ID_VALUE);
        verify(presenter,
               times(1)).onGroupIdChange();
        verify(view,
               times(1)).setArtifactId(ARTIFACT_ID_VALUE);
        verify(presenter,
               times(1)).onArtifactIdChange();
        verify(view,
               times(1)).setVersion(VERSION_VALUE);
        verify(presenter,
               times(1)).onVersionChange();
    }

    @Test
    public void testOnArtifactSelectedFailed() {
        when(translationService.format(ContainerConfigPresenter_LoadGAVErrorMessage,
                                       SERVICE_CALLER_EXCEPTION_MESSAGE)).thenReturn(ERROR_MESSAGE);

        prepareServiceCallerError(m2RepoService,
                                  m2RepoServiceCaller);

        presenter.onArtifactSelected(JAR_PATH);
        verify(view,
               times(1)).clearFormError();
        verify(popupHelper,
               times(1)).showErrorPopup(ERROR_MESSAGE);
        verify(view,
               times(1)).setGroupId(EMPTY_STRING);
        verify(view,
               times(1)).setArtifactId(EMPTY_STRING);
        verify(view,
               times(1)).setVersion(EMPTY_STRING);
    }

    @Test
    public void testOnContainerNameChangeValid() {
        when(view.getContainerName()).thenReturn(CONTAINER_NAME_VALUE);
        when(provisioningValidationService.isValidContainerName(CONTAINER_NAME_VALUE)).thenReturn(true);
        presenter.onContainerNameChange();
        verify(view,
               times(1)).clearContainerNameHelpText();
        verify(view,
               times(1)).clearFormError();
        verify(view,
               times(1)).setContainerNameStatus(FormStatus.VALID);
    }

    @Test
    public void testOnContainerNameChangeValidButAlreadyInUse() {
        ArrayList<String> alreadyInUseContainerNames = new ArrayList<>();
        alreadyInUseContainerNames.add(CONTAINER_NAME_VALUE);
        presenter.setup(alreadyInUseContainerNames);

        when(view.getContainerName()).thenReturn(CONTAINER_NAME_VALUE);
        when(provisioningValidationService.isValidContainerName(CONTAINER_NAME_VALUE)).thenReturn(true);
        when(translationService.getTranslation(ContainerConfigPresenter_ContainerNameAlreadyInUseErrorMessage)).thenReturn(ERROR_MESSAGE);
        presenter.onContainerNameChange();
        verify(view,
               times(1)).clearContainerNameHelpText();
        verify(view,
               times(1)).clearFormError();
        verify(view,
               times(1)).setContainerNameStatus(FormStatus.ERROR);
        verify(view,
               times(1)).setContainerNameHelpText(ERROR_MESSAGE);
    }

    @Test
    public void onContainerNameChangeInvalid() {
        when(view.getContainerName()).thenReturn(CONTAINER_NAME_VALUE);
        when(provisioningValidationService.isValidContainerName(CONTAINER_NAME_VALUE)).thenReturn(false);
        when(translationService.getTranslation(ContainerConfigPresenter_InvalidContainerNameErrorMessage)).thenReturn(ERROR_MESSAGE);

        presenter.onContainerNameChange();
        verify(view,
               times(1)).clearContainerNameHelpText();
        verify(view,
               times(1)).clearFormError();
        verify(view,
               times(1)).setContainerNameStatus(FormStatus.ERROR);
        verify(view,
               times(1)).setContainerNameHelpText(ERROR_MESSAGE);
    }

    @Test
    public void testOnGroupIdChangedValid() {
        when(view.getGroupId()).thenReturn(GROUP_ID_VALUE);
        presenter.onGroupIdChange();
        verify(view,
               times(1)).setGroupIdStatus(FormStatus.VALID);
        verify(view,
               times(1)).clearFormError();
    }

    @Test
    public void testOnGroupIdChangedInValid() {
        when(view.getGroupId()).thenReturn(EMPTY_STRING);
        presenter.onGroupIdChange();
        verify(view,
               times(1)).setGroupIdStatus(FormStatus.ERROR);
        verify(view,
               times(1)).clearFormError();
    }

    @Test
    public void testOnArtifactIdChangedValid() {
        when(view.getArtifactId()).thenReturn(ARTIFACT_ID_VALUE);
        presenter.onArtifactIdChange();
        verify(view,
               times(1)).setArtifactIdStatus(FormStatus.VALID);
        verify(view,
               times(1)).clearFormError();
    }

    @Test
    public void testOnArtifactIdChangedInValid() {
        when(view.getArtifactId()).thenReturn(EMPTY_STRING);
        presenter.onArtifactIdChange();
        verify(view,
               times(1)).setArtifactIdStatus(FormStatus.ERROR);
        verify(view,
               times(1)).clearFormError();
    }

    @Test
    public void testOnVersionChangedValid() {
        when(view.getVersion()).thenReturn(VERSION_VALUE);
        presenter.onVersionChange();
        verify(view,
               times(1)).setVersionStatus(FormStatus.VALID);
        verify(view,
               times(1)).clearFormError();
    }

    @Test
    public void testOnVersionChangedInValid() {
        when(view.getVersion()).thenReturn(EMPTY_STRING);
        presenter.onVersionChange();
        verify(view,
               times(1)).setVersionStatus(FormStatus.ERROR);
        verify(view,
               times(1)).clearFormError();
    }
}
