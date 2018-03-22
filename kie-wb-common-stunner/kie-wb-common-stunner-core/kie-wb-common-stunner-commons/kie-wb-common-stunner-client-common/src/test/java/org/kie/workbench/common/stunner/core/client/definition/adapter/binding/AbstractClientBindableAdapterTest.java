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

import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public class AbstractClientBindableAdapterTest {

    public static final String DEFINITION_SET_DESCRIPTION = "DefinitionSet Description";
    public static final String DEFINITION_DESCRIPTION = "Definition Description";
    public static final String DEFINITION_TITLE = "Definition Title";
    public static final String PROPERTY_SET_NAME = "PropertySet Name";
    public static final String PROPERTY_CAPTION = "Property Caption";
    public static final String PROPERTY_DESCRIPTION = "Property Description";

    protected Object model;

    @Mock
    protected StunnerTranslationService translationService;

    @Mock
    protected DefinitionUtils definitionUtils;

    protected void init() {
        model = new Object();

        when(translationService.getDefinitionSetDescription(model.getClass().getName())).thenReturn(DEFINITION_SET_DESCRIPTION);

        when(translationService.getDefinitionDescription(model.getClass().getName())).thenReturn(DEFINITION_DESCRIPTION);
        when(translationService.getDefinitionTitle(model.getClass().getName())).thenReturn(DEFINITION_TITLE);

        when(translationService.getPropertySetName(model.getClass().getName())).thenReturn(PROPERTY_SET_NAME);

        when(translationService.getPropertyCaption(model.getClass().getName())).thenReturn(PROPERTY_CAPTION);
        when(translationService.getPropertyDescription(model.getClass().getName())).thenReturn(PROPERTY_DESCRIPTION);
    }
}
