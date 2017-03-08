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

package org.drools.workbench.screens.globals.client.editor;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddGlobalPopupTest {

    @Mock
    private AddGlobalPopupView view;

    @InjectMocks
    private AddGlobalPopup addGlobalPopup;

    @Test
    public void init() {
        addGlobalPopup.init();

        verify( view,
                times( 1 ) ).init( addGlobalPopup );
    }

    @Test
    public void getAlias() {
        when( view.getInsertedAlias() ).thenReturn( "alias" );

        String aliasResult = addGlobalPopup.getAlias();

        assertEquals( "alias",
                      aliasResult );
    }

    @Test
    public void getClassName() {
        when( view.getSelectedClassName() ).thenReturn( "className" );

        String classNameResult = addGlobalPopup.getClassName();

        assertEquals( "className",
                      classNameResult );
    }

    @Test
    public void show() {
        List<String> classNames = Arrays.asList( "fqn1" );

        addGlobalPopup.show( () -> {},
                             () -> {},
                             classNames );

        verify( view,
                times( 1 ) ).clear();
        verify( view,
                times( 1 ) ).setClassNames( classNames );
        verify( view,
                times( 1 ) ).show();
    }

    @Test
    public void validateAliasWithFailures() {
        when( view.getInsertedAlias() ).thenReturn( "" );

        addGlobalPopup.onAliasInputChanged();

        verify( view,
                times( 1 ) ).showAliasValidationError();
    }

    @Test
    public void validateAliasWithNoFailures() {
        when( view.getInsertedAlias() ).thenReturn( "alias" );

        addGlobalPopup.onAliasInputChanged();

        verify( view,
                times( 1 ) ).hideAliasValidationError();
    }

    @Test
    public void validateClassNameWithFailures() {
        when( view.getSelectedClassName() ).thenReturn( "" );

        addGlobalPopup.onClassNameSelectChanged();

        verify( view,
                times( 1 ) ).showClassNameValidationError();
    }

    @Test
    public void validateClassNameWithNoFailures() {
        when( view.getSelectedClassName() ).thenReturn( "className" );

        addGlobalPopup.onClassNameSelectChanged();

        verify( view,
                times( 1 ) ).hideClassNameValidationError();
    }
}
