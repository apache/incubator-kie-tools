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

package org.uberfire.ext.widgets.common.client.menu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.menu.RefreshSelectorMenuBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class RefreshSelectorMenuBuilderTest {

    @Mock
    RefreshSelectorMenuBuilder.SupportsRefreshInterval supportsRefreshInterval;

    RefreshSelectorMenuBuilder refreshSelectorMenuBuilder;

    @GwtMock
    AnchorListItem oneMinuteRadioButton;

    ClickHandler clickHandler;

    @Before
    public void setup() {
        when(oneMinuteRadioButton.addClickHandler(any(ClickHandler.class))).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock aInvocation) throws Throwable {
                clickHandler = (ClickHandler) aInvocation.getArguments()[0];
                return null;
            }
        });
        refreshSelectorMenuBuilder = new RefreshSelectorMenuBuilder(supportsRefreshInterval);
    }

    @Test
    public void testUpdateRefreshInterval() {
        int time = 1;
        refreshSelectorMenuBuilder.createTimeSelector(time, "", time, mock(AnchorListItem.class));
        clickHandler.onClick(new ClickEvent() {
        });

        verify(supportsRefreshInterval).onUpdateRefreshInterval(true, time);
        verify(oneMinuteRadioButton, times(2)).setIcon(IconType.CHECK);
    }

    @Test
    public void testSelectedRefreshInterval() {
        int time = 1;
        int selectedTime = 2;
        refreshSelectorMenuBuilder.createTimeSelector(time, "", selectedTime, mock(AnchorListItem.class));

        verify(oneMinuteRadioButton, never()).setIcon(any(IconType.class));
    }

}