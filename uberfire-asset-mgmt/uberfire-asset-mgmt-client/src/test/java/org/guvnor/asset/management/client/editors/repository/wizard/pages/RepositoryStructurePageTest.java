/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.client.editors.repository.wizard.pages;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.asset.management.client.editors.repository.wizard.CreateRepositoryWizardModel;
import org.guvnor.asset.management.client.editors.repository.wizard.WizardTestUtils;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mocks.CallerMock;

import static org.guvnor.asset.management.client.editors.repository.wizard.WizardTestUtils.WizardPageStatusChangeEventMock;
import static org.guvnor.asset.management.client.editors.repository.wizard.WizardTestUtils.assertPageComplete;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RepositoryStructurePageTest {

    private static final String VALID_VALUE = "Valid";

    private static final String INVALID_VALUE = "Invalid";

    @GwtMock
    RepositoryStructurePageView view;

    RepositoryStructureService repositoryStructureService = mock(RepositoryStructureService.class);

    @Test
    public void testPageLoad() {
        RepositoryStructurePage structurePage = new RepositoryStructurePage(view,
                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService));
    }

    /**
     * Tests that the page reacts properly when a valid project name is typed.
     */
    @Test
    public void testValidProjectNameChange() {
        testProjectNameChange(true);
    }

    /**
     * Tests that the page reacts properly when an invalid project name is typed.
     */
    @Test
    public void testInvalidProjectNameChange() {
        testProjectNameChange(false);
    }

    private void testProjectNameChange(boolean testValidChange) {

        RepositoryStructurePageExtended structurePage = new RepositoryStructurePageExtended(view,
                                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService),
                                                                                            new WizardTestUtils.WizardPageStatusChangeEventMock());

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        structurePage.setModel(model);

        if (testValidChange) {
            when(view.getProjectName()).thenReturn(VALID_VALUE);
        } else {
            when(view.getProjectName()).thenReturn(INVALID_VALUE);
        }

        when(repositoryStructureService.isValidProjectName(VALID_VALUE)).thenReturn(true);
        when(repositoryStructureService.isValidProjectName(INVALID_VALUE)).thenReturn(false);

        structurePage.onProjectNameChange();

        verify(view,
               times(2)).getProjectName();

        if (testValidChange) {
            verify(view,
                   times(1)).clearProjectNameErrorMessage();
            assertEquals(VALID_VALUE,
                         model.getProjectName());
        } else {
            verify(view,
                   times(1)).setProjectNameErrorMessage(anyString());
            assertEquals(INVALID_VALUE,
                         model.getProjectName());
        }

        assertPageComplete(false,
                           structurePage);
    }

    /**
     * Tests that the page reacts properly when a valid groupId is typed.
     */
    @Test
    public void testValidGroupIdChange() {
        testGroupIdChange(true);
    }

    /**
     * Tests that the page reacts properly when an invalid groupId is typed.
     */
    @Test
    public void testInvalidGroupIdChange() {
        testGroupIdChange(false);
    }

    private void testGroupIdChange(boolean testValidChange) {

        RepositoryStructurePageExtended structurePage = new RepositoryStructurePageExtended(view,
                                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService),
                                                                                            new WizardTestUtils.WizardPageStatusChangeEventMock());

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        structurePage.setModel(model);

        if (testValidChange) {
            when(view.getGroupId()).thenReturn(VALID_VALUE);
        } else {
            when(view.getGroupId()).thenReturn(INVALID_VALUE);
        }

        when(repositoryStructureService.isValidGroupId(INVALID_VALUE)).thenReturn(false);
        when(repositoryStructureService.isValidGroupId(VALID_VALUE)).thenReturn(true);

        structurePage.onGroupIdChange();

        verify(view,
               times(2)).getGroupId();

        if (testValidChange) {
            verify(view,
                   times(1)).clearGroupIdErrorMessage();
            assertEquals(VALID_VALUE,
                         model.getGroupId());
        } else {
            verify(view,
                   times(1)).setGroupIdErrorMessage(anyString());
            assertEquals(INVALID_VALUE,
                         model.getGroupId());
        }

        assertPageComplete(false,
                           structurePage);
    }

    /**
     * Tests that the page reacts properly when a valid artifactId is typed.
     */
    @Test
    public void testValidArtifactIdChange() {
        testArtifactIdChange(true);
    }

    /**
     * Tests that the page reacts properly when an invalid artifactId is typed.
     */
    @Test
    public void testInvalidArtifactIdChange() {
        testArtifactIdChange(false);
    }

    private void testArtifactIdChange(boolean testValidChange) {

        RepositoryStructurePageExtended structurePage = new RepositoryStructurePageExtended(view,
                                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService),
                                                                                            new WizardTestUtils.WizardPageStatusChangeEventMock());

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        structurePage.setModel(model);

        if (testValidChange) {
            when(view.getArtifactId()).thenReturn(VALID_VALUE);
        } else {
            when(view.getArtifactId()).thenReturn(INVALID_VALUE);
        }

        when(repositoryStructureService.isValidArtifactId(VALID_VALUE)).thenReturn(true);
        when(repositoryStructureService.isValidArtifactId(INVALID_VALUE)).thenReturn(false);

        structurePage.onArtifactIdChange();

        verify(view,
               times(2)).getArtifactId();

        if (testValidChange) {
            verify(view,
                   times(1)).clearArtifactIdErrorMessage();
            assertEquals(VALID_VALUE,
                         model.getArtifactId());
        } else {
            verify(view,
                   times(1)).setArtifactIdErrorMessage(anyString());
            assertEquals(INVALID_VALUE,
                         model.getArtifactId());
        }

        assertPageComplete(false,
                           structurePage);
    }

    /**
     * Tests that the page reacts properly when a valid version typed.
     */
    @Test
    public void testValidVersionChange() {
        testVersionChange(true);
    }

    /**
     * Tests that the page reacts properly when an invalid version is typed.
     */
    @Test
    public void testInvalidVersionChange() {
        testVersionChange(false);
    }

    private void testVersionChange(boolean testValidChange) {

        RepositoryStructurePageExtended structurePage = new RepositoryStructurePageExtended(view,
                                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService),
                                                                                            new WizardTestUtils.WizardPageStatusChangeEventMock());

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        structurePage.setModel(model);

        if (testValidChange) {
            when(view.getVersion()).thenReturn(VALID_VALUE);
        } else {
            when(view.getVersion()).thenReturn(INVALID_VALUE);
        }

        when(repositoryStructureService.isValidVersion(VALID_VALUE)).thenReturn(true);
        when(repositoryStructureService.isValidVersion(INVALID_VALUE)).thenReturn(false);

        structurePage.onVersionChange();

        verify(view,
               times(2)).getVersion();

        if (testValidChange) {
            verify(view,
                   times(1)).clearVersionErrorMessage();
            assertEquals(VALID_VALUE,
                         model.getVersion());
        } else {
            verify(view,
                   times(1)).setVersionErrorMessage(anyString());
            assertEquals(INVALID_VALUE,
                         model.getVersion());
        }

        assertPageComplete(false,
                           structurePage);
    }

    /**
     * Test a sequence of steps that will successfully complete the page.
     */
    @Test
    public void testPageCompleted() {

        String projectName = "ProjectName";
        String groupId = "GroupId";
        String artifactId = "ArtifactId";
        String version = "Version";

        RepositoryStructurePageExtended structurePage = new RepositoryStructurePageExtended(view,
                                                                                            new CallerMock<RepositoryStructureService>(repositoryStructureService),
                                                                                            new WizardTestUtils.WizardPageStatusChangeEventMock());

        CreateRepositoryWizardModel model = new CreateRepositoryWizardModel();
        structurePage.setModel(model);

        when(view.getProjectName()).thenReturn(projectName);
        when(view.getGroupId()).thenReturn(groupId);
        when(view.getArtifactId()).thenReturn(artifactId);
        when(view.getVersion()).thenReturn(version);

        when(repositoryStructureService.isValidProjectName(projectName)).thenReturn(true);
        when(repositoryStructureService.isValidGroupId(groupId)).thenReturn(true);
        when(repositoryStructureService.isValidArtifactId(artifactId)).thenReturn(true);
        when(repositoryStructureService.isValidVersion(version)).thenReturn(true);

        structurePage.prepareView();
        //emulate that the page was visited at least one time as required.
        structurePage.setStructurePageWasVisited(true);

        //this sequence is not relevant
        structurePage.onProjectNameChange();
        structurePage.onGroupIdChange();
        structurePage.onArtifactIdChange();
        structurePage.onVersionChange();

        assertPageComplete(true,
                           structurePage);

        assertEquals(projectName,
                     model.getProjectName());
        assertEquals(groupId,
                     model.getGroupId());
        assertEquals(artifactId,
                     model.getArtifactId());
        assertEquals(version,
                     model.getVersion());
    }

    public static class RepositoryStructurePageExtended extends RepositoryStructurePage {

        public RepositoryStructurePageExtended(RepositoryStructurePageView view,
                                               Caller<RepositoryStructureService> repositoryStructureService,
                                               WizardPageStatusChangeEventMock event) {
            super(view,
                  repositoryStructureService);
            super.wizardPageStatusChangeEvent = event;
        }
    }
}