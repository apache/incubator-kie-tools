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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PopupUtilTest {

    private static final String TITLE = "TITLE";

    private static final String INLINE_NOTIFICATION = "INLINE_NOTIFICATION";

    private static final String OK_BUTTON_TEXT = "OK_BUTTON_TEXT";

    private static final String CONFIRM_MESSAGE = "CONFIRM_MESSAGE";

    private PopupUtil popupUtil;

    @Mock
    private ConfirmPopup confirmPopup;

    @Mock
    private Command command;

    @Before
    public void setUp() {
        popupUtil = new PopupUtil(confirmPopup);
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
}
