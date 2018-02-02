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

package org.kie.workbench.common.widgets.client.popups.copy;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.project.model.Package;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.handlers.PackageListBox;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@WithClassesToStub(Button.class)
@RunWith(GwtMockitoTestRunner.class)
public class CopyPopupWithPackageViewTest {

    private CopyPopupWithPackageView copyView;

    private ArgumentCaptor<Command> packageLoadedCommandCaptor;

    @Before
    public void setup() {
        final CopyPopUpPresenter presenter = mock(CopyPopUpPresenter.class);
        doReturn(PathFactory.newPath("my-file.txt",
                                     "my-project/src/main/resources/my-file.txt")).when(presenter).getPath();

        copyView = spy(new CopyPopupWithPackageView());
        copyView.presenter = presenter;
        copyView.packageListBox = mock(PackageListBox.class);
        copyView.translationService = mock(TranslationService.class);
        doReturn(mock(Package.class)).when(copyView.packageListBox).getSelectedPackage();
        doReturn(mock(Button.class)).when(copyView).button(anyString(),
                                                           any(Command.class),
                                                           any(ButtonType.class));

        packageLoadedCommandCaptor = ArgumentCaptor.forClass(Command.class);
        doNothing().when(copyView.packageListBox).setUp(eq(true),
                                                        packageLoadedCommandCaptor.capture());
    }

    @Test
    public void copyProjectResourceWithActiveProject() {
        givenThatThereIsAnActiveProject();
        givenThatAProjectResourceIsBeingCopied();

        copyView.packageListBoxSetup();

        verify(copyView.copyButton()).setEnabled(false);
        verify(copyView.packageListBox).setUp(eq(true),
                                              any(Command.class));

        packageLoadedCommandCaptor.getValue().execute();

        verify(copyView.copyButton()).setEnabled(true);

        copyView.getTargetPath();

        verify(copyView.packageListBox.getSelectedPackage()).getPackageMainResourcesPath();
    }

    @Test
    public void copyProjectResourceWithoutActiveProject() {
        givenThatThereIsNotAnActiveProject();
        givenThatAProjectResourceIsBeingCopied();

        copyView.packageListBoxSetup();

        verify(copyView.copyButton(),
               never()).setEnabled(false);
        verify(copyView.packageListBox,
               never()).setUp(eq(true),
                              any(Command.class));

        copyView.getTargetPath();

        verify(copyView.packageListBox.getSelectedPackage(),
               never()).getPackageMainResourcesPath();
    }

    @Test
    public void copyNotAProjectResourceWithActiveProject() {
        givenThatThereIsAnActiveProject();
        givenThatNotAProjectResourceIsBeingCopied();

        copyView.packageListBoxSetup();

        verify(copyView.copyButton(),
               never()).setEnabled(false);
        verify(copyView.packageListBox,
               never()).setUp(eq(true),
                              any(Command.class));

        copyView.getTargetPath();

        verify(copyView.packageListBox.getSelectedPackage()).getPackageMainResourcesPath();
    }

    @Test
    public void copyNotAProjectResourceWithoutActiveProject() {
        givenThatThereIsNotAnActiveProject();
        givenThatNotAProjectResourceIsBeingCopied();

        copyView.packageListBoxSetup();

        verify(copyView.copyButton(),
               never()).setEnabled(false);
        verify(copyView.packageListBox,
               never()).setUp(eq(true),
                              any(Command.class));

        copyView.getTargetPath();

        verify(copyView.packageListBox.getSelectedPackage(),
               never()).getPackageMainResourcesPath();
    }

    private void givenThatThereIsNotAnActiveProject() {
        changeActiveProjectStatus(false);
    }

    private void givenThatThereIsAnActiveProject() {
        changeActiveProjectStatus(true);
    }

    private void changeActiveProjectStatus(boolean thereIsAnActiveProject) {
        doReturn(thereIsAnActiveProject).when(copyView).thereIsAnActiveProject();
    }

    private void givenThatNotAProjectResourceIsBeingCopied() {
        changeProjectResourceStatus(false);
    }

    private void givenThatAProjectResourceIsBeingCopied() {
        changeProjectResourceStatus(true);
    }

    private void changeProjectResourceStatus(boolean isAProjectResource) {
        doReturn(isAProjectResource).when(copyView).isAProjectResource(anyString());
    }
}
