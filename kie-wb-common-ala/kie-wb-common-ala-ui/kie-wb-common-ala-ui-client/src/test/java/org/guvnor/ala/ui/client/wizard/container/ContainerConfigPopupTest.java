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

import java.util.List;

import org.guvnor.ala.ui.client.widget.popup.BaseOkCancelPopupView;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerConfigPopupTest {

    public static String TITLE = "TITLE";

    @Mock
    private ContainerConfigPresenter configPresenter;

    @Mock
    private ContainerConfigPresenter.View configPresenterView;

    @Mock
    private HTMLElement configPresenterViewElement;

    @Mock
    private BaseOkCancelPopupView basePopupView;

    private ContainerConfigPopup popup;

    private ParameterizedCommand okCommand = mock(ParameterizedCommand.class);

    private Command cancelCommand = mock(Command.class);

    private List<String> alreadyInUseNames = mock(List.class);

    @Before
    public void setUp() {
        when(configPresenter.getView()).thenReturn(configPresenterView);
        when(configPresenterView.getElement()).thenReturn(configPresenterViewElement);

        popup = new ContainerConfigPopup(basePopupView,
                                         configPresenter);
        popup.init();
        verify(basePopupView,
               times(1)).init(popup);
        verify(basePopupView,
               times(1)).setContent(configPresenterViewElement);
    }

    @Test
    public void testShow() {
        popup.show(TITLE,
                   okCommand,
                   cancelCommand,
                   alreadyInUseNames);
        verify(configPresenter,
               times(1)).clear();
        verify(configPresenter,
               times(1)).setup(alreadyInUseNames);
        verify(basePopupView,
               times(1)).show(TITLE);
    }

    @Test
    public void testOnOKWhenValidConfig() {
        popup.show(TITLE,
                   okCommand,
                   cancelCommand,
                   alreadyInUseNames);
        ContainerConfig containerConfig = mock(ContainerConfig.class);
        when(configPresenter.getContainerConfig()).thenReturn(containerConfig);
        when(configPresenter.validateForSubmit()).thenReturn(true);

        popup.onOK();
        verify(configPresenter,
               times(1)).validateForSubmit();
        verify(basePopupView,
               times(1)).hide();
        verify(okCommand,
               times(1)).execute(containerConfig);
    }

    @Test
    public void testOnOKWhenInvalidConfig() {
        popup.show(TITLE,
                   okCommand,
                   cancelCommand,
                   alreadyInUseNames);
        when(configPresenter.validateForSubmit()).thenReturn(false);

        popup.onOK();
        verify(configPresenter,
               times(1)).validateForSubmit();
        verify(basePopupView,
               never()).hide();
        verify(okCommand,
               never()).execute(any());
    }

    @Test
    public void testOnCancel() {
        popup.show(TITLE,
                   okCommand,
                   cancelCommand,
                   alreadyInUseNames);

        popup.onCancel();
        verify(configPresenter,
               never()).validateForSubmit();
        verify(basePopupView,
               times(1)).hide();
        verify(cancelCommand,
               times(1)).execute();
    }
}
