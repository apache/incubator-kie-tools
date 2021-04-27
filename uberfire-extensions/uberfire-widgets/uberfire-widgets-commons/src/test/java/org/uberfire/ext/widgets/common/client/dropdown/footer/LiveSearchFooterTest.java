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

package org.uberfire.ext.widgets.common.client.dropdown.footer;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.dropdown.InlineCreationEditor;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiveSearchFooterTest {

    private static final String NEW_LABEL = "new";
    private static final String RESET_LABEL = "reset";

    @Mock
    private HTMLElement editorElement;

    @Mock
    private InlineCreationEditor editor;

    @Mock
    private LiveSearchFooterView view;

    @Mock
    private Command onNewEntry;

    @Mock
    private Command onReset;

    private LiveSearchFooter footer;

    @Before
    public void init() {
        when(editor.getElement()).thenReturn(editorElement);

        footer = new LiveSearchFooter(view);

        footer.init(onNewEntry, onReset);
    }

    @Test
    public void testFunctionallity() {

        verify(view).init(footer);

        footer.getElement();
        verify(view).getElement();

        footer.setNewEntryLabel(NEW_LABEL);
        verify(view).setNewEntryLabel(NEW_LABEL);

        footer.setResetLabel(RESET_LABEL);
        verify(view).setResetLabel(RESET_LABEL);

        footer.showReset(true);
        verify(view).showReset(true);

        footer.showAddNewEntry(true);
        verify(view).showAddNewEntry(true);

        footer.restore();
        verify(view).restore();

        footer.showEditor(editor);
        verify(view).show(editorElement);

        footer.onResetPressed();
        verify(onReset).execute();

        footer.onNewEntryPressed();
        verify(onNewEntry).execute();
    }
}
