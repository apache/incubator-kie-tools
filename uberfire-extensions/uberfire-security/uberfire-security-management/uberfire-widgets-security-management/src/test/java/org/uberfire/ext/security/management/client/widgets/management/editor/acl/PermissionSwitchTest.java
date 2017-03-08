/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionSwitch;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermissionSwitchTest {

    @Mock
    PermissionSwitch.View view;

    @Mock
    Command onChange;

    PermissionSwitch presenter;

    @Before
    public void setup() {
        presenter = new PermissionSwitch(view);
    }

    @Test
    public void testInit() {
        presenter.init("On",
                       "Off",
                       true,
                       0);
        when(view.isOn()).thenReturn(true);

        verify(view).init("On",
                          "Off",
                          true);
        assertTrue(presenter.isOn());
    }

    @Test
    public void testChange() {
        presenter.init("On",
                       "Off",
                       true,
                       0);
        presenter.setOnChange(onChange);
        when(view.isOn()).thenReturn(false);

        presenter.onChange();
        assertFalse(presenter.isOn());
        verify(onChange).execute();
    }
}
