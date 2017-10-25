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

package org.guvnor.ala.ui.client.util;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class PopupHelperTest {

    private static final String TITLE = "TITLE";

    private static final String MESSAGE = "MESSAGE";

    private static final String DETAIL = "DETAIL";

    private PopupHelper popupHelper;

    @Mock
    private YesNoCancelPopup yesNoCancelPopup;

    @Mock
    private ErrorPopup errorPopup;

    @Before
    public void setup() {
        popupHelper = spy(new PopupHelper(errorPopup) {
            @Override
            protected YesNoCancelPopup newYesNoPopup(String title,
                                                     String message,
                                                     Command yesCommand,
                                                     Command noCommand) {
                return yesNoCancelPopup;
            }

            @Override
            protected YesNoCancelPopup newNotificationPopup(String title,
                                                            String message) {
                return yesNoCancelPopup;
            }
        });
    }

    @Test
    public void testShowInformationPopup() {
        popupHelper.showInformationPopup(MESSAGE);
        verify(popupHelper,
               times(1)).newNotificationPopup(CommonConstants.INSTANCE.Information(),
                                              MESSAGE);
        verifyPopupWasShown(yesNoCancelPopup);
    }

    @Test
    public void testShowErrorPopup() {
        popupHelper.showErrorPopup(MESSAGE);
        errorPopup.showError(MESSAGE);
    }

    @Test
    public void testShowErrorPopupWithDetail() {
        popupHelper.showErrorPopup(MESSAGE,
                                   DETAIL);
        errorPopup.showError(MESSAGE,
                             DETAIL);
    }

    @Test
    public void testShowYesNoPopup() {
        Command yesCommand = mock(Command.class);
        Command noCommand = mock(Command.class);
        popupHelper.showYesNoPopup(TITLE,
                                   MESSAGE,
                                   yesCommand,
                                   noCommand);
        verify(popupHelper,
               times(1)).newYesNoPopup(TITLE,
                                       MESSAGE,
                                       yesCommand,
                                       noCommand);
        verifyPopupWasShown(yesNoCancelPopup);
    }

    @Test
    public void testGetPopupErrorCallback() {
        Message message = mock(Message.class);
        popupHelper.getPopupErrorCallback().error(message,
                                                  new Exception(MESSAGE));
        verify(popupHelper,
               times(1)).showErrorPopup(MESSAGE);
    }

    private void verifyPopupWasShown(YesNoCancelPopup popup) {
        verify(popup,
               times(1)).setClosable(false);
        verify(popup,
               times(1)).clearScrollHeight();
        verify(popup,
               times(1)).show();
    }
}