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

package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreference;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.table.client.DataGrid;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Image.class, Label.class, Text.class})
public class SimpleTableTest {

    @GwtMock
    DataGrid dataGridMock;

    protected CallerMock<UserPreferencesService> userPreferencesService;

    @Mock
    protected UserPreferencesService userPreferencesServiceMock;

    private SimpleTable simpleTable;
    private GridPreferencesStore gridPreferencesStore;

    @Before
    public void setupMocks() {
        simpleTable = new SimpleTable();
        gridPreferencesStore = new GridPreferencesStore(new GridGlobalPreferences("key", null, null));
        simpleTable.setGridPreferencesStore(gridPreferencesStore);

        userPreferencesService = new CallerMock<>(userPreferencesServiceMock);
        simpleTable.setPreferencesService(userPreferencesService);
    }

    @Test
    public void testRedrawFlush() throws Exception {
        this.simpleTable = new SimpleTable();

        simpleTable.dataGrid = dataGridMock;
        simpleTable.redraw();
        verify(dataGridMock).redraw();
        verify(dataGridMock).flush();
    }

    @Test
    public void testSavePreferencesAfterColumnChangeByDefault() {
        simpleTable.afterColumnChangedHandler();

        assertTrue(simpleTable.isPersistingPreferencesOnChange());
        verify(userPreferencesServiceMock).saveUserPreferences(any(UserPreference.class));
    }

    @Test
    public void testSavePreferencesAfterColumnChangeConf() {
        simpleTable.setPersistPreferencesOnChange(true);
        simpleTable.afterColumnChangedHandler();

        verify(userPreferencesServiceMock).saveUserPreferences(any(UserPreference.class));

        simpleTable.setPersistPreferencesOnChange(false);
        simpleTable.afterColumnChangedHandler();

        verifyNoMoreInteractions(userPreferencesServiceMock);
    }
    @Test
    public void testDefaultSavePreferencesUsingGlobalPreferencesKey() {
        String newKey = "newKey";
        gridPreferencesStore.setPreferenceKey(newKey);

        simpleTable.saveGridPreferences();

        ArgumentCaptor<UserPreference> argumentCaptor = ArgumentCaptor.forClass(UserPreference.class);
        verify(userPreferencesServiceMock).saveUserPreferences(argumentCaptor.capture());

        assertEquals(gridPreferencesStore.getGlobalPreferences().getKey(), argumentCaptor.getValue().getPreferenceKey());
        assertNotEquals(newKey, argumentCaptor.getValue().getPreferenceKey());
        assertEquals(UserPreferencesType.GRIDPREFERENCES, argumentCaptor.getValue().getType());
    }

    @Test
    public void testSaveUserPreferencesUsingPreferencesKey() {
        String newKey = "newKey";
        gridPreferencesStore.setPreferenceKey(newKey);

        simpleTable.saveGridToUserPreferences();

        ArgumentCaptor<UserPreference> argumentCaptor = ArgumentCaptor.forClass(UserPreference.class);
        verify(userPreferencesServiceMock).saveUserPreferences(argumentCaptor.capture());

        assertEquals(newKey, argumentCaptor.getValue().getPreferenceKey());
        assertEquals(UserPreferencesType.GRIDPREFERENCES, argumentCaptor.getValue().getType());
    }
}