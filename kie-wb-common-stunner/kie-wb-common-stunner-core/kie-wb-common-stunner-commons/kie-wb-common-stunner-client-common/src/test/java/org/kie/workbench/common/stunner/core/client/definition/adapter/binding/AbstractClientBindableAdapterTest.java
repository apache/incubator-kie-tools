/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.mockito.Mockito.when;

@PrepareForTest(ClientBindingUtils.class)
public class AbstractClientBindableAdapterTest {

    protected static final String DEFINITION_SET_DESCRIPTION = "DefinitionSet Description";
    protected static final String DEFINITION_DESCRIPTION = "Definition Description";
    protected static final String DEFINITION_TITLE = "Definition Title";
    protected static final String PROPERTY_SET_NAME = "PropertySet Name";
    protected static final String PROPERTY_CAPTION = "Property Caption";
    protected static final String PROPERTY_DESCRIPTION = "Property Description";
    protected static final String PROPERTY_NAME = "name";

    protected Map<PropertyMetaTypes, Class> metaPropertyTypeClasses = new HashMap<>();
    protected Map<Class, Class> baseTypes = new HashMap<>();
    protected Map<Class, Set<String>> propertySetsFieldNames = new HashMap<>();
    protected Map<Class, Set<String>> propertiesFieldNames = new HashMap<>();
    protected Map<Class, Class> propertyGraphFactoryFieldNames = new HashMap<>();
    protected Map<Class, String> propertyIdFieldNames = new HashMap<>();
    protected Map<Class, String> propertyLabelsFieldNames = new HashMap<>();
    protected Map<Class, String> propertyTitleFieldNames = new HashMap<>();
    protected Map<Class, String> propertyCategoryFieldNames = new HashMap<>();
    protected Map<Class, String> propertyDescriptionFieldNames = new HashMap<>();
    protected Map<Class, String> propertyNameFields = new HashMap<>();

    protected Object model;

    @Mock
    protected StunnerTranslationService translationService;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected Object value;

    protected void init() {
        model = new Object();

        when(translationService.getDefinitionSetDescription(model.getClass().getName())).thenReturn(DEFINITION_SET_DESCRIPTION);

        when(translationService.getDefinitionDescription(model.getClass().getName())).thenReturn(DEFINITION_DESCRIPTION);
        when(translationService.getDefinitionTitle(model.getClass().getName())).thenReturn(DEFINITION_TITLE);

        when(translationService.getPropertySetName(model.getClass().getName())).thenReturn(PROPERTY_SET_NAME);

        when(translationService.getPropertyCaption(model.getClass().getName())).thenReturn(PROPERTY_CAPTION);
        when(translationService.getPropertyDescription(model.getClass().getName())).thenReturn(PROPERTY_DESCRIPTION);

        propertiesFieldNames.put(model.getClass(), Stream.of(PROPERTY_NAME).collect(Collectors.toSet()));
        propertyNameFields.put(model.getClass(), PROPERTY_NAME);
        PowerMockito.mockStatic(ClientBindingUtils.class);
        PowerMockito.when(ClientBindingUtils.getProxiedValue(model, PROPERTY_NAME)).thenReturn(value);
    }
}
