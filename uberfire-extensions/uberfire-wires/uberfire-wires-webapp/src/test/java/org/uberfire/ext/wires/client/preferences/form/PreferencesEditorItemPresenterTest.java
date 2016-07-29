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

package org.uberfire.ext.wires.client.preferences.form;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.shared.PreferenceScope;
import org.uberfire.ext.preferences.shared.impl.PreferenceScopedValue;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PreferencesEditorItemPresenterTest {

    private PreferencesEditorItemPresenter.View view;

    private PreferencesEditorItemPresenter presenter;

    @Before
    public void setup() {
        view = mock( PreferencesEditorItemPresenter.View.class );
        presenter = new PreferencesEditorItemPresenter( view );
        presenter.setViewMode( ViewMode.GLOBAL );
        presenter.setPersistedPreferenceValue( "persistedValue" );
        presenter.setPersistedPreferenceScope( mock( PreferenceScope.class ) );
    }

    @Test
    public void preferenceShouldNotBePersistedWhenTheValueIsNotModifiedTest() {
        doReturn( "persistedValue" ).when( view ).getNewPreferenceValue();

        assertFalse( presenter.shouldBePersisted() );
    }

    @Test
    public void preferenceShouldBePersistedWhenTheValueIsModifiedTest() {
        doReturn( "newValue" ).when( view ).getNewPreferenceValue();

        assertTrue( presenter.shouldBePersisted() );
    }
}
