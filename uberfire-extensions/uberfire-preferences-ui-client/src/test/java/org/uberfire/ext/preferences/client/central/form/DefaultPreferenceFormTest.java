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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldOption;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.preferences.shared.PropertyFormOptions;
import org.uberfire.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMock;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMockPortableGeneratedImpl;
import org.uberfire.preferences.shared.impl.validation.NotEmptyValidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultPreferenceFormTest {

    private BasePreferencePortable<PortablePreferenceMock> preference;

    private DefaultPreferenceForm.View formView;

    private DefaultPreferenceForm formPresenter;

    @Before
    public void setup() {
        final TranslationService translationService = mock(TranslationService.class);
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0])
                .when(translationService).format(any(), anyVararg());

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

    @Test
    public void testCreateFieldInfo() {
        fireInitializationEvent("preference-id");

        final PropertyEditorFieldInfo fieldInfo = formPresenter.createFieldInfo("property",
                                                                                PropertyEditorType.TEXT,
                                                                                "some-text");

        assertEquals("property",
                     fieldInfo.getKey());
        assertEquals(1,
                     fieldInfo.getValidators().size());
        assertEquals("propertyBundleKey",
                     fieldInfo.getLabel());
        assertEquals("propertyHelpBundleKey",
                     fieldInfo.getHelpText());
        assertEquals(1,
                     fieldInfo.getOptions().size());
        assertEquals(PropertyEditorFieldOption.DISABLED,
                     fieldInfo.getOptions().get(0));
    }
    
    @Test
    public void testCreateFieldInfoCombo() {
        fireInitializationEvent("preference-id");

        final PropertyEditorFieldInfo fieldInfo = formPresenter.createFieldInfo("property",
                                                                                PropertyEditorType.COMBO,
                                                                                EnumForComboTest.val1);

        assertEquals("property",
                     fieldInfo.getKey());
        assertEquals(1,
                     fieldInfo.getValidators().size());
        assertEquals("propertyBundleKey",
                     fieldInfo.getLabel());
        assertEquals("propertyHelpBundleKey",
                     fieldInfo.getHelpText());
        assertEquals(1,
                     fieldInfo.getOptions().size());
        assertEquals(PropertyEditorFieldOption.DISABLED,
                     fieldInfo.getOptions().get(0));
        
        assertEquals(EnumForComboTest.val1.name(),
                fieldInfo.getCurrentStringValue());
        
        assertEquals(EnumForComboTest.values().length, 
               fieldInfo.getComboValues().size());
        
        assertTrue(fieldInfo.getComboValues()
                .contains(EnumForComboTest.val1.name()));
        assertTrue(fieldInfo.getComboValues()
                .contains(EnumForComboTest.val2.name()));
      
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
        preferenceHierarchyElement.setBundleKey("bundleKey");
        preferenceHierarchyElement.addPropertyBundleKey("property",
                                                        "propertyBundleKey");
        preferenceHierarchyElement.addPropertyHelpBundleKey("property",
                                                            "propertyHelpBundleKey");
        final PropertyFormOptions[] propertyFormOptions = new PropertyFormOptions[1];
        propertyFormOptions[0] = PropertyFormOptions.DISABLED;
        preferenceHierarchyElement.addPropertyFormOptions("property",
                                                          propertyFormOptions);

        HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent(preferenceHierarchyElement);

        formPresenter.hierarchyItemFormInitializationEvent(event);
    }
}
