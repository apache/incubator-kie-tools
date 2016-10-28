/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.editor.common;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DefEditorActionsPanelTest {

    @Mock
    private DefEditorActionsPanelView view;

    private DefEditorActionsPanel actionsPanel;

    @Mock
    private Command saveCommand;

    @Mock
    private Command deleteCommand;

    @Mock
    private Command cancelCommand;

    @Before
    public void setup( ) {
        actionsPanel = new DefEditorActionsPanel( view );
        actionsPanel.setSaveCommand( saveCommand );
        actionsPanel.setCancelCommand( cancelCommand );
        actionsPanel.setDeleteCommand( deleteCommand );
    }

    @Test
    public void testActions( ) {
        actionsPanel.onSave( );
        verify( saveCommand, times( 1 ) ).execute( );
        actionsPanel.onCancel( );
        verify( cancelCommand, times( 1 ) ).execute( );
        actionsPanel.onDelete();
        verify( deleteCommand, times( 1 ) ).execute( );
    }
}