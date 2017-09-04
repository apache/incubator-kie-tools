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

package org.kie.workbench.common.forms.editor.client.editor.properties.binding;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public abstract class DataBinderEditorTest<EDITOR extends DataBindingEditor> {

    public static final String FIELD_BINDING = "field";
    public static final String NAME = "name";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String CP = "cp";

    protected List<String> fields = new ArrayList<>();

    @Mock
    protected FieldPropertiesRendererHelper helper;

    @Mock
    protected FieldDefinition fieldDefinition;

    protected EDITOR editor;

    @Before
    public void initTest() {

        fields.add(FIELD_BINDING);
        fields.add(NAME);
        fields.add(LAST_NAME);
        fields.add(ADDRESS);
        fields.add(CITY);
        fields.add(CP);

        when(helper.getCurrentField()).thenReturn(fieldDefinition);
        when(helper.getAvailableModelFields(fieldDefinition)).thenReturn(fields);

        when(fieldDefinition.getBinding()).thenReturn(FIELD_BINDING);
    }
}
