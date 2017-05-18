/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import org.jboss.errai.common.client.dom.Span;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmPopupTest {

    @Mock
    Modal modal;

    @Mock
    Span modalTitle;

    @Mock
    Span modalConfirmationMessageLabel;

    @Mock
    Span okButton;

    @InjectMocks
    ConfirmPopup popup;

    @Test
    public void testHide() {
        popup.hide();

        verify(modal).hide();
    }

    @Test
    public void testCancel() {
        popup.onCancelClick(null);

        verify(modal).hide();
    }

    @Test
    public void testClose() {
        popup.onCloseClick(null);

        verify(modal).hide();
    }

    @Test
    public void testOk() {
        final Command command = mock(Command.class);
        popup.show(null,
                   null,
                   null,
                   command);

        popup.onOkClick(null);

        verify(command).execute();
        verify(modal).hide();
    }
}
