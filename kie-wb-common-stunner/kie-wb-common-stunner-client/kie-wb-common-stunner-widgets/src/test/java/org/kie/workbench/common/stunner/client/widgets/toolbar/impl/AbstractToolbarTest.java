/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.AbstractToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractToolbarTest {

    @Mock
    private ToolbarView toolbarView;

    @Mock
    private AbstractToolbarCommand toolbarCommand;

    @Mock
    private AbstractToolbarItem toolbarItem;

    @Mock
    private Widget toolbarItemWidget;

    @Mock
    private ClientSession session;

    private AbstractToolbar toolbar;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.toolbar = new AbstractToolbar(toolbarView) {
            @Override
            protected AbstractToolbarItem newToolbarItem() {
                return toolbarItem;
            }
        };
        when(toolbarItem.asWidget()).thenReturn(toolbarItemWidget);

        toolbar.addCommand(AbstractToolbar.class, toolbarCommand);
        toolbar.initialize(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkInitialise() {
        verify(toolbarView).addItem(eq(toolbarItemWidget));
        verify(toolbarItem).show(eq(toolbar),
                                 eq(session),
                                 eq(toolbarCommand),
                                 any(Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkEnable() {
        toolbar.enable(toolbarCommand);

        verify(toolbarItem).enable();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkDisable() {
        toolbar.disable(toolbarCommand);

        verify(toolbarItem).disable();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGetItemWhenNotFound() {
        toolbar.disable(mock(AbstractToolbarCommand.class));

        verify(toolbarItem,
               never()).disable();
    }
}
