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

package org.kie.workbench.common.widgets.metadata.client.menu;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SaveAllMenuBuilderTest {

    private SaveAllMenuBuilder builder;

    @Mock
    private SaveAllMenuView view;

    @Before
    public void setup() {
        builder = new SaveAllMenuBuilder( view );
        builder.setup();
    }

    @Test
    public void testEnable() {
        final MenuItem mi = builder.build();
        mi.setEnabled( true );

        verify( view,
                times( 1 ) ).setEnabled( eq( true ) );
    }

    @Test
    public void testDisable() {
        final MenuItem mi = builder.build();
        mi.setEnabled( false );

        verify( view,
                times( 1 ) ).setEnabled( eq( false ) );
    }

    @Test
    public void testSaveAllCommand_Set() {
        final Command command = mock( Command.class );
        builder.setSaveAllCommand( command );

        builder.onSaveAll();

        verify( command,
                times( 1 ) ).execute();
    }

    @Test
    public void testSaveAllCommand_NotSet() {
        try {
            builder.onSaveAll();
        } catch ( NullPointerException npe ) {
            fail( "Null Commands should be handled." );
        }
    }

}
