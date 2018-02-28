/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.layout.editor.client.widgets.LayoutElementPropertiesPresenter;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;
import org.uberfire.mocks.EventSourceMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutElementPropertiesPresenterTest {

    @Mock
    LayoutEditorCssHelper cssHelper;

    @Mock
    EventSourceMock<LayoutElementPropertyChangedEvent> propertyChangedEvent;

    @Mock
    EventSourceMock<LayoutElementClearAllPropertiesEvent> propertyClearAllEvent;

    @Mock
    LayoutElementPropertiesPresenter.View view;

    @Mock
    LayoutEditorElement layoutElement;

    Map<String,String> elementProps = new HashMap<>();
    PropertyEditorCategory category1 = new PropertyEditorCategory("c1");
    PropertyEditorCategory category2 = new PropertyEditorCategory("c2");
    LayoutElementPropertiesPresenter presenter;

    @Before
    public void initialize() {
        List<PropertyEditorCategory> categories = new ArrayList<>();
        category1 = new PropertyEditorCategory("c1");
        category1.withField(new PropertyEditorFieldInfo("f1", "v1", PropertyEditorType.TEXT).withKey("f1"));
        categories.add(category1);
        category2 = new PropertyEditorCategory("c2");
        category2.withField(new PropertyEditorFieldInfo("f2", "v2", PropertyEditorType.TEXT).withKey("f2"));
        categories.add(category2);

        when(layoutElement.getProperties()).thenReturn(elementProps);
        when(layoutElement.getPropertyCategories()).thenReturn(categories);
        presenter = new LayoutElementPropertiesPresenter(view, cssHelper, propertyChangedEvent, propertyClearAllEvent);
        presenter.edit(layoutElement);
    }

    @Test
    public void testInit() {
        verify(view).clear();
        verify(view).addCategory(category1);
        verify(view).addCategory(category2);
        verify(view).show();

        assertEquals(presenter.getCurrentValues().get("f1"), "v1");
        assertEquals(presenter.getCurrentValues().get("f2"), "v2");
    }

    @Test
    public void testReset() {
        presenter.reset();

        assertTrue(presenter.getCurrentValues().isEmpty());
        verify(layoutElement).removeProperty("f1");
        verify(layoutElement).removeProperty("f2");
        verify(propertyClearAllEvent).fire(any());
    }

    @Test
    public void testChangeValue() {
        assertEquals(presenter.getCurrentValues().get("f1"), "v1");

        presenter.onPropertyChanged("f1", "v2");
        verify(layoutElement).setProperty("f1", "v2");
        verify(propertyChangedEvent).fire(any());
        assertEquals(presenter.getCurrentValues().get("f1"), "v2");
    }

    @Test
    public void testRemoveValue() {
        assertEquals(presenter.getCurrentValues().get("f1"), "v1");

        presenter.onPropertyChanged("f1", "");
        verify(layoutElement).removeProperty("f1");
        verify(propertyChangedEvent).fire(any());
        assertFalse(presenter.getCurrentValues().containsKey("f1"));
    }
}
