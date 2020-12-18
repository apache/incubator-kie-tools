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

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ErrorPopupTest {

    private static final String MESSAGE = "MESSAGE";

    private static final String DETAIL = "DETAIL";

    private static final String SHOW_DETAIL_LABEL = "SHOW_DETAIL_LABEL";

    private static final String CLOSE_DETAIL_LABEL = "CLOSE_DETAIL_LABEL";

    @Mock
    private HTMLElement inlineNotification;

    @Mock
    private HTMLElement standardNotification;

    @Mock
    private ErrorPopup.View view;

    private ErrorPopup popup;

    @Before
    public void setUp() {
        when(view.getInlineNotification()).thenReturn(inlineNotification);
        when(view.getStandardNotification()).thenReturn(standardNotification);
        when(view.getShowDetailLabel()).thenReturn(SHOW_DETAIL_LABEL);
        when(view.getCloseDetailLabel()).thenReturn(CLOSE_DETAIL_LABEL);

        popup = spy(new ErrorPopup(view));
        popup.init();
        verify(view,
               times(1)).init(popup);
    }

    @Test
    public void testShowError() {
        popup.showError(MESSAGE);
        verify(popup,
               times(1)).showError(MESSAGE,
                                   ErrorPopup.DisplayMode.PATTERN_FLY);
    }

    @Test
    public void testShowErrorStandard() {
        popup.showError(MESSAGE,
                        ErrorPopup.DisplayMode.STANDARD);
        verifyStandardNotificationWasSet(MESSAGE);
        verify(view,
               times(1)).setDetailValue("");
        verify(view,
               times(1)).showDetailPanel(false);
        verify(view,
               times(1)).show();
    }

    @Test
    public void testShowErrorPatternFly() {
        popup.showError(MESSAGE,
                        ErrorPopup.DisplayMode.PATTERN_FLY);
        verifyInlineNotificationWasSet(MESSAGE);
        verify(view,
               times(1)).setDetailValue("");
        verify(view,
               times(1)).showDetailPanel(false);
        verify(view,
               times(1)).show();
    }

    @Test
    public void testShowErrorWithDetail() {
        popup.showError(MESSAGE,
                        DETAIL);
        verify(popup,
               times(1)).showError(MESSAGE,
                                   DETAIL,
                                   ErrorPopup.DisplayMode.PATTERN_FLY);
    }

    @Test
    public void testShowErrorStandardWithDetail() {
        popup.showError(MESSAGE,
                        DETAIL,
                        ErrorPopup.DisplayMode.STANDARD);
        verifyStandardNotificationWasSet(MESSAGE);
        verify(view,
               times(1)).setDetailValue(DETAIL);
        verify(view,
               times(1)).showDetailPanel(true);
        verify(view,
               times(1)).show();
    }

    @Test
    public void testShowErrorPatternFlyWithDetail() {
        popup.showError(MESSAGE,
                        DETAIL,
                        ErrorPopup.DisplayMode.PATTERN_FLY);
        verifyInlineNotificationWasSet(MESSAGE);
        verify(view,
               times(1)).setDetailValue(DETAIL);
        verify(view,
               times(1)).showDetailPanel(true);
        verify(view,
               times(1)).show();
    }

    private void verifyStandardNotificationWasSet(String message) {
        verify(view,
               times(1)).setNotification(standardNotification);
        verify(view,
               times(1)).setStandardNotificationValue(message);
        verify(view,
               never()).setNotification(inlineNotification);
        verify(view,
               never()).setInlineNotificationValue(any());
    }

    private void verifyInlineNotificationWasSet(String message) {
        verify(view,
               times(1)).setNotification(inlineNotification);
        verify(view,
               times(1)).setInlineNotificationValue(message);
        verify(view,
               never()).setNotification(standardNotification);
        verify(view,
               never()).setStandardNotificationValue(any());
    }

    @Test
    public void testOnOK() {
        popup.onOk();
        verify(view,
               times(1)).hide();
    }

    @Test
    public void testOnClose() {
        popup.onClose();
        verify(view,
               times(1)).hide();
    }

    @Test
    public void testOnDetailWhenDetailCollapsed() {
        when(view.isDetailCollapsed()).thenReturn(true);
        popup.onDetail();
        verify(view,
               times(1)).setCollapseDetailIcon(false);
        verify(view,
               times(1)).setDetailLabel(CLOSE_DETAIL_LABEL);
    }

    @Test
    public void testOnDetailWhenDetailNotCollapsed() {
        when(view.isDetailCollapsed()).thenReturn(false);
        popup.onDetail();
        verify(view,
               times(1)).setCollapseDetailIcon(true);
        verify(view,
               times(1)).setDetailLabel(SHOW_DETAIL_LABEL);
    }
}