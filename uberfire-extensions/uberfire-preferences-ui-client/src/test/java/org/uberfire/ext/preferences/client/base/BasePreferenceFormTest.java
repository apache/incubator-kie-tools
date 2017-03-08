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

package org.uberfire.ext.preferences.client.base;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralPreSaveEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralUndoChangesEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;

import static org.mockito.Mockito.*;

public class BasePreferenceFormTest {

    private BasePreferenceForm<Object> basePreferenceForm;

    @Before
    public void setup() {
        basePreferenceForm = spy(getBasePreferenceForm());
        Map<String, String> params = new HashMap<>();
        params.put("id",
                   "preference-id");
        basePreferenceForm.onStartup(new DefaultPlaceRequest("preferenceForm",
                                                             params));
    }

    @Test
    public void initIsCalledWhenFormIdIsRequested() {
        final BasePreferencePortable preference = mock(BasePreferencePortable.class);
        final PreferenceHierarchyElement hierarchyElement = new PreferenceHierarchyElement();
        hierarchyElement.setId("preference-id");
        hierarchyElement.setPortablePreference(preference);

        HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent(hierarchyElement);
        basePreferenceForm.hierarchyItemFormInitializationEvent(event);

        verify(basePreferenceForm).init(preference);
    }

    @Test
    public void initIsNotCalledWhenDifferentFormIdIsRequested() {
        final BasePreferencePortable preference = mock(BasePreferencePortable.class);
        final PreferenceHierarchyElement hierarchyElement = new PreferenceHierarchyElement();
        hierarchyElement.setId("another-preference-id");
        hierarchyElement.setPortablePreference(preference);

        HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent(hierarchyElement);
        basePreferenceForm.hierarchyItemFormInitializationEvent(event);

        verify(basePreferenceForm,
               never()).init(any(BasePreferencePortable.class));
    }

    @Test
    public void beforeSaveIsCalledWhenPreSaveEventIsObserved() {
        PreferencesCentralPreSaveEvent event = new PreferencesCentralPreSaveEvent();
        basePreferenceForm.preSaveEvent(event);

        verify(basePreferenceForm).beforeSave();
    }

    @Test
    public void onUndoIsCalledWhenUndoEventIsObserved() {
        PreferencesCentralUndoChangesEvent event = new PreferencesCentralUndoChangesEvent();
        basePreferenceForm.undoChangesEvent(event);

        verify(basePreferenceForm).onUndo();
    }

    private BasePreferenceForm<Object> getBasePreferenceForm() {
        return new BasePreferenceForm<Object>() {
            @Override
            public void init(final Object preference) {
            }

            @Override
            public void beforeSave() {
            }

            @Override
            public void onUndo() {
            }
        };
    }
}
