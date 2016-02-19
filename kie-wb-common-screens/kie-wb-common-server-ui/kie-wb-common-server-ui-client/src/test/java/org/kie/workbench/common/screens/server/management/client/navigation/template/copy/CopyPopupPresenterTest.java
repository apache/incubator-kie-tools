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

package org.kie.workbench.common.screens.server.management.client.navigation.template.copy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CopyPopupPresenterTest {

    @Mock
    CopyPopupPresenter.View view;

    @InjectMocks
    CopyPopupPresenter presenter;

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testHide() {
        presenter.hide();

        verify(view).hide();
    }

    @Test
    public void testCopy() {
        final String newTemplateName = "NewTemplateName";
        when(view.getNewTemplateName()).thenReturn(newTemplateName);
        final ParameterizedCommand command = mock(ParameterizedCommand.class);
        presenter.copy(command);

        verify(view).clear();
        verify(view).display();

        presenter.save();

        verify(command).execute(newTemplateName);
    }

    @Test
    public void testCopyError() {
        when(view.getNewTemplateName()).thenReturn("");

        presenter.save();

        verify(view).errorOnTemplateNameFromGroup();
    }

    @Test
    public void testErrorOnTemplateNameFromGroup() {
        final String errorMessage = "errorMessage";

        presenter.errorDuringProcessing(errorMessage);

        verify(view).errorOnTemplateNameFromGroup(errorMessage);
    }

}
