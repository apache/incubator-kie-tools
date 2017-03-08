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

package org.uberfire.ext.preferences.client.central.form;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMock;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMockPortableGeneratedImpl;

import static org.mockito.Mockito.*;

public class DefaultPreferenceFormTest {

    private BasePreferencePortable<PortablePreferenceMock> preference;

    private DefaultPreferenceForm.View formView;

    private DefaultPreferenceForm formPresenter;

    @Before
    public void setup() {
        final TranslationService translationService = mock(TranslationService.class);
        preference = spy(new PortablePreferenceMockPortableGeneratedImpl());
        formView = mock(DefaultPreferenceForm.View.class);
        formPresenter = new DefaultPreferenceForm(formView,
                                                  translationService);

        Map<String, String> params = new HashMap<>();
        params.put("id",
                   "preference-id");
        formPresenter.onStartup(new DefaultPlaceRequest("preferenceForm",
                                                        params));
    }

    @Test
    public void testFormShouldBeInitializedWhenEventWithSameIdIsObserved() {
        fireInitializationEvent("preference-id");
        verify(formView).init(formPresenter);
    }

    @Test
    public void testFormShouldNotBeInitializedWhenEventWithDifferentIdIsObserved() {
        fireInitializationEvent("another-preference-id");
        verify(formView,
               never()).init(formPresenter);
    }

    @Test
    public void testPropertyChangedInThisFormShouldSetThePreferenceProperty() {
        fireInitializationEvent("preference-id");
        firePropertyChangedEvent("preference-id");

        verify(preference).set("property",
                               "newValue");
    }

    @Test
    public void testPropertyChangedInAnotherFormShouldNotSetThePreferenceProperty() {
        fireInitializationEvent("preference-id");
        firePropertyChangedEvent("another-preference-id");

        verify(preference,
               never()).set("property",
                            "newValue");
    }

    private void firePropertyChangedEvent(final String eventId) {
        final PropertyEditorFieldInfo propertyInfo = mock(PropertyEditorFieldInfo.class);
        doReturn(eventId).when(propertyInfo).getEventId();
        doReturn("property").when(propertyInfo).getKey();

        formPresenter.propertyChanged(new PropertyEditorChangeEvent(propertyInfo,
                                                                    "newValue"));
    }

    private void fireInitializationEvent(final String eventId) {
        PreferenceHierarchyElement<PortablePreferenceMock> preferenceHierarchyElement = new PreferenceHierarchyElement();
        preferenceHierarchyElement.setId(eventId);
        preferenceHierarchyElement.setPortablePreference(preference);

        HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent(preferenceHierarchyElement);

        formPresenter.hierarchyItemFormInitializationEvent(event);
    }
}
