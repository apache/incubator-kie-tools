/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class HelpIconTest {

    @Mock
    private Icon icon;

    @Mock
    private SimplePanel panel;

    @Mock
    private Popover popover;

    private HelpIcon helpIcon;

    @Before
    public void setUp() {
        this.helpIcon = new HelpIcon(icon,
                                     panel,
                                     popover);
    }

    @Test
    public void setHelpContent() {
        helpIcon.setHelpContent("testContent");

        verify(popover).setContent("testContent");
    }

    @Test
    public void setHelpTitle() {
        helpIcon.setHelpTitle("testTitle");

        verify(popover).setTitle("testTitle");
    }
}
