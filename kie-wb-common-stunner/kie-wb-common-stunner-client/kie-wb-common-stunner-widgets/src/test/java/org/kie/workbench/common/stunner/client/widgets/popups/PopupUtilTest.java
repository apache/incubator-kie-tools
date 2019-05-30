/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.popups;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PopupUtilTest {

    private static final String TITLE = "TITLE";

    private static final String INLINE_NOTIFICATION = "INLINE_NOTIFICATION";

    private static final String OK_BUTTON_TEXT = "OK_BUTTON_TEXT";

    private static final String CONFIRM_MESSAGE = "CONFIRM_MESSAGE";

    private PopupUtil popupUtil;

    @Mock
    private ConfirmPopup confirmPopup;

    @GwtMock
    private YesNoCancelPopup yesNoCancelPopup;

    @Mock
    private Command command;

    @Mock
    private Command noCommand;

    @Before
    public void setUp() {
        popupUtil = spy(new PopupUtil(confirmPopup) {
            @Override
            YesNoCancelPopup buildYesNoCancelPopup(String title, String message, Command yesCommand, Command noCommand, Command cancelCommand) {
                return yesNoCancelPopup;
            }
        });
    }

    @Test
    public void testShowConfirmPopup() {
        popupUtil.showConfirmPopup(TITLE,
                                   OK_BUTTON_TEXT,
                                   CONFIRM_MESSAGE,
                                   command);

        verify(confirmPopup,
               times(1)).show(TITLE,
                              OK_BUTTON_TEXT,
                              CONFIRM_MESSAGE,
                              command);
    }

    @Test
    public void testShowConfirmPopupWithInlineNotification() {
        InlineNotification.InlineNotificationType someNotificationType = InlineNotification.InlineNotificationType.WARNING;
        Button.ButtonStyleType someBtnType = Button.ButtonStyleType.PRIMARY;
        popupUtil.showConfirmPopup(TITLE,
                                   INLINE_NOTIFICATION,
                                   someNotificationType,
                                   OK_BUTTON_TEXT,
                                   someBtnType,
                                   CONFIRM_MESSAGE,
                                   command);

        verify(confirmPopup,
               times(1)).show(TITLE,
                              INLINE_NOTIFICATION,
                              someNotificationType,
                              OK_BUTTON_TEXT,
                              someBtnType,
                              CONFIRM_MESSAGE,
                              command);
    }

    @Test
    public void testShowYesNoCancelPopup() {
        popupUtil.showYesNoCancelPopup(TITLE,
                                       CONFIRM_MESSAGE,
                                       command,
                                       noCommand);
        verify(popupUtil).buildYesNoCancelPopup(eq(TITLE), eq(CONFIRM_MESSAGE), eq(command), eq(noCommand), any(Command.class));
        verify(yesNoCancelPopup).clearScrollHeight();
        verify(yesNoCancelPopup).setClosable(false);
        verify(yesNoCancelPopup).show();
    }
}
